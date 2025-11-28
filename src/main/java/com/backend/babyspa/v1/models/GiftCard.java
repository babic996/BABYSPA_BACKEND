package com.backend.babyspa.v1.models;

import com.backend.babyspa.v1.utils.DateTimeUtil;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.Filter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Table(name = "gift_card")
@EqualsAndHashCode(of = "serialNumber")
public class GiftCard extends TenantEntity {

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

    @Override
    public String toString() {
        return "Id: " + giftCardId +
                ", Serijski broj: " + serialNumber +
                ", Datum isteka: " + (Objects.nonNull(expirationDate) ? DateTimeUtil.formatLocalDateTime(expirationDate) : "") +
                ", Iskorištena: " + (used ? "DA" : "NE");
    }

}
