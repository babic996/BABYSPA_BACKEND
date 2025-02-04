package com.backend.babyspa.v1.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
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
public class CreateServicePackageDto {

	@NotNull(message = "Morate unijeti ime bebe")
	@NotBlank(message = "Poslali ste samo razmake")
	@Pattern(regexp = "^(?! ).*", message = "Unijeli ste prvo razmak pa naziv")
	private String servicePackageName;

	@NotNull(message = "Morate unijeti broj termina")
	@Min(value = 1, message = "Broj termina mora biti veći od 0")
	private int termNumber;

	@NotNull(message = "Morate unijeti koliko dana traje termin")
	@Min(value = 1, message = "Broj trajanja u danima mora biti veći od 0")
	private int servicePackageDurationDays;

	@NotNull(message = "Morate unijeti cijenu paketa")
	@DecimalMin(value = "0.1", message = "Cijena mora biti veća od 0")
	private BigDecimal price;

	private String note;
}
