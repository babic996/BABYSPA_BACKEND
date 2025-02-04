package com.backend.babyspa.v1.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateDiscountDto {

	@NotNull(message = "Morate unijeti vrijednost za popust")
	@DecimalMin(value = "0.1", message = "Vrijednost mora biti veća od 0")
	private BigDecimal value;

	@NotNull(message = "Popust može biti procentualan ili snižen za neku vrijednost")
	private Boolean isPrecentage;

}
