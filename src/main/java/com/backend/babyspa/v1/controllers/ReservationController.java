package com.backend.babyspa.v1.controllers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.babyspa.v1.config.TenantContext;
import com.backend.babyspa.v1.dtos.CreateReservationDto;
import com.backend.babyspa.v1.dtos.ReservationFindAllDto;
import com.backend.babyspa.v1.dtos.ReservationShortInfo;
import com.backend.babyspa.v1.dtos.UpdateReservationDto;
import com.backend.babyspa.v1.models.Reservation;
import com.backend.babyspa.v1.services.ReservationService;
import com.backend.babyspa.v1.utils.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/reservation")
public class ReservationController extends BaseController {

	@Autowired
	ReservationService reservationService;

	@GetMapping("/find-by-id")
	public ResponseEntity<ApiResponse<Reservation>> findById(@RequestParam int reservationId) {

		try {
			return createSuccessResponse(reservationService.findById(reservationId));
		} catch (Exception e) {
			return createExceptionResponse(e);
		}
	}

	@GetMapping("/find-by-arrangement-id")
	public ResponseEntity<ApiResponse<List<ReservationShortInfo>>> findByArrangementId(
			@RequestParam int arrangementId) {

		try {
			return createSuccessResponse(reservationService.findByArrangementId(arrangementId));
		} catch (Exception e) {
			return createExceptionResponse(e);
		}
	}

	@PostMapping("/save")
	public ResponseEntity<ApiResponse<ReservationFindAllDto>> save(
			@RequestBody @Valid CreateReservationDto createReservationDto, BindingResult bindingResult) {

		if (hasErrors(bindingResult)) {
			return createErrorResponse(bindingResult);
		}

		try {
			ReservationFindAllDto reservation = reservationService.save(createReservationDto);
			return createSuccessResponse(reservation);
		} catch (Exception e) {
			return createExceptionResponse(e);
		}
	}

	@PutMapping("/update")
	public ResponseEntity<ApiResponse<ReservationFindAllDto>> update(
			@RequestBody @Valid UpdateReservationDto updateReservationDto, BindingResult bindingResult) {

		if (hasErrors(bindingResult)) {
			return createErrorResponse(bindingResult);
		}

		try {
			ReservationFindAllDto reservation = reservationService.update(updateReservationDto);
			return createSuccessResponse(reservation);
		} catch (Exception e) {
			return createExceptionResponse(e);
		}
	}

	@DeleteMapping("/delete")
	public ResponseEntity<ApiResponse<Integer>> delete(@RequestParam int reservationId) {

		try {
			int deletedReservationId = reservationService.delete(reservationId);
			return createSuccessResponse(deletedReservationId);
		} catch (Exception e) {
			return createExceptionResponse(e);
		}
	}

	@PutMapping("/canceled")
	public ResponseEntity<ApiResponse<Integer>> reservationCanceled(@RequestParam int reservationId) {

		try {
			int canceledReservationId = reservationService.reservationCanceled(reservationId);
			return createSuccessResponse(canceledReservationId);
		} catch (Exception e) {
			return createExceptionResponse(e);
		}
	}

	@GetMapping("/find-all-list")
	public ResponseEntity<ApiResponse<List<ReservationFindAllDto>>> findAll() {

		return createSuccessResponse(reservationService.findAllList());
	}

	@GetMapping("/find-all-by-arrangement")
	public ResponseEntity<ApiResponse<List<Reservation>>> findAllByArrangement(@RequestParam int arrangementId) {

		try {
			return createSuccessResponse(reservationService.findAllByArrangementId(arrangementId));
		} catch (Exception e) {
			return createExceptionResponse(e);
		}
	}

	@GetMapping("/exists-by-arrangement")
	public ResponseEntity<ApiResponse<Boolean>> existsByArrangement(@RequestParam int arrangementId) {

		return createSuccessResponse(reservationService.existingByArrangement(arrangementId));
	}

	@GetMapping("/generate-report")
	public void generateReports(@RequestParam(required = true) boolean generateForAllDays,
			@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(required = false) LocalDateTime date) {

		reservationService.generateReportForAllDateInReservation(generateForAllDays,
				Objects.nonNull(date) ? date.toLocalDate() : null, TenantContext.getTenant());
	}

}
