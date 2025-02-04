package com.backend.babyspa.v1.dtos;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateReservationDto {

	@NotNull(message = "Morate odabrati datum rezervacije")
	private LocalDateTime startDate;

	@NotNull(message = "Morate poslati id aranzmana")
	private Integer arrangementId;

	@NotNull(message = "Morate odabrati koliko minuta traje rezervacije")
	@Min(value = 1, message = "Rezervacija mora trajati najmanje jednu minutu")
	private Integer durationReservation;

	private String note;
}
