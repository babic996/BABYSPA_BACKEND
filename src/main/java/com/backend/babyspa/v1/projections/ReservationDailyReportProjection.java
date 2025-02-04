package com.backend.babyspa.v1.projections;

import org.springframework.beans.factory.annotation.Value;

public interface ReservationDailyReportProjection {

	@Value("#{target.number_of_reservation}")
	Integer getNumberOfReservation();

	@Value("#{target.date}")
	String getDate();
}
