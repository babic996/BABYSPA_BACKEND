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

import com.backend.babyspa.v1.projections.ReservationDailyReportProjection;
import com.backend.babyspa.v1.services.ReservationDailyReportService;
import com.backend.babyspa.v1.utils.ApiResponse;

@RestController
@RequestMapping("/reservation-daily-report")
public class ReservationDailyReportController extends BaseController {

	@Autowired
	ReservationDailyReportService reservationDailyReportService;

	@GetMapping("/find-all")
	public ResponseEntity<ApiResponse<List<ReservationDailyReportProjection>>> findAll(
			@RequestParam(required = false) Integer statusId, @RequestParam(required = false) Integer babyId,
			@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(required = false) LocalDateTime startRangeDate,
			@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(required = false) LocalDateTime endRangeDate,
			@RequestParam(required = true) String groupDataType) {

		try {
			return createSuccessResponse(reservationDailyReportService.findAll(statusId, babyId, startRangeDate,
					endRangeDate, groupDataType));
		} catch (Exception e) {
			return createExceptionResponse(e);
		}
	}

}
