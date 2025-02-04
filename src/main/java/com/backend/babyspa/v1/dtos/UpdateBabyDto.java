package com.backend.babyspa.v1.dtos;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBabyDto {

	@NotNull(message = "Morate poslati id bebe")
	private int babyId;

	@NotNull(message = "Morate unijeti ime bebe")
	@NotBlank(message = "Poslali ste samo razmake")
	@Pattern(regexp = "^(?! ).*", message = "Unijeli ste prvo razmak pa ime")
	private String babyName;

	private String babySurname;

	@NotNull(message = "Morate unijeti broj mjeseci")
	@Min(value = 1, message = "Broj mjeseci mora biti veći od 0")
	private int numberOfMonths;

	private LocalDateTime birthDate;

	@NotNull(message = "Morate unijeti kontakt telefon")
	@Pattern(regexp = "\\+\\d{10,}", message = "Broj mora početi sa '+' i sadržavati najmanje 10 cifara, bez razmaka")
	private String phoneNumber;

	private String motherName;

	private String note;
}
