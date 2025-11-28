package com.backend.babyspa.v1.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.babyspa.v1.projections.ServicePackagesDailyReportProjection;
import com.backend.babyspa.v1.services.ServicePackageDailyReportService;
import com.backend.babyspa.v1.utils.ApiResponse;

@RestController
@RequestMapping("/service-package-daily-report")
public class ServicePackageDailyReportController {

    @Autowired
    ServicePackageDailyReportService servicePackageDailyReportService;

    @GetMapping("/find-all")
    public ResponseEntity<ApiResponse<List<ServicePackagesDailyReportProjection>>> findAll(
            @RequestParam(required = false) Integer servicePackageId,
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(required = false) LocalDateTime startRangeDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(required = false) LocalDateTime endRangeDate,
            @RequestParam String groupDataType) {

        return ResponseEntity.ok(ApiResponse.success(servicePackageDailyReportService.findAll(servicePackageId, startRangeDate,
                endRangeDate, groupDataType)));
		
    }
}
