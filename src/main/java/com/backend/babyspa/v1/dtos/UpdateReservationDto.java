package com.backend.babyspa.v1.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateReservationDto {

	@NotNull(message = "Morate poslati id rezervacije")
	private Integer reservationId;

	@NotNull(message = "Morate odabrati status")
	private Integer statusId;

	private String note;
}
