package com.backend.babyspa.v1.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "arrangement_snapshot")
public class ArrangementSnapshot {

  @Id
  @Column(name = "arrangement_snapshot_id", nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int arrangementSnapshotId;

  @Column(name = "remaining_term", nullable = false)
  private int remainingTerm;

  @Column(name = "extend_duration_days")
  private Integer extendDurationDays;

  @Column(name = "price", nullable = false)
  private BigDecimal price;

  @Column(name = "note", columnDefinition = "TEXT")
  private String note;

  @Column(name = "discount_info", columnDefinition = "TEXT")
  private String discountInfo;

  @Column(name = "baby_info", columnDefinition = "TEXT")
  private String babyInfo;

  @Column(name = "status_info", columnDefinition = "TEXT")
  private String statusInfo;

  @Column(name = "service_package_info", columnDefinition = "TEXT")
  private String servicePackageInfo;

  @Column(name = "payment_type_info", columnDefinition = "TEXT")
  private String paymentTypeInfo;

  @Column(name = "gift_card_info")
  private String giftCardInfo;
}
