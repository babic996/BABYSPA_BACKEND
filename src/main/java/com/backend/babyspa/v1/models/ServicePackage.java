package com.backend.babyspa.v1.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Table(name = "service_package")
public class ServicePackage extends TenantEntity {

  @Id
  @Column(name = "service_package_id", nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int servicePackageId;

  @Column(name = "service_package_name", nullable = false)
  private String servicePackageName;

  @Column(name = "term_number", nullable = false)
  private int termNumber;

  @Column(name = "service_package_duration_days", nullable = false)
  private int servicePackageDurationDays;

  @Column(name = "price", nullable = false)
  private BigDecimal price;

  @Column(name = "note", columnDefinition = "TEXT", nullable = true)
  private String note;

  @Column(name = "tenant_id", nullable = false)
  private String tenantId;

  @Column(name = "is_deleted")
  private boolean isDeleted = false;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  @ManyToOne
  @JoinColumn(name = "created_by_user_id", referencedColumnName = "user_id", nullable = false)
  private User createdByUser;

  @ManyToOne
  @JoinColumn(name = "updated_by_user_id", referencedColumnName = "user_id", nullable = true)
  private User updatedByUser;

  @ManyToOne
  @JoinColumn(name = "deleted_by_user_id", referencedColumnName = "user_id")
  private User deletedByUser;

  @Override
  public String toString() {
    return "Id: "
        + servicePackageId
        + ", Ime paketa: "
        + servicePackageName
        + ", Broj termina: "
        + termNumber
        + ", Trajanje u danima: "
        + servicePackageDurationDays
        + ", Cijena: "
        + price
        + ", Bilješka: "
        + note;
  }
}
