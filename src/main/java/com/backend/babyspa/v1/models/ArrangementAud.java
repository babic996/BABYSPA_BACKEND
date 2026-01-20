package com.backend.babyspa.v1.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import org.hibernate.annotations.Filter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@Table(name = "arrangement_aud")
public class ArrangementAud extends TenantEntity {

  @Id
  @Column(name = "arrangement_aud_id", nullable = false)
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int arrangementAudId;

  @OneToOne
  @JoinColumn(name = "arrangement_snapshot_id", nullable = false)
  private ArrangementSnapshot arrangementSnapshot;

  private int arrangementId;

  @Enumerated(EnumType.STRING)
  private UserActionType userActionType;

  @ManyToOne
  @JoinColumn(name = "action_by_user_id", referencedColumnName = "user_id")
  private User actionByUser;

  @JoinColumn(name = "action_at", nullable = false)
  private LocalDateTime actionAt = LocalDateTime.now();
}
