package com.backend.babyspa.v1.models;

import java.math.BigDecimal;

import org.hibernate.annotations.Filter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Table(name = "discount")
public class Discount extends TenantEntity {

    @Id
    @Column(name = "discount_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int discountId;

    @Column(name = "value", nullable = false)
    private BigDecimal value;

    @Column(name = "is_precentage", nullable = false)
    private boolean isPrecentage;

    @Column(name = "discount_name", nullable = false)
    private String discountName;

    public Discount(BigDecimal value, boolean isPrecentage, String discountName, String tenantId) {
        this.value = value;
        this.isPrecentage = isPrecentage;
        this.discountName = discountName;
    }

    @Override
    public String toString() {
        return "Id: " + discountId +
                ", Vrijednost: " + value +
                ", Ime popusta: " + discountName +
                ", Popust u procentima: " + (isPrecentage ? "DA" : "NE");
    }

}
