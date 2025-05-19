package com.backend.babyspa.v1.repositories;

import com.backend.babyspa.v1.models.Arrangement;
import com.backend.babyspa.v1.models.Discount;
import com.backend.babyspa.v1.models.GiftCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GiftCardRepository extends JpaRepository<GiftCard, Integer> {

    boolean existsBySerialNumberAndTenantId(String serialNumber, String tenantId);

    boolean existsBySerialNumberAndTenantIdAndGiftCardIdNot(String serialNumber, String tenantId, int giftCardId);

    Optional<GiftCard> findBySerialNumberAndTenantId(String serialNumber, String tenantId);

    List<GiftCard> findByUsedAndTenantId(boolean isUsed, String tenantId);

    @Query(value = """
            SELECT gc.*
            FROM gift_card gc
            WHERE (:giftCardId IS NULL OR gc.gift_card_id = :giftCardId)
            AND (:serialNumber IS NULL
                       OR LOWER(serial_number) LIKE LOWER(CONCAT('%', :serialNumber, '%')))
            AND (:isUsed IS NULL OR gc.used = :isUsed)
            AND (gc.tenant_id = :tenantId)
            ORDER BY gc.gift_card_id DESC
            """, nativeQuery = true)
    List<GiftCard> findAllGiftCardNative(
            @Param("serialNumber") String serialNumber,
            @Param("isUsed") Boolean isUsed, @Param("giftCardId") Integer giftCardId,
            @Param("tenantId") String tenantId);

    @Query(value = """
            SELECT gc.*
            FROM gift_card gc
            WHERE (:giftCardId IS NULL OR gc.gift_card_id = :giftCardId)
            AND (:serialNumber IS NULL
                       OR LOWER(serial_number) LIKE LOWER(CONCAT('%', :serialNumber, '%')))
            AND (:isUsed IS NULL OR gc.used = :isUsed)
            AND (gc.expiration_date >= :startDate AND gc.expiration_date <= :endDate)
            AND (gc.tenant_id = :tenantId)
            ORDER BY gc.gift_card_id DESC
            """, nativeQuery = true)
    List<GiftCard> findAllGiftCardNativeWithStartDateAndDate(
            @Param("serialNumber") String serialNumber,
            @Param("isUsed") Boolean isUsed, @Param("giftCardId") Integer giftCardId, @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("tenantId") String tenantId);


}
