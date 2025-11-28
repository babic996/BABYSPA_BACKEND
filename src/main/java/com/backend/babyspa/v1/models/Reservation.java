package com.backend.babyspa.v1.models;

import java.time.LocalDateTime;

import org.hibernate.annotations.Filter;

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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Table(name = "reservation")
public class Reservation extends TenantEntity {

    @Id
    @Column(name = "reservation_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reservationId;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne
    @JoinColumn(name = "arrangement_id", nullable = false)
    private Arrangement arrangement;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id", referencedColumnName = "user_id", nullable = false)
    private User createdByUser;

    @ManyToOne
    @JoinColumn(name = "updated_by_user_id", referencedColumnName = "user_id")
    private User updatedByUser;

    @ManyToOne
    @JoinColumn(name = "deleted_by_user_id", referencedColumnName = "user_id")
    private User deletedByUser;
}
