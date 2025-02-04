package com.backend.babyspa.v1.projections;

import org.springframework.beans.factory.annotation.Value;

public interface ServicePackagesDailyReportProjection {

	@Value("#{target.number_of_used_packages}")
	Integer getNumberOfUsedPackages();

	@Value("#{target.date}")
	String getDate();
}
