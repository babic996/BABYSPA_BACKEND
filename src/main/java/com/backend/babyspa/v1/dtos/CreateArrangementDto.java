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
public class CreateArrangementDto {

	private Integer discountId;

	private String note;

	@NotNull(message = "Morate bebu za koju pravi aranzman")
	private int babyId;

	@NotNull(message = "Morate izabrati paket usluge")
	private int servicePackageId;

}
