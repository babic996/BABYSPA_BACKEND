package com.backend.babyspa.v1.controllers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

import com.backend.babyspa.v1.dtos.CreateArrangementDto;
import com.backend.babyspa.v1.dtos.FindAllArrangementDto;
import com.backend.babyspa.v1.dtos.ShortDetailsDto;
import com.backend.babyspa.v1.dtos.UpdateArrangementDto;
import com.backend.babyspa.v1.models.Arrangement;
import com.backend.babyspa.v1.services.ArrangementService;
import com.backend.babyspa.v1.utils.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/arrangement")
public class ArrangementController extends BaseController {

    @Autowired
    ArrangementService arrangementService;

    @GetMapping("/find-by-id")
    public ResponseEntity<ApiResponse<Arrangement>> findById(@RequestParam int arrangementId) {

        return ResponseEntity.ok(ApiResponse.success(arrangementService.findById(arrangementId)));
    }

    @GetMapping("/exists-by-service-package-id")
    public ResponseEntity<ApiResponse<Boolean>> existsByServicePackageId(@RequestParam int servicePackageId) {

        return ResponseEntity.ok(ApiResponse.success(arrangementService.existsByServicePackage(servicePackageId)));
    }

    @PostMapping("/save")
    public ResponseEntity<ApiResponse<Arrangement>> save(@RequestBody @Valid CreateArrangementDto createReservationDto,
                                                         BindingResult bindingResult) {

        if (hasErrors(bindingResult)) {
            return createErrorResponse(bindingResult);
        }

        return ResponseEntity.ok(ApiResponse.success(arrangementService.save(createReservationDto)));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<FindAllArrangementDto>> update(
            @RequestBody @Valid UpdateArrangementDto updateReservationDto, BindingResult bindingResult) {

        if (hasErrors(bindingResult)) {
            return createErrorResponse(bindingResult);
        }

        return ResponseEntity.ok(ApiResponse.success(arrangementService.update(updateReservationDto)));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Integer>> delete(@RequestParam int arrangementId) {

        return ResponseEntity.ok(ApiResponse.success(arrangementService.delete(arrangementId)));
    }

    @GetMapping("/find-all")
    public ResponseEntity<ApiResponse<Page<FindAllArrangementDto>>> findAll(@RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) BigDecimal startPrice,
                                                                            @RequestParam(required = false) BigDecimal endPrice, @RequestParam(required = false) Integer statusId,
                                                                            @RequestParam(required = false) Integer servicePackageId, @RequestParam(required = false) Integer babyId,
                                                                            @RequestParam(required = false) Integer paymentTypeId,
                                                                            @RequestParam(required = false) Integer giftCardId,
                                                                            @RequestParam(required = false) Integer remainingTerm,
                                                                            @RequestParam(required = false) Integer arrangementId,
                                                                            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(required = false) LocalDateTime startRangeDate,
                                                                            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(required = false) LocalDateTime endRangeDate) {

        return ResponseEntity.ok(ApiResponse.success(arrangementService.findAll(page, size, babyId, statusId, servicePackageId,
                paymentTypeId, giftCardId, remainingTerm, startPrice, endPrice, arrangementId, startRangeDate, endRangeDate)));
    }

    @GetMapping("/find-price")
    public ResponseEntity<ApiResponse<BigDecimal>> findTotalSum(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) BigDecimal startPrice,
                                                                @RequestParam(required = false) BigDecimal endPrice, @RequestParam(required = false) Integer statusId,
                                                                @RequestParam(required = false) Integer servicePackageId, @RequestParam(required = false) Integer babyId,
                                                                @RequestParam(required = false) Integer paymentTypeId,
                                                                @RequestParam(required = false) Integer giftCardId,
                                                                @RequestParam(required = false) Integer remainingTerm,
                                                                @RequestParam(required = false) Integer arrangementId,
                                                                @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(required = false) LocalDateTime startRangeDate,
                                                                @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(required = false) LocalDateTime endRangeDate) {

        return ResponseEntity.ok(ApiResponse.success(arrangementService.findTotalSum(babyId, statusId, servicePackageId, paymentTypeId, giftCardId,
                remainingTerm, startPrice, endPrice, arrangementId, startRangeDate, endRangeDate)));
    }

    @GetMapping("/find-all-list")
    public ResponseEntity<ApiResponse<List<ShortDetailsDto>>> findAllList() {

        return ResponseEntity.ok(ApiResponse.success(arrangementService.findAllArrangementList()));
    }
}
