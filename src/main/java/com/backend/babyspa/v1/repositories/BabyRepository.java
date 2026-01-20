package com.backend.babyspa.v1.repositories;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.babyspa.v1.models.Baby;

@Repository
public interface BabyRepository
    extends JpaRepository<Baby, Integer>, JpaSpecificationExecutor<Baby> {

  boolean existsByPhoneNumberAndBabyNameAndIsDeleted(
      String phoneNumber, String babyName, boolean isDeleted);

  List<Baby> findByIsDeleted(boolean isDeleted);

  boolean existsByPhoneNumberAndBabyNameAndBabyIdNotAndIsDeleted(
      String phoneNumber, String babyName, int babyId, boolean isDeleted);

  @Modifying
  @Transactional
  @Query(
      value =
          """
            UPDATE baby
            SET number_of_months =
                (EXTRACT(YEAR FROM AGE(CAST(:now AS TIMESTAMP), birth_date)) * 12) +
                EXTRACT(MONTH FROM AGE(CAST(:now AS TIMESTAMP), birth_date)) +
                (CASE WHEN EXTRACT(DAY FROM AGE(CAST(:now AS TIMESTAMP), birth_date)) > 2 THEN 1 ELSE 0 END)
            WHERE birth_date IS NOT NULL AND is_deleted = :isDeleted
            """,
      nativeQuery = true)
  void updateAllNumberOfMonths(
      @Param("now") LocalDateTime now, @Param("isDeleted") boolean isDeleted);
}
