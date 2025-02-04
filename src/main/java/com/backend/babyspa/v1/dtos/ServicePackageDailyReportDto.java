package com.backend.babyspa.v1.dtos;

import java.time.LocalDate;

import com.backend.babyspa.v1.models.ServicePackage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServicePackageDailyReportDto {

	private int numberOfUsedPackages;
	private LocalDate date;
	private ServicePackage servicePackage;
}
