package com.backend.babyspa.v1.models;

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
@Table(name = "baby")
public class Baby extends TenantEntity {

  @Id
  @Column(name = "baby_id", nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int babyId;

  @Column(name = "baby_name", nullable = false)
  private String babyName;

  @Column(name = "baby_surname")
  private String babySurname;

  @Column(name = "number_of_months", nullable = false)
  private int numberOfMonths;

  @Column(name = "birth_date")
  private LocalDateTime birthDate;

  @Column(name = "phone_number", nullable = false)
  private String phoneNumber;

  @Column(name = "mother_name")
  private String motherName;

  @Column(name = "note", columnDefinition = "TEXT")
  private String note;

  @Column(name = "is_deleted")
  private boolean isDeleted = false;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  @Column(name = "created_at")
  private LocalDateTime createdAt = LocalDateTime.now();

  @ManyToOne
  @JoinColumn(name = "created_by_user_id", referencedColumnName = "user_id", nullable = false)
  private User createdByUser;

  @ManyToOne
  @JoinColumn(name = "updated_by_user_id", referencedColumnName = "user_id")
  private User updatedByUser;

  @ManyToOne
  @JoinColumn(name = "deleted_by_user_id", referencedColumnName = "user_id")
  private User deletedByUser;

  @Override
  public String toString() {
    return "Id: "
        + babyId
        + ", Ime bebe: "
        + babyName
        + ", Ime majke: "
        + motherName
        + ", Broj telefona: "
        + phoneNumber
        + ", Broj mjeseci: "
        + numberOfMonths
        + ", Bilješka: "
        + note;
  }
}
