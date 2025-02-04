package com.backend.babyspa.v1.dtos;

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
public class UpdateStatusDto {

	@NotNull(message = "Morate poslati id statusa")
	private int statusId;

	@NotNull(message = "Morate unijeti ime statusa")
	@NotBlank(message = "Poslali ste samo razmake")
	@Pattern(regexp = "^(?! ).*", message = "Unijeli ste prvo razmak pa ime statusa")
	private String statusName;

	@NotNull(message = "Morate unijeti kod statusa")
	@NotBlank(message = "Poslali ste samo razmake")
	@Pattern(regexp = "^(?! ).*", message = "Unijeli ste prvo razmak pa kod statusa")
	private String statusCode;
	
	@NotNull(message = "Morate poslati id tipa statusa")
	private int statusTypeId;
}
