package com.backend.babyspa.v1.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.backend.babyspa.v1.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.babyspa.v1.config.TenantContext;
import com.backend.babyspa.v1.dtos.CreateReservationDto;
import com.backend.babyspa.v1.dtos.ReservationDailyReportDto;
import com.backend.babyspa.v1.dtos.ReservationFindAllDto;
import com.backend.babyspa.v1.dtos.ReservationShortInfo;
import com.backend.babyspa.v1.dtos.ServicePackageDailyReportDto;
import com.backend.babyspa.v1.dtos.UpdateReservationDto;
import com.backend.babyspa.v1.exceptions.NotFoundException;
import com.backend.babyspa.v1.models.Arrangement;
import com.backend.babyspa.v1.models.Reservation;
import com.backend.babyspa.v1.models.ServicePackage;
import com.backend.babyspa.v1.models.Status;
import com.backend.babyspa.v1.repositories.ReservationRepository;

import jakarta.transaction.Transactional;

@Service
public class ReservationService {

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    ArrangementService arrangementService;

    @Autowired
    StatusService statusService;

    @Autowired
    ReservationDailyReportService reservationDailyReportService;

    @Autowired
    ServicePackageService servicePackageService;

    @Autowired
    ServicePackageDailyReportService servicePackageDailyReportService;

    @Autowired
    ReservationHistoryStatusService reservationHistoryStatusService;

    @Autowired
    SecurityUtil securityUtil;

    private final String reservationReserved = "term_reserved";
    private final String reservationUsed = "term_used";
    private final String reservationCanceled = "term_canceled";
    private final String reservationStatusType = "reservation";

    public Reservation findById(int reservationId) throws NotFoundException {

        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NotFoundException("Nije pronadjena rezervacija sa id: " + reservationId + "!"));
    }

    public List<ReservationShortInfo> findByArrangementId(int arrangementId) throws NotFoundException {

        Arrangement arrangement = arrangementService.findById(arrangementId);
        List<ReservationShortInfo> reservationShortInfoList = reservationRepository.findByArrangementAndIsDeleted(arrangement, false)
                .stream()
                .map(x -> new ReservationShortInfo(x.getStartDate(), x.getEndDate(), x.getStatus().getStatusName()))
                .collect(Collectors.toList());

        return reservationShortInfoList;

    }

    @Transactional(rollbackOn = Exception.class)
    public ReservationFindAllDto save(CreateReservationDto createReservationDto) throws Exception {

        Arrangement arrangement = arrangementService.findById(createReservationDto.getArrangementId());
        Status status = statusService.findByStatusCode(reservationReserved);
        Reservation reservation = new Reservation();

        if (arrangement.getRemainingTerm() == 0) {
            throw new Exception("Nije moguće napraviti rezervaciju jer je iskorišten maksimalan broj termina!");
        }

        if (reservationRepository.existsByArrangementAndIsDeleted(arrangement, false)) {
            Reservation firstReservation = reservationRepository
                    .findFirstByArrangementAndIsDeletedOrderByReservationIdAsc(arrangement, false)
                    .orElseThrow(() -> new Exception("Nije pronađena prva rezervacija za aranžman čiji je Id: "
                            + arrangement.getArrangementId() + "!"));
            if ((firstReservation.getStartDate().plusDays(arrangement.getServicePackage()
                            .getServicePackageDurationDays()
                            + (Objects.nonNull(arrangement.getExtendDurationDays()) ? arrangement.getExtendDurationDays() : 0))
                    .isBefore(createReservationDto.getStartDate()))) {
                throw new Exception("Nije moguće napraviti rezervaciju jer je broj dana koliko traje paket istekao!");
            }
        }

        reservation.setArrangement(arrangement);
        reservation.setStartDate(createReservationDto.getStartDate());
        reservation.setEndDate(
                createReservationDto.getStartDate().plusMinutes(createReservationDto.getDurationReservation()));
        reservation.setStatus(status);
        reservation.setNote(createReservationDto.getNote());
        reservation.setCreatedByUser(securityUtil.getCurrentUser());
        arrangementService.decreaseRemainingTerm(arrangement);

        reservationRepository.save(reservation);

        return buildReservationFindAllDtoFromReservation(reservation);
    }

    @Transactional(rollbackOn = Exception.class)
    public ReservationFindAllDto update(UpdateReservationDto updateReservationDto) throws Exception {

        Status status = statusService.findById(updateReservationDto.getStatusId());
        Reservation reservation = findById(updateReservationDto.getReservationId());
        Status statusBeforeUpdate = reservation.getStatus();

        if (reservation.getStatus().getStatusCode().equals(reservationCanceled)
                && reservation.getArrangement().getRemainingTerm() == 0
                && !status.getStatusCode().equals(reservationCanceled)) {
            throw new NotFoundException(
                    "Nije moguće ažurirati rezervaciju jer bi broj preostalih termina aranžmana bio manji od 0!");
        } else {
            if (!reservation.getStatus().getStatusCode().equals(reservationCanceled)
                    && status.getStatusCode().equals(reservationCanceled)) {
                arrangementService.increaseRemainingTerm(reservation.getArrangement());
            }

            if (reservation.getStatus().getStatusCode().equals(reservationCanceled)
                    && !status.getStatusCode().equals(reservationCanceled)) {
                arrangementService.decreaseRemainingTerm(reservation.getArrangement());
            }
            reservation.setStatus(status);
        }

        reservation.setNote(updateReservationDto.getNote());
        reservation.setUpdatedByUser(securityUtil.getCurrentUser());

        reservationRepository.save(reservation);
        reservationHistoryStatusService.save(reservation, statusBeforeUpdate, securityUtil.getCurrentUser());

        return buildReservationFindAllDtoFromReservation(reservation);
    }

    @Transactional(rollbackOn = NotFoundException.class)
    public int delete(int reservationId) throws NotFoundException {

        Reservation reservation = findById(reservationId);

        arrangementService.increaseRemainingTerm(reservation.getArrangement());
        reservation.setDeleted(true);
        reservation.setDeletedByUser(securityUtil.getCurrentUser());
        reservation.setDeletedAt(LocalDateTime.now());

        reservationRepository.save(reservation);

        return reservationId;
    }

    public int reservationCanceled(int reservationId) throws NotFoundException {

        Reservation reservation = findById(reservationId);
        Status status = statusService.findByStatusCode(reservationCanceled);
        Status statusBeforeUpdate = reservation.getStatus();

        arrangementService.increaseRemainingTerm(reservation.getArrangement());
        reservation.setStatus(status);
        reservationRepository.save(reservation);
        reservationHistoryStatusService.save(reservation, statusBeforeUpdate, securityUtil.getCurrentUser());

        return reservationId;
    }

    public List<ReservationFindAllDto> findAllList() {

        return reservationRepository.findByTenantIdAndIsDeleted(TenantContext.getTenant(), false).stream()
                .map(x -> buildReservationFindAllDtoFromReservation(x)).collect(Collectors.toList());
    }

    public List<Reservation> findAllByArrangementId(int arrangementId) {

        Arrangement arrangement = arrangementService.findById(arrangementId);

        return reservationRepository.findByArrangementAndIsDeleted(arrangement, false);
    }

    @Transactional
    public void generateReservationReport(LocalDate date, String tenantId) {
        List<Status> statuses = statusService.findAllByStatusTypeCode(reservationStatusType);

        if (!statuses.isEmpty()) {
            statuses.forEach(status -> {
                List<Object[]> usegesPerBaby = reservationRepository.countReservationPerBabyAndStatus(date,
                        status.getStatusId(), tenantId, false);
                if (Objects.nonNull(usegesPerBaby)) {
                    usegesPerBaby.forEach(useges -> {
                        ReservationDailyReportDto reservationDailyReportDto = new ReservationDailyReportDto();
                        reservationDailyReportDto.setDate(date);
                        reservationDailyReportDto.setBabyId(((Number) useges[1]).intValue());
                        reservationDailyReportDto.setNumberOfReservation(((Number) useges[0]).intValue());
                        reservationDailyReportDto.setStatus(status);
                        reservationDailyReportService.save(reservationDailyReportDto, tenantId);
                    });
                }
            });
        }

    }

    @Transactional(rollbackOn = NotFoundException.class)
    public void updateReservationWithStatusCreatedToStatusUsed() {

        LocalDateTime dayBefore = LocalDateTime.now().minusDays(1);
        Status statusReservationReserved = statusService.findByStatusCode(reservationReserved);
        Status statusReservationUsed = statusService.findByStatusCode(reservationUsed);

        reservationRepository.findByStartDateAndStatusCode(dayBefore, statusReservationReserved.getStatusId(), false).stream()
                .forEach(reservation -> updateReservationStatus(reservation, statusReservationUsed));
    }

    private void updateReservationStatus(Reservation reservation, Status status) {
        reservation.setStatus(status);
        reservationRepository.save(reservation);
    }

    @Transactional
    public void generateServicePackageReport(LocalDate date, String tenantId) {
        List<ServicePackage> servicePackages = servicePackageService.findAllByTenantForReport(tenantId);

        if (!servicePackages.isEmpty()) {
            servicePackages.forEach(servicePackage -> {
                ServicePackageDailyReportDto servicePackageDailyReportDto = new ServicePackageDailyReportDto();
                int usedPackages = reservationRepository.countServicePackageByStartDateAndServicePackageId(date,
                        servicePackage.getServicePackageId(), tenantId, false);
                servicePackageDailyReportDto.setNumberOfUsedPackages(usedPackages);
                servicePackageDailyReportDto.setDate(date);
                servicePackageDailyReportDto.setServicePackage(servicePackage);
                servicePackageDailyReportService.save(servicePackageDailyReportDto, tenantId);
            });
        }
    }

    private ReservationFindAllDto buildReservationFindAllDtoFromReservation(Reservation reservation) {

        ReservationFindAllDto reservationFindAllDto = new ReservationFindAllDto();

        reservationFindAllDto.setReservationId(reservation.getReservationId());
        reservationFindAllDto.setStatus(reservation.getStatus());
        reservationFindAllDto.setEndDate(reservation.getEndDate());
        reservationFindAllDto.setStartDate(reservation.getStartDate());
        reservationFindAllDto.setCreatedAt(reservation.getCreatedAt());
        reservationFindAllDto.setNote(reservation.getNote());
        reservationFindAllDto.setArrangement(
                arrangementService.buildFindAllArrangementDtoFromArrangement(reservation.getArrangement()));

        return reservationFindAllDto;
    }

    public boolean existingByArrangement(int arrangementId) {

        Arrangement arrangement = arrangementService.findById(arrangementId);

        return reservationRepository.existsByArrangementAndIsDeleted(arrangement, false);
    }

    @Transactional
    public void generateReportForAllDateInReservation(boolean generateForAllDays, LocalDate date, String tenantId) {

        if (generateForAllDays) {
            reservationDailyReportService.deleteAll();
            servicePackageDailyReportService.deleteAll();

            LocalDate currentDate = LocalDate.now();
            List<LocalDate> allDatesFromReservation = reservationRepository
                    .findDistinctReservationDates(currentDate.atStartOfDay(), tenantId, false).stream().map(x -> x.getDate())
                    .collect(Collectors.toList());

            allDatesFromReservation.forEach(x -> {
                generateReservationReport(x, tenantId);
                generateServicePackageReport(x, tenantId);
            });
        } else {
            if (reservationDailyReportService.existsByDateAndTenantId(date, tenantId)) {
                reservationDailyReportService.deleteByDateAndTenantId(date, tenantId);
            }
            if (servicePackageDailyReportService.existsByDateAndTenantId(date, tenantId)) {
                servicePackageDailyReportService.deleteByDateAndTenantId(date, tenantId);
            }

            generateServicePackageReport(date, tenantId);
            generateReservationReport(date, tenantId);
        }
    }
}
