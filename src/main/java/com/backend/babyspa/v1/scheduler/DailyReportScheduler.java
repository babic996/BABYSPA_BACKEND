package com.backend.babyspa.v1.scheduler;

import java.util.Arrays;
import java.util.List;

import com.backend.babyspa.v1.models.TenantNames;
import com.backend.babyspa.v1.services.BabyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.backend.babyspa.v1.services.ReservationService;

@Component
public class DailyReportScheduler {

    @Autowired
    ReservationService reservationService;

    @Autowired
    BabyService babyService;

    @Value("${tenants}")
    private String tenantsProperty;

    @Scheduled(cron = "00 59 23 * * *", zone = "Europe/Sarajevo")
    public void generateReports() {
        Arrays.stream(TenantNames.values())
                .forEach(tenant -> reservationService.generateReportForAllDateInReservation(true, null, tenant.name()));
    }

    @Scheduled(cron = "0 0 1 * * *", zone = "Europe/Sarajevo")
    public void updateReservationStatusDayBefore() {
        reservationService.updateReservationWithStatusCreatedToStatusUsed();
    }

    @Scheduled(cron = "0 59 23 * * *", zone = "Europe/Sarajevo")
    public void updateAllNumberOfMonths() {

        babyService.updateMonthsForAll();
    }

}
