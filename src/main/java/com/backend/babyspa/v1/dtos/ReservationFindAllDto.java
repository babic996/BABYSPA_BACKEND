package com.backend.babyspa.v1.dtos;

import java.time.LocalDateTime;

import com.backend.babyspa.v1.models.Status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationFindAllDto {

	private int reservationId;
	private LocalDateTime createdAt;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private String note;
	private Status status;
	private FindAllArrangementDto arrangement;
}
