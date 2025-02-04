package com.backend.babyspa.v1.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationShortInfo {

	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private String statusName;
}
