package com.backend.babyspa.v1.repositories;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.babyspa.v1.models.Baby;

@Repository
public interface BabyRepository extends JpaRepository<Baby, Integer> {

    boolean existsByPhoneNumberAndBabyNameAndTenantIdAndIsDeleted(String phoneNumber, String babyName, String tenantId, boolean isDeleted);

    List<Baby> findByTenantIdAndIsDeleted(String tenantId, boolean isDeleted);

    boolean existsByPhoneNumberAndBabyNameAndTenantIdAndBabyIdNotAndIsDeleted(String phoneNumber, String babyName, String tenantId,
                                                                              int babyId, boolean isDeleted);

    @Query(value = """
                SELECT *
                FROM baby
                WHERE (:searchText IS NULL
                       OR LOWER(baby_name) LIKE LOWER(CONCAT('%', :searchText, '%'))
                       OR LOWER(baby_surname) LIKE LOWER(CONCAT('%', :searchText, '%'))
                       OR REPLACE(phone_number, '+', '') LIKE CONCAT('%', REPLACE(:searchText, '+', ''), '%'))
                  AND (birth_date >= :startDate AND birth_date <= :endDate)
                  AND tenant_id = :tenantId
                  AND is_deleted = :isDeleted
                ORDER BY baby_id DESC
            """, nativeQuery = true)
    Page<Baby> findAllNativeWithDate(@Param("searchText") String searchText,
                                     @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate,
                                     @Param("tenantId") String tenantId, @Param("isDeleted") boolean isDeleted, Pageable pageable);

    @Query(value = """
                SELECT *
                FROM baby
                WHERE (:searchText IS NULL
                       OR LOWER(baby_name) LIKE LOWER(CONCAT('%', :searchText, '%'))
                       OR LOWER(baby_surname) LIKE LOWER(CONCAT('%', :searchText, '%'))
                       OR REPLACE(phone_number, '+', '') LIKE CONCAT('%', REPLACE(:searchText, '+', ''), '%'))
                AND tenant_id = :tenantId
                AND is_deleted = :isDeleted
                ORDER BY baby_id DESC
            """, nativeQuery = true)
    Page<Baby> findAllNativeWithoutDate(@Param("searchText") String searchText, @Param("tenantId") String tenantId, @Param("isDeleted") boolean isDeleted,
                                        Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = """
            UPDATE baby
            SET number_of_months =
                (EXTRACT(YEAR FROM AGE(CAST(:now AS TIMESTAMP), birth_date)) * 12) +
                EXTRACT(MONTH FROM AGE(CAST(:now AS TIMESTAMP), birth_date)) +
                (CASE WHEN EXTRACT(DAY FROM AGE(CAST(:now AS TIMESTAMP), birth_date)) > 2 THEN 1 ELSE 0 END)
            WHERE birth_date IS NOT NULL AND is_deleted = :isDeleted
            """, nativeQuery = true)
    void updateAllNumberOfMonths(@Param("now") LocalDateTime now, @Param("isDeleted") boolean isDeleted);

}
