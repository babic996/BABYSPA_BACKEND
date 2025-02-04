package com.backend.babyspa.v1.models;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "service_package_daily_report")
public class ServicePackageDailyReport {

	@Id
	@Column(name = "service_package_daily_report_id", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int servicePackageDailyReportId;

	@Column(name = "number_of_used_packages", nullable = false)
	private int numberOfUsedPackages;

	@Column(name = "date", nullable = false)
	private LocalDate date;

	@ManyToOne
	@JoinColumn(name = "service_package_id", nullable = true)
	private ServicePackage servicePackage;

	@Column(name = "tenant_id", nullable = false)
	private String tenantId;
}
