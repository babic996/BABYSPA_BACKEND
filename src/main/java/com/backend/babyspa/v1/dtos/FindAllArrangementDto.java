package com.backend.babyspa.v1.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FindAllArrangementDto {

	private int arrangementId;
	private LocalDateTime createdAt;
	private int remainingTerm;
	private Integer extendDurationDays;
	private BigDecimal price;
	private String note;
	private ShortDetailsDto discount;
	private ShortDetailsDto babyDetails;
	private ShortDetailsDto status;
	private ShortDetailsDto servicePackage;
	private ShortDetailsDto paymentType;

}
