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

        return ResponseEntity.ok(ApiResponse.success(reservationService.findById(reservationId)));
    }

    @GetMapping("/find-by-arrangement-id")
    public ResponseEntity<ApiResponse<List<ReservationShortInfo>>> findByArrangementId(
            @RequestParam int arrangementId) {

        return ResponseEntity.ok(ApiResponse.success(reservationService.findByArrangementId(arrangementId)));
    }

    @PostMapping("/save")
    public ResponseEntity<ApiResponse<ReservationFindAllDto>> save(
            @RequestBody @Valid CreateReservationDto createReservationDto, BindingResult bindingResult) {

        if (hasErrors(bindingResult)) {
            return createErrorResponse(bindingResult);
        }

        return ResponseEntity.ok(ApiResponse.success(reservationService.save(createReservationDto)));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<ReservationFindAllDto>> update(
            @RequestBody @Valid UpdateReservationDto updateReservationDto, BindingResult bindingResult) {

        if (hasErrors(bindingResult)) {
            return createErrorResponse(bindingResult);
        }

        return ResponseEntity.ok(ApiResponse.success(reservationService.update(updateReservationDto)));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Integer>> delete(@RequestParam int reservationId) {

        return ResponseEntity.ok(ApiResponse.success(reservationService.delete(reservationId)));
    }

    @PutMapping("/canceled")
    public ResponseEntity<ApiResponse<Integer>> reservationCanceled(@RequestParam int reservationId) {

        return ResponseEntity.ok(ApiResponse.success(reservationService.reservationCanceled(reservationId)));
    }

    @GetMapping("/find-all-list")
    public ResponseEntity<ApiResponse<List<ReservationFindAllDto>>> findAll() {

        return ResponseEntity.ok(ApiResponse.success(reservationService.findAllList()));
    }

    @GetMapping("/find-all-by-arrangement")
    public ResponseEntity<ApiResponse<List<Reservation>>> findAllByArrangement(@RequestParam int arrangementId) {

        return ResponseEntity.ok(ApiResponse.success(reservationService.findAllByArrangementId(arrangementId)));
    }

    @GetMapping("/exists-by-arrangement")
    public ResponseEntity<ApiResponse<Boolean>> existsByArrangement(@RequestParam int arrangementId) {

        return ResponseEntity.ok(ApiResponse.success(reservationService.existingByArrangement(arrangementId)));
    }

    @GetMapping("/generate-report")
    public void generateReports(@RequestParam(required = true) boolean generateForAllDays,
                                @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(required = false) LocalDateTime date) {

        reservationService.generateReportForAllDateInReservation(generateForAllDays,
                Objects.nonNull(date) ? date.toLocalDate() : null, TenantContext.getTenant());
    }

}
