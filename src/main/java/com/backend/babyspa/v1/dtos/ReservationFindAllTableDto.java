package com.backend.babyspa.v1.dtos;

import com.backend.babyspa.v1.models.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationFindAllTableDto {

  private int reservationId;
  private int arrangementId;
  private int remainingTerm;
  private LocalDateTime createdAt;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private Status status;
  private ShortDetailsDto babyDetails;
  private String servicePackageName;
  private String note;
}
