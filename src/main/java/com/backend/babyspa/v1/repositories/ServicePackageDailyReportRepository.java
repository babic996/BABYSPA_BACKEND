package com.backend.babyspa.v1.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.babyspa.v1.models.ServicePackageDailyReport;
import com.backend.babyspa.v1.projections.ServicePackagesDailyReportProjection;

@Repository
public interface ServicePackageDailyReportRepository extends JpaRepository<ServicePackageDailyReport, Integer> {

	List<ServicePackageDailyReport> findAllByServicePackageIsNull();

    boolean existsByDate(LocalDate date);

    void deleteByDate(LocalDate date);

    void deleteAll();

	@Query(value = """
			SELECT
			    TO_CHAR(date, 'DD.MM.YYYY.') AS date,
			    SUM(number_of_used_packages) AS number_of_used_packages
			FROM
			    service_package_daily_report
			WHERE
			    (:servicePackageId IS NULL OR service_package_id = :servicePackageId)
			AND (tenant_id = :tenantId)
			GROUP BY
			    date
			ORDER BY
			    MIN(date) ASC
			""", nativeQuery = true)
	List<ServicePackagesDailyReportProjection> findAllByServicePackageId(
			@Param("servicePackageId") Integer servicePackageId, @Param("tenantId") String tenantId);

	@Query(value = """
			SELECT
			    TO_CHAR(date, 'DD.MM.YYYY.') AS date,
			    SUM(number_of_used_packages) AS number_of_used_packages
			FROM
			    service_package_daily_report
			WHERE
			    (:servicePackageId IS NULL OR service_package_id = :servicePackageId) AND (date >= :startDate AND date <= :endDate)
			AND (tenant_id = :tenantId)
			GROUP BY
			    date
			ORDER BY
			    MIN(date) ASC
			""", nativeQuery = true)
	List<ServicePackagesDailyReportProjection> findAllByServicePackageIdAndStartDateAndEndDate(
			@Param("servicePackageId") Integer servicePackageId, @Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate, @Param("tenantId") String tenantId);

	@Query(value = """
			SELECT
			    TO_CHAR(date, 'Month YYYY') AS date,
			    SUM(number_of_used_packages) AS number_of_used_packages
			FROM
			    service_package_daily_report
			WHERE
			    (:servicePackageId IS NULL OR service_package_id = :servicePackageId) AND (tenant_id = :tenantId)
			GROUP BY
			    TO_CHAR(date, 'Month YYYY')
			ORDER BY
			    MIN(date) ASC
			""", nativeQuery = true)
	List<ServicePackagesDailyReportProjection> findAllByServicePackageIdGroupByMonth(
			@Param("servicePackageId") Integer servicePackageId, @Param("tenantId") String tenantId);

	@Query(value = """
			SELECT
			    TO_CHAR(date, 'Month YYYY') AS date,
			    SUM(number_of_used_packages) AS number_of_used_packages
			FROM
			    service_package_daily_report
			WHERE
			    (:servicePackageId IS NULL OR service_package_id = :servicePackageId) AND (date >= :startDate AND date <= :endDate)
			AND (tenant_id = :tenantId)
			GROUP BY
			    TO_CHAR(date, 'Month YYYY')
			ORDER BY
			    MIN(date) ASC
			""", nativeQuery = true)
	List<ServicePackagesDailyReportProjection> findAllByServicePackageIdAndStartDateAndEndDateGroupByMonth(
			@Param("servicePackageId") Integer servicePackageId, @Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate, @Param("tenantId") String tenantId);

	@Query(value = """
			SELECT
			    TO_CHAR(date, 'YYYY') AS date,
			    SUM(number_of_used_packages) AS number_of_used_packages
			FROM
			    service_package_daily_report
			WHERE
			    (:servicePackageId IS NULL OR service_package_id = :servicePackageId)
			AND (tenant_id = :tenantId)
			GROUP BY
			    TO_CHAR(date, 'YYYY')
			ORDER BY
			    MIN(date) ASC
			""", nativeQuery = true)
	List<ServicePackagesDailyReportProjection> findAllByServicePackageIdGroupByYear(
			@Param("servicePackageId") Integer servicePackageId, @Param("tenantId") String tenantId);

	@Query(value = """
			SELECT
			    TO_CHAR(date, 'YYYY') AS date,
			    SUM(number_of_used_packages) AS number_of_used_packages
			FROM
			    service_package_daily_report
			WHERE
			    (:servicePackageId IS NULL OR service_package_id = :servicePackageId) AND (date >= :startDate AND date <= :endDate)
			AND (tenant_id = :tenantId)
			GROUP BY
			    TO_CHAR(date, 'YYYY')
			ORDER BY
			    MIN(date) ASC
			""", nativeQuery = true)
	List<ServicePackagesDailyReportProjection> findAllByServicePackageIdAndStartDateAndEndDateGroupByYear(
			@Param("servicePackageId") Integer servicePackageId, @Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate, @Param("tenantId") String tenantId);

}
