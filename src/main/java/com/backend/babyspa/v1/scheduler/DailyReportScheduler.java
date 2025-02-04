package com.backend.babyspa.v1.scheduler;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.backend.babyspa.v1.services.ReservationService;

@Component
public class DailyReportScheduler {

	@Autowired
	ReservationService reservationService;

	@Value("${tenants}")
	private String tenantsProperty;

	@Scheduled(cron = "00 59 23 * * *", zone = "Europe/Sarajevo")
	public void generateReports() {
		List<String> tenants = Arrays.asList(tenantsProperty.split(","));
		tenants.forEach(tenant -> {
			reservationService.generateReportForAllDateInReservation(true, null, tenant);
		});

	}

	@Scheduled(cron = "0 0 1 * * *", zone = "Europe/Sarajevo")
	public void updateReservationStatusDayBefore() {
		reservationService.updateReservationWithStatusCreatedToStatusUsed();
	}

}
