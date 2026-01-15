package com.backend.babyspa.v1.repositories;

import com.backend.babyspa.v1.models.GiftCard;
import com.backend.babyspa.v1.projections.FindAllGiftCardDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GiftCardRepository extends JpaRepository<GiftCard, Integer> {

    boolean existsBySerialNumber(String serialNumber);

    boolean existsBySerialNumberAndGiftCardIdNot(String serialNumber, int giftCardId);

    Optional<GiftCard> findBySerialNumber(String serialNumber);

    List<GiftCard> findByUsed(boolean isUsed);

    @Query(value = """
            SELECT gc.*, a.arrangement_id, b.phone_number
            FROM gift_card gc
            LEFT JOIN arrangement a ON gc.gift_card_id = a.gift_card_id
            LEFT JOIN baby b ON a.baby_id = b.baby_id
            WHERE (:giftCardId IS NULL OR gc.gift_card_id = :giftCardId)
            AND (:serialNumber IS NULL
                       OR LOWER(serial_number) LIKE LOWER(CONCAT('%', :serialNumber, '%')))
            AND (:isUsed IS NULL OR gc.used = :isUsed)
            AND (gc.tenant_id = :tenantId)
            ORDER BY gc.gift_card_id DESC
            """, nativeQuery = true)
    List<FindAllGiftCardDto> findAllGiftCardNative(
            @Param("serialNumber") String serialNumber,
            @Param("isUsed") Boolean isUsed, @Param("giftCardId") Integer giftCardId,
            @Param("tenantId") String tenantId);

    @Query(value = """
            SELECT gc.*, a.arrangement_id, b.phone_number
            FROM gift_card gc
            LEFT JOIN arrangement a ON gc.gift_card_id = a.gift_card_id
            LEFT JOIN baby b ON a.baby_id = b.baby_id
            WHERE (:giftCardId IS NULL OR gc.gift_card_id = :giftCardId)
            AND (:serialNumber IS NULL
                       OR LOWER(serial_number) LIKE LOWER(CONCAT('%', :serialNumber, '%')))
            AND (:isUsed IS NULL OR gc.used = :isUsed)
            AND (gc.expiration_date >= :startDate AND gc.expiration_date <= :endDate)
            AND (gc.tenant_id = :tenantId)
            ORDER BY gc.gift_card_id DESC
            """, nativeQuery = true)
    List<FindAllGiftCardDto> findAllGiftCardNativeWithStartDateAndDate(
            @Param("serialNumber") String serialNumber,
            @Param("isUsed") Boolean isUsed, @Param("giftCardId") Integer giftCardId, @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("tenantId") String tenantId);
}
