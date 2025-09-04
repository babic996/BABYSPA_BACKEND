package com.backend.babyspa.v1.repositories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.babyspa.v1.models.Arrangement;
import com.backend.babyspa.v1.models.Reservation;
import com.backend.babyspa.v1.projections.LocalDateProjection;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    boolean existsByArrangementAndIsDeleted(Arrangement arrangement, boolean isDeleted);

    Optional<Reservation> findFirstByArrangementAndIsDeletedOrderByReservationIdAsc(Arrangement arrangement, boolean isDeleted);

    List<Reservation> findByArrangementAndIsDeleted(Arrangement arrangement, boolean isDeleted);

    @Query(value = "SELECT * FROM reservation r WHERE DATE(r.start_date) = :dayBefore AND r.status_id = :statusId AND r.is_deleted = :isDeleted", nativeQuery = true)
    List<Reservation> findByStartDateAndStatusCode(LocalDateTime dayBefore, int statusId, boolean isDeleted);

    void deleteByArrangement(Arrangement arrangement);

    @Query(value = "SELECT COUNT(r) FROM reservation r WHERE DATE(r.start_date) = :currentDate AND r.status_id = :statusId AND r.is_deleted = :isDeleted", nativeQuery = true)
    int countReservationByStartDateAndStatusId(@Param("currentDate") LocalDate currentDate,
                                               @Param("statusId") int statusId, @Param("isDeleted") boolean isDeleted);

    @Query(value = """
            SELECT
            COUNT(r) AS reservation_count,
            a.baby_id
            FROM
            reservation r
            JOIN
            arrangement a ON r.arrangement_id = a.arrangement_id
            WHERE
            DATE(r.start_date) = :currentDate AND r.status_id = :statusId AND r.tenant_id = :tenantId AND r.is_deleted = :isDeleted
            GROUP BY
             a.baby_id;
            """, nativeQuery = true)
    List<Object[]> countReservationPerBabyAndStatus(@Param("currentDate") LocalDate currentDate,
                                                    @Param("statusId") int statusId, @Param("tenantId") String tenantId, @Param("isDeleted") boolean isDeleted);

    @Query(value = """
            SELECT COALESCE(SUM(counts), 0) AS total_count
            FROM (
            SELECT COUNT(r.*) AS counts
            FROM reservation r
            LEFT JOIN arrangement a ON r.arrangement_id = a.arrangement_id
            WHERE DATE(r.start_date) = :currentDate AND a.service_package_id = :servicePackageId AND r.tenant_id = :tenantId AND r.is_deleted = :isDeleted
            GROUP BY r.arrangement_id
            ) AS subquery
            """, nativeQuery = true)
    int countServicePackageByStartDateAndServicePackageId(@Param("currentDate") LocalDate currentDate,
                                                          @Param("servicePackageId") int servicePackageId, @Param("tenantId") String tenantId, @Param("isDeleted") boolean isDeleted);

    @Query(value = "SELECT DISTINCT DATE(start_date) FROM reservation WHERE start_date < :currentDateTime AND tenant_id = :tenantId AND is_deleted = :isDeleted", nativeQuery = true)
    List<LocalDateProjection> findDistinctReservationDates(@Param("currentDateTime") LocalDateTime currentDateTime,
                                                           @Param("tenantId") String tenantId, @Param("isDeleted") boolean isDeleted);

    List<Reservation> findByTenantIdAndIsDeleted(String tenantId, boolean isDeleted);

    @Query(value = """
            SELECT r.*
            FROM reservation r
            JOIN status s ON r.status_id = s.status_id
            JOIN arrangement a ON r.arrangement_id = a.arrangement_id
            WHERE (:statusId IS NULL OR s.status_id = :statusId)
            AND (:arrangementId IS NULL OR a.arrangement_id = :arrangementId)
            AND (r.start_date >= :startDate AND r.start_date <= :endDate)
            AND (r.tenant_id = :tenantId)
            AND (r.is_deleted = :isDeleted)
            ORDER BY
                 CASE
                     WHEN r.start_date::date = CURRENT_DATE THEN 1
                     WHEN r.start_date > NOW() THEN 2
                     ELSE 3
                 END,
                 CASE
                     WHEN r.start_date > NOW() THEN r.start_date
                     ELSE r.start_date
                 END DESC,
            r.start_date ASC
            """, nativeQuery = true)
    List<Reservation> findAllReservationNativeWithStartDateAndDate(@Param("statusId") Integer statusId, @Param("arrangementId") Integer arrangementId,
                                                                   @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
                                                                   @Param("tenantId") String tenantId, @Param("isDeleted") boolean isDeleted);

    @Query(value = """
            SELECT r.*
            FROM reservation r
            JOIN status s ON r.status_id = s.status_id
            JOIN arrangement a ON r.arrangement_id = a.arrangement_id
            WHERE (:statusId IS NULL OR s.status_id = :statusId)
            AND (:arrangementId IS NULL OR a.arrangement_id = :arrangementId)
            AND (r.tenant_id = :tenantId)
            AND (r.is_deleted = :isDeleted)
            ORDER BY
                 CASE
                     WHEN r.start_date::date = CURRENT_DATE THEN 1
                     WHEN r.start_date > NOW() THEN 2
                     ELSE 3
                 END,
                 CASE
                     WHEN r.start_date > NOW() THEN r.start_date
                     ELSE r.start_date
                 END DESC,
            r.start_date ASC
            """, nativeQuery = true)
    List<Reservation> findAllReservationNative(@Param("statusId") Integer statusId, @Param("arrangementId") Integer arrangementId,
                                               @Param("tenantId") String tenantId, @Param("isDeleted") boolean isDeleted);

}
