package com.backend.babyspa.v1.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UpdateArrangementDto {

	@NotNull(message = "Morate poslati id aranzmana")
	private int arrangementId;

	private Integer discountId;

	private String note;

	private Integer extendDurationDays;

	@NotNull(message = "Morate bebu za koju pravi aranzman")
	private int babyId;

	@NotNull(message = "Morate izabrati status aranzmana")
	private int statusId;

	@NotNull(message = "Morate izabrati paket usluge")
	private int servicePackageId;

	private Integer paymentTypeId;
}
