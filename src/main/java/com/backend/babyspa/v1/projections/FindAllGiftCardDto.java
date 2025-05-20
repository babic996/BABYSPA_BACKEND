package com.backend.babyspa.v1.projections;

import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

public interface FindAllGiftCardDto {

    @Value("#{target.gift_card_id}")
    Integer getGiftCardId();

    @Value("#{target.expiration_date}")
    LocalDateTime getExpirationDate();

    @Value("#{target.used}")
    Boolean getUsed();

    @Value("#{target.serial_number}")
    String getSerialNumber();

    @Value("#{target.arrangement_id}")
    Integer getArrangementId();

    @Value("#{target.phone_number}")
    String getPhoneNumber();
}
