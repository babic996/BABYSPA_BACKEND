package com.backend.babyspa.v1.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "gift_card")
public class GiftCard {

    @Id
    @Column(name = "gift_card_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int giftCardId;

    @Column(name = "serial_number", nullable = false)
    private String serialNumber;

    @Column(name = "expiration_date", nullable = true)
    private LocalDateTime expirationDate;

    @Column(name = "used", nullable = false)
    private boolean used = false;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GiftCard giftCard)) return false;

        return serialNumber != null && serialNumber.equals(giftCard.serialNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialNumber);
    }

}
