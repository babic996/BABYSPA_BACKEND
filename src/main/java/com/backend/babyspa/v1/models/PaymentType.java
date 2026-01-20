package com.backend.babyspa.v1.models;

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

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Table(name = "payment_type")
public class PaymentType extends TenantEntity {

  @Id
  @Column(name = "payment_type_id", nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int paymentTypeId;

  @Column(name = "payment_type_name", nullable = false)
  private String paymentTypeName;

  @Column(name = "payment_type_code", nullable = false)
  private String paymentTypeCode;

  @Column(name = "tenant_id", nullable = false)
  private String tenantId;

  public PaymentType(String paymentTypeName, String paymentTypeCode, String tenantId) {
    this.paymentTypeName = paymentTypeName;
    this.paymentTypeCode = paymentTypeCode;
    this.tenantId = tenantId;
  }

  @Override
  public String toString() {
    return "Id: " + paymentTypeId + ", Ime: " + paymentTypeName + ", Kod: " + paymentTypeCode;
  }
}
