package com.backend.babyspa.v1.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.babyspa.v1.models.ReservationDailyReport;
import com.backend.babyspa.v1.models.Status;
import com.backend.babyspa.v1.projections.ReservationDailyReportProjection;

@Repository
public interface ReservationDailyReportRepository extends JpaRepository<ReservationDailyReport, Integer> {

	List<ReservationDailyReport> findByStatus(Status status);

	boolean existsByDateAndTenantId(LocalDate date, String tenantId);

	void deleteByDateAndTenantId(LocalDate date, String tenantId);

	@Query(value = """
			SELECT
			    TO_CHAR(date, 'DD.MM.YYYY.') AS date,
			    SUM(number_of_reservation) AS number_of_reservation
			FROM
			    reservation_daily_report
			WHERE
			    (:statusId IS NULL OR status_id = :statusId) AND (:babyId IS NULL OR baby_id = :babyId)
			GROUP BY
			    date
			ORDER BY
			    MIN(date) ASC
						""", nativeQuery = true)
	List<ReservationDailyReportProjection> findAllByStatusIdAndBabyId(@Param("statusId") Integer statusId,
			@Param("babyId") Integer babyId);

	@Query(value = """
			SELECT
			    TO_CHAR(date, 'DD.MM.YYYY.') AS date,
			    SUM(number_of_reservation) AS number_of_reservation
			FROM
			    reservation_daily_report
			WHERE
			    (:statusId IS NULL OR status_id = :statusId) AND (:babyId IS NULL OR baby_id = :babyId)
			AND (date >= :startDate AND date <= :endDate)
			GROUP BY
			    date
			ORDER BY
			    MIN(date) ASC
						""", nativeQuery = true)
	List<ReservationDailyReportProjection> findAllByStatusIdAndBabyIdAndStartDateAndEndDate(
			@Param("statusId") Integer statusId, @Param("babyId") Integer babyId,
			@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

	@Query(value = """
			SELECT
				TO_CHAR(date, 'Month YYYY') AS date,
				SUM(number_of_reservation) AS number_of_reservation
			FROM
				reservation_daily_report
			WHERE
				(:statusId IS NULL OR status_id = :statusId)
			AND (:babyId IS NULL OR baby_id = :babyId)
			GROUP BY
				TO_CHAR(date, 'Month YYYY')
			ORDER BY
			 	MIN(date) ASC;
						""", nativeQuery = true)
	List<ReservationDailyReportProjection> findAllByStatusIdAndBabyIdGroupByMonth(@Param("statusId") Integer statusId,
			@Param("babyId") Integer babyId);

	@Query(value = """
			SELECT
			    TO_CHAR(date, 'Month YYYY') AS date,
			    SUM(number_of_reservation) AS number_of_reservation
			FROM
			    reservation_daily_report
			WHERE
			    (:statusId IS NULL OR status_id = :statusId) AND (:babyId IS NULL OR baby_id = :babyId)
			AND (date >= :startDate AND date <= :endDate)
			GROUP BY
				TO_CHAR(date, 'Month YYYY')
			ORDER BY
			    MIN(date) ASC
						""", nativeQuery = true)
	List<ReservationDailyReportProjection> findAllByStatusIdAndBabyIdAndStartDateAndEndDateGroupByMonth(
			@Param("statusId") Integer statusId, @Param("babyId") Integer babyId,
			@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

	@Query(value = """
			SELECT
				TO_CHAR(date, 'YYYY') AS date,
				SUM(number_of_reservation) AS number_of_reservation
			FROM
				reservation_daily_report
			WHERE
				(:statusId IS NULL OR status_id = :statusId)
			AND (:babyId IS NULL OR baby_id = :babyId)
			GROUP BY
				TO_CHAR(date, 'YYYY')
			ORDER BY
			 	MIN(date) ASC;
						""", nativeQuery = true)
	List<ReservationDailyReportProjection> findAllByStatusIdAndBabyIdGroupByYear(@Param("statusId") Integer statusId,
			@Param("babyId") Integer babyId);

	@Query(value = """
			SELECT
			    TO_CHAR(date, 'YYYY') AS date,
			    SUM(number_of_reservation) AS number_of_reservation
			FROM
			    reservation_daily_report
			WHERE
			    (:statusId IS NULL OR status_id = :statusId) AND (:babyId IS NULL OR baby_id = :babyId)
			AND (date >= :startDate AND date <= :endDate)
			GROUP BY
				TO_CHAR(date, 'YYYY')
			ORDER BY
			    MIN(date) ASC
						""", nativeQuery = true)
	List<ReservationDailyReportProjection> findAllByStatusIdAndBabyIdAndStartDateAndEndDateGroupByYear(
			@Param("statusId") Integer statusId, @Param("babyId") Integer babyId,
			@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
