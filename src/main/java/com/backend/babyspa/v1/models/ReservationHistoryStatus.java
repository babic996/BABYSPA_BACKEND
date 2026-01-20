package com.backend.babyspa.v1.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Filter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Table(name = "reservation_history_status")
public class ReservationHistoryStatus extends TenantEntity {

  @Id
  @Column(name = "reservation_history_status_id", nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int reservationHistoryStatusId;

  @ManyToOne
  @JoinColumn(name = "reservation_id", nullable = false)
  private Reservation reservation;

  @ManyToOne
  @JoinColumn(name = "status_id", nullable = false)
  private Status status;

  @ManyToOne
  @JoinColumn(name = "action_by_user_id", referencedColumnName = "user_id", nullable = false)
  private User actionByUser;

  @JoinColumn(name = "action_at", nullable = false)
  private LocalDateTime actionAt = LocalDateTime.now();

  @Column(name = "tenant_id", nullable = false)
  private String tenantId;
}
