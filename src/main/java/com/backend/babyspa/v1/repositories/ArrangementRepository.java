package com.backend.babyspa.v1.repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.backend.babyspa.v1.models.GiftCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.babyspa.v1.models.Arrangement;
import com.backend.babyspa.v1.models.Baby;
import com.backend.babyspa.v1.models.ServicePackage;

@Repository
public interface ArrangementRepository
    extends JpaRepository<Arrangement, Integer>, JpaSpecificationExecutor<Arrangement> {

  List<Arrangement> findByRemainingTermGreaterThanAndIsDeleted(
      int remainingTerm, boolean isDeleted);

  boolean existsByServicePackageAndIsDeleted(ServicePackage servicePackage, boolean isDeleted);

  boolean existsByBabyAndIsDeleted(Baby baby, boolean isDeleted);

  boolean existsByGiftCardAndIsDeleted(GiftCard giftCard, boolean isDeleted);

  @Query(
      value =
          """
        SELECT COALESCE(SUM(a.price), 0)
        FROM arrangement a
        WHERE a.tenant_id = :tenantId
        AND a.is_deleted = :isDeleted
        AND (CAST(:statusId AS INTEGER) IS NULL OR a.status_id = CAST(:statusId AS INTEGER))
        AND (CAST(:babyId AS INTEGER) IS NULL OR a.baby_id = CAST(:babyId AS INTEGER))
        AND (CAST(:arrangementId AS INTEGER) IS NULL OR a.arrangement_id = CAST(:arrangementId AS INTEGER))
        AND (CAST(:paymentTypeId AS INTEGER) IS NULL OR a.payment_type_id = CAST(:paymentTypeId AS INTEGER))
        AND (CAST(:giftCardId AS INTEGER) IS NULL OR a.gift_card_id = CAST(:giftCardId AS INTEGER))
        AND (CAST(:servicePackageId AS INTEGER) IS NULL OR a.service_package_id = CAST(:servicePackageId AS INTEGER))
        AND (CAST(:remainingTerm AS INTEGER) IS NULL OR a.remaining_term = CAST(:remainingTerm AS INTEGER))
        AND (CAST(:startPrice AS NUMERIC) IS NULL OR a.price >= CAST(:startPrice AS NUMERIC))
        AND (CAST(:endPrice AS NUMERIC) IS NULL OR a.price <= CAST(:endPrice AS NUMERIC))
        AND (CAST(:startDate AS TIMESTAMP) IS NULL OR a.created_at >= CAST(:startDate AS TIMESTAMP))
        AND (CAST(:endDate AS TIMESTAMP) IS NULL OR a.created_at <= CAST(:endDate AS TIMESTAMP))
    """,
      nativeQuery = true)
  BigDecimal findTotalPriceSum(
      @Param("statusId") Integer statusId,
      @Param("babyId") Integer babyId,
      @Param("paymentTypeId") Integer paymentTypeId,
      @Param("giftCardId") Integer giftCardId,
      @Param("startPrice") BigDecimal startPrice,
      @Param("endPrice") BigDecimal endPrice,
      @Param("remainingTerm") Integer remainingTerm,
      @Param("servicePackageId") Integer servicePackageId,
      @Param("arrangementId") Integer arrangementId,
      @Param("startDate") LocalDateTime startDate,
      @Param("endDate") LocalDateTime endDate,
      @Param("tenantId") String tenantId,
      @Param("isDeleted") boolean isDeleted);
}
