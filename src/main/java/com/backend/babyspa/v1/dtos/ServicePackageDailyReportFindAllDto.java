package com.backend.babyspa.v1.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServicePackageDailyReportFindAllDto {

	private int servicePackageDailyReportId;
	private int numberOfUsedPackages;
	private String date;
}
