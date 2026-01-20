package com.backend.babyspa.v1.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import com.backend.babyspa.v1.dtos.*;
import com.backend.babyspa.v1.exceptions.BusinessException;
import com.backend.babyspa.v1.projections.LocalDateProjection;
import com.backend.babyspa.v1.utils.DateTimeUtil;
import com.backend.babyspa.v1.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.backend.babyspa.v1.config.TenantContext;
import com.backend.babyspa.v1.exceptions.NotFoundException;
import com.backend.babyspa.v1.models.Arrangement;
import com.backend.babyspa.v1.models.Reservation;
import com.backend.babyspa.v1.models.ServicePackage;
import com.backend.babyspa.v1.models.Status;
import com.backend.babyspa.v1.repositories.ReservationRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationService {

  @Autowired private ReservationRepository reservationRepository;

  @Autowired private ArrangementService arrangementService;

  @Autowired private StatusService statusService;

  @Autowired private ReservationDailyReportService reservationDailyReportService;

  @Autowired private ServicePackageService servicePackageService;

  @Autowired private ServicePackageDailyReportService servicePackageDailyReportService;

  @Autowired private ReservationHistoryStatusService reservationHistoryStatusService;

  @Autowired private SecurityUtil securityUtil;

  private static final String reservationReserved = "term_reserved";
  private static final String reservationUsed = "term_used";
  private static final String reservationCanceled = "term_canceled";
  private static final String reservationStatusType = "reservation";

  public Reservation findById(int reservationId) {
    return reservationRepository
        .findById(reservationId)
        .orElseThrow(
            () ->
                new NotFoundException("Nije pronadjena rezervacija sa id: " + reservationId + "!"));
  }

  public List<ReservationShortInfo> findByArrangementId(int arrangementId) {
    Arrangement arrangement = arrangementService.findById(arrangementId);

    return reservationRepository.findByArrangementAndIsDeleted(arrangement, false).stream()
        .map(
            r ->
                new ReservationShortInfo(
                    r.getStartDate(),
                    r.getEndDate(),
                    r.getStatus().getStatusName(),
                    r.getStatus().getStatusCode()))
        .toList();
  }

  @Transactional
  public ReservationFindAllDto save(CreateReservationDto createReservationDto) {
    Arrangement arrangement = arrangementService.findById(createReservationDto.getArrangementId());
    Status status = statusService.findByStatusCode(reservationReserved);

    if (arrangement.getRemainingTerm() == 0) {
      throw new BusinessException(
          "Nije moguće napraviti rezervaciju jer je iskorišten maksimalan broj termina!");
    }

    if (reservationRepository.existsByArrangementAndIsDeleted(arrangement, false)) {
      Reservation firstReservation =
          reservationRepository
              .findFirstByArrangementAndIsDeletedOrderByReservationIdAsc(arrangement, false)
              .orElseThrow(
                  () ->
                      new NotFoundException(
                          "Nije pronađena prva rezervacija za aranžman čiji je Id: "
                              + arrangement.getArrangementId()
                              + "!"));
      if ((firstReservation
          .getStartDate()
          .plusDays(
              arrangement.getServicePackage().getServicePackageDurationDays()
                  + (Objects.nonNull(arrangement.getExtendDurationDays())
                      ? arrangement.getExtendDurationDays()
                      : 0))
          .isBefore(createReservationDto.getStartDate()))) {
        throw new BusinessException(
            "Nije moguće napraviti rezervaciju jer je broj dana koliko traje paket istekao!");
      }
    }

    Reservation reservation = new Reservation();

    reservation.setArrangement(arrangement);
    reservation.setStartDate(createReservationDto.getStartDate());
    reservation.setEndDate(
        createReservationDto
            .getStartDate()
            .plusMinutes(createReservationDto.getDurationReservation()));
    reservation.setStatus(status);
    reservation.setNote(createReservationDto.getNote());
    reservation.setCreatedByUser(securityUtil.getCurrentUser());
    arrangementService.decreaseRemainingTerm(arrangement);

    reservationRepository.save(reservation);

    return buildReservationFindAllDtoFromReservation(reservation);
  }

  @Transactional
  public ReservationFindAllDto update(UpdateReservationDto updateReservationDto) {
    Status status = statusService.findById(updateReservationDto.getStatusId());
    Reservation reservation = findById(updateReservationDto.getReservationId());
    Status statusBeforeUpdate = reservation.getStatus();

    if (reservation.getStatus().getStatusCode().equals(reservationCanceled)
        && reservation.getArrangement().getRemainingTerm() == 0
        && !status.getStatusCode().equals(reservationCanceled)) {
      throw new BusinessException(
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
    reservationHistoryStatusService.save(
        reservation, statusBeforeUpdate, securityUtil.getCurrentUser());

    return buildReservationFindAllDtoFromReservation(reservation);
  }

  @Transactional
  public int delete(int reservationId) {
    Reservation reservation = findById(reservationId);
    LocalDateTime currentDateTime = LocalDateTime.now();

    //        if (!reservation.getStartDate().isAfter(currentDateTime)) {
    //            throw new BuisnessException("Nije moguće izbrisati rezervaciju koja je već
    // završena.");
    //        }

    if (!reservation.getStatus().getStatusCode().equals(reservationCanceled)) {
      arrangementService.increaseRemainingTerm(reservation.getArrangement());
    }

    reservation.setDeleted(true);
    reservation.setDeletedByUser(securityUtil.getCurrentUser());
    reservation.setDeletedAt(LocalDateTime.now());

    reservationRepository.save(reservation);

    return reservationId;
  }

  public int reservationCanceled(int reservationId) {
    Reservation reservation = findById(reservationId);
    Status status = statusService.findByStatusCode(reservationCanceled);
    Status statusBeforeUpdate = reservation.getStatus();

    arrangementService.increaseRemainingTerm(reservation.getArrangement());
    reservation.setStatus(status);
    reservationRepository.save(reservation);
    reservationHistoryStatusService.save(
        reservation, statusBeforeUpdate, securityUtil.getCurrentUser());

    return reservationId;
  }

  public List<ReservationFindAllDto> findAllList() {
    return reservationRepository.findByIsDeleted(false).stream()
        .map(this::buildReservationFindAllDtoFromReservation)
        .toList();
  }

  public Page<ReservationFindAllTableDto> findAll(
      int page,
      int size,
      Integer statusId,
      Integer arrangementId,
      LocalDateTime startDate,
      LocalDateTime endDate) {
    List<ReservationFindAllTableDto> reservationFindAllDtos;
    List<Reservation> reservations;

    if (Objects.isNull(startDate) && Objects.nonNull(endDate)) {
      startDate = DateTimeUtil.getDateTimeFromString("1999-01-01 00:00:00");
    } else if (Objects.nonNull(startDate) && Objects.isNull(endDate)) {
      endDate = LocalDateTime.now().plusMinutes(15);
    }

    if (Objects.isNull(startDate) && Objects.isNull(endDate)) {
      reservations =
          reservationRepository.findAllReservationNative(
              statusId, arrangementId, TenantContext.getTenant(), false);
    } else {
      reservations =
          reservationRepository.findAllReservationNativeWithStartDateAndDate(
              statusId, arrangementId, startDate, endDate, TenantContext.getTenant(), false);
    }

    reservationFindAllDtos =
        reservations.stream().map(this::buildReservationFindAllTableDtoFromReservation).toList();

    Pageable pageable = PageRequest.of(page, size);
    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), reservationFindAllDtos.size());
    if (start > end) {
      start = end = 0;
    }

    final Page<ReservationFindAllTableDto> pageItem =
        new PageImpl<>(
            reservationFindAllDtos.subList(start, end), pageable, reservationFindAllDtos.size());

    return pageItem;
  }

  public List<Reservation> findAllByArrangementId(int arrangementId) {
    Arrangement arrangement = arrangementService.findById(arrangementId);
    return reservationRepository.findByArrangementAndIsDeleted(arrangement, false);
  }

  @Transactional
  public void generateReservationReport(LocalDate date, String tenantId) {
    List<Status> statuses = statusService.findAllByStatusTypeCode(reservationStatusType);

    if (!statuses.isEmpty()) {
      statuses.forEach(
          status -> {
            List<Object[]> usegesPerBaby =
                reservationRepository.countReservationPerBabyAndStatus(
                    date, status.getStatusId(), tenantId, false);
            if (Objects.nonNull(usegesPerBaby)) {
              usegesPerBaby.forEach(
                  useges -> {
                    ReservationDailyReportDto reservationDailyReportDto =
                        new ReservationDailyReportDto();
                    reservationDailyReportDto.setDate(date);
                    reservationDailyReportDto.setBabyId(((Number) useges[1]).intValue());
                    reservationDailyReportDto.setNumberOfReservation(
                        ((Number) useges[0]).intValue());
                    reservationDailyReportDto.setStatus(status);
                    reservationDailyReportService.save(reservationDailyReportDto, tenantId);
                  });
            }
          });
    }
  }

  @Transactional
  public void updateReservationWithStatusCreatedToStatusUsed() {
    LocalDateTime dayBefore = LocalDateTime.now().minusDays(1);
    Status statusReservationReserved = statusService.findByStatusCode(reservationReserved);
    Status statusReservationUsed = statusService.findByStatusCode(reservationUsed);

    reservationRepository
        .findByStartDateAndStatusCode(dayBefore, statusReservationReserved.getStatusId(), false)
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
      servicePackages.forEach(
          servicePackage -> {
            ServicePackageDailyReportDto servicePackageDailyReportDto =
                new ServicePackageDailyReportDto();
            int usedPackages =
                reservationRepository.countServicePackageByStartDateAndServicePackageId(
                    date, servicePackage.getServicePackageId(), tenantId, false);
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

  private ReservationFindAllTableDto buildReservationFindAllTableDtoFromReservation(
      Reservation reservation) {
    ReservationFindAllTableDto reservationFindAllTableDto = new ReservationFindAllTableDto();

    reservationFindAllTableDto.setReservationId(reservation.getReservationId());
    reservationFindAllTableDto.setArrangementId(reservation.getArrangement().getArrangementId());
    reservationFindAllTableDto.setBabyDetails(
        new ShortDetailsDto(
            reservation.getArrangement().getBaby().getBabyId(),
            reservation.getArrangement().getBaby().getBabyName()
                + (Objects.nonNull(reservation.getArrangement().getBaby().getBabySurname())
                    ? " " + reservation.getArrangement().getBaby().getBabySurname()
                    : "")
                + " ("
                + reservation.getArrangement().getBaby().getPhoneNumber()
                + ")"));
    reservationFindAllTableDto.setRemainingTerm(reservation.getArrangement().getRemainingTerm());
    reservationFindAllTableDto.setStatus(reservation.getStatus());
    reservationFindAllTableDto.setStartDate(reservation.getStartDate());
    reservationFindAllTableDto.setEndDate(reservation.getEndDate());
    reservationFindAllTableDto.setCreatedAt(reservation.getCreatedAt());
    reservationFindAllTableDto.setServicePackageName(
        reservation.getArrangement().getServicePackage().getServicePackageName());
    reservationFindAllTableDto.setNote(reservation.getNote());

    return reservationFindAllTableDto;
  }

  public boolean existingByArrangement(int arrangementId) {
    Arrangement arrangement = arrangementService.findById(arrangementId);

    return reservationRepository.existsByArrangementAndIsDeleted(arrangement, false);
  }

  @Transactional
  public void generateReportForAllDateInReservation(
      boolean generateForAllDays, LocalDate date, String tenantId) {
    if (generateForAllDays) {
      reservationDailyReportService.deleteAllByTenantId(tenantId);
      servicePackageDailyReportService.deleteAllByTenantId(tenantId);

      LocalDate currentDate = LocalDate.now();
      List<LocalDate> allDatesFromReservation =
          reservationRepository
              .findDistinctReservationDates(currentDate.atStartOfDay(), tenantId, false)
              .stream()
              .map(LocalDateProjection::getDate)
              .toList();

      allDatesFromReservation.forEach(
          x -> {
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
