package com.backend.babyspa.v1.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.babyspa.v1.config.TenantContext;
import com.backend.babyspa.v1.dtos.ReservationDailyReportDto;
import com.backend.babyspa.v1.models.ReportSortEnum;
import com.backend.babyspa.v1.models.ReservationDailyReport;
import com.backend.babyspa.v1.projections.ReservationDailyReportProjection;
import com.backend.babyspa.v1.repositories.ReservationDailyReportRepository;
import com.backend.babyspa.v1.utils.DateTimeUtil;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReservationDailyReportService {

  @Autowired private ReservationDailyReportRepository reservationDailyReportRepository;

  @Autowired private BabyService babyService;

  @Transactional
  public ReservationDailyReport save(
      ReservationDailyReportDto reservationDailyReportDto, String tenantId) {

    ReservationDailyReport reservationDailyReport = new ReservationDailyReport();

    if (Objects.nonNull(reservationDailyReportDto.getStatus())) {
      reservationDailyReport.setStatus(reservationDailyReportDto.getStatus());
    }
    reservationDailyReport.setNumberOfReservation(
        reservationDailyReportDto.getNumberOfReservation());
    reservationDailyReport.setDate(reservationDailyReportDto.getDate());
    reservationDailyReport.setBaby(babyService.findById(reservationDailyReportDto.getBabyId()));
    reservationDailyReport.setTenantId(tenantId);

    return reservationDailyReportRepository.save(reservationDailyReport);
  }

  public List<ReservationDailyReportProjection> findAll(
      Integer statusId,
      Integer babyId,
      LocalDateTime startRangeDate,
      LocalDateTime endRangeDate,
      String groupDataType) {

    if (Objects.isNull(startRangeDate) && Objects.nonNull(endRangeDate)) {
      startRangeDate = DateTimeUtil.getDateTimeFromString("1999-01-01 00:00:00");
    } else if (Objects.nonNull(startRangeDate) && Objects.isNull(endRangeDate)) {
      endRangeDate = LocalDateTime.now().plusMinutes(15);
    }

    if (Objects.isNull(startRangeDate) && Objects.isNull(endRangeDate)) {
      if (groupDataType.equals(ReportSortEnum.day.name())) {
        return reservationDailyReportRepository.findAllByStatusIdAndBabyId(
            statusId, babyId, TenantContext.getTenant());
      } else if (groupDataType.equals(ReportSortEnum.month.name())) {
        return reservationDailyReportRepository.findAllByStatusIdAndBabyIdGroupByMonth(
            statusId, babyId, TenantContext.getTenant());
      } else if (groupDataType.equals(ReportSortEnum.year.name())) {
        return reservationDailyReportRepository.findAllByStatusIdAndBabyIdGroupByYear(
            statusId, babyId, TenantContext.getTenant());
      }

    } else {
      if (groupDataType.equals(ReportSortEnum.day.name())) {
        return reservationDailyReportRepository.findAllByStatusIdAndBabyIdAndStartDateAndEndDate(
            statusId,
            babyId,
            startRangeDate.toLocalDate(),
            endRangeDate.toLocalDate(),
            TenantContext.getTenant());
      } else if (groupDataType.equals(ReportSortEnum.month.name())) {
        return reservationDailyReportRepository
            .findAllByStatusIdAndBabyIdAndStartDateAndEndDateGroupByMonth(
                statusId,
                babyId,
                startRangeDate.toLocalDate(),
                endRangeDate.toLocalDate(),
                TenantContext.getTenant());
      } else if (groupDataType.equals(ReportSortEnum.year.name())) {
        return reservationDailyReportRepository
            .findAllByStatusIdAndBabyIdAndStartDateAndEndDateGroupByYear(
                statusId,
                babyId,
                startRangeDate.toLocalDate(),
                endRangeDate.toLocalDate(),
                TenantContext.getTenant());
      }
    }

    return null;
  }

  public boolean existsByDateAndTenantId(LocalDate date, String tenantId) {
    return reservationDailyReportRepository.existsByDate(date);
  }

  @Transactional
  public void deleteAllByTenantId(String tenantId) {
    reservationDailyReportRepository.deleteAll();
  }

  @Transactional
  public void deleteByDateAndTenantId(LocalDate date, String tenantId) {
    reservationDailyReportRepository.deleteByDate(date);
  }
}
