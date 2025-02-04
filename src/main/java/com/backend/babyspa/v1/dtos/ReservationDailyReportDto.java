package com.backend.babyspa.v1.dtos;

import java.time.LocalDate;

import com.backend.babyspa.v1.models.Status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDailyReportDto {

	private int numberOfReservation;
	private LocalDate date;
	private Status status;
	private int babyId;
}
