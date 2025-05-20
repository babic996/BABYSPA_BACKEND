package com.backend.babyspa.v1.controllers;

import com.backend.babyspa.v1.dtos.CreateGiftCardDto;
import com.backend.babyspa.v1.dtos.UpdateGiftCardDto;
import com.backend.babyspa.v1.models.GiftCard;
import com.backend.babyspa.v1.projections.FindAllGiftCardDto;
import com.backend.babyspa.v1.services.GiftCardService;
import com.backend.babyspa.v1.utils.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/gift-card")
public class GiftCardController extends BaseController {

    @Autowired
    GiftCardService giftCardService;

    @PostMapping("/save")
    public ResponseEntity<ApiResponse<GiftCard>> save(@RequestBody @Valid CreateGiftCardDto createGiftCardDto,
                                                      BindingResult bindingResult) {

        if (hasErrors(bindingResult)) {
            return createErrorResponse(bindingResult);
        }

        try {
            GiftCard giftCard = giftCardService.save(createGiftCardDto);
            return createSuccessResponse(giftCard);
        } catch (Exception e) {
            return createExceptionResponse(e);
        }

    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<GiftCard>> update(@RequestBody @Valid UpdateGiftCardDto updateGiftCardDto,
                                                        BindingResult bindingResult) {

        if (hasErrors(bindingResult)) {
            return createErrorResponse(bindingResult);
        }
        try {
            GiftCard giftCard = giftCardService.update(updateGiftCardDto);
            return createSuccessResponse(giftCard);
        } catch (Exception e) {
            return createExceptionResponse(e);
        }
    }

    @GetMapping("/find-all-list")
    public ResponseEntity<ApiResponse<List<GiftCard>>> findAllList(@RequestParam(required = false) Boolean isUsed, @RequestParam(required = false) Integer arrangementId) {

        try {
            return createSuccessResponse(giftCardService.findAllList(isUsed, arrangementId));
        } catch (Exception e) {
            return createExceptionResponse(e);
        }
    }


    @GetMapping("/find-all")
    public ResponseEntity<ApiResponse<Page<FindAllGiftCardDto>>> findAll(@RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int size,
                                                                         @RequestParam(required = false) String serialNumber,
                                                                         @RequestParam(required = false) Boolean isUsed,
                                                                         @RequestParam(required = false) Integer giftCardId,
                                                                         @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(required = false) LocalDateTime startRangeDate,
                                                                         @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(required = false) LocalDateTime endRangeDate) {
        try {
            return createSuccessResponse(giftCardService.findAll(page, size, serialNumber, isUsed, giftCardId, startRangeDate, endRangeDate));
        } catch (Exception e) {
            return createExceptionResponse(e);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Integer>> delete(@RequestParam int giftCardId) {

        try {
            int deletedGiftCardId = giftCardService.delete(giftCardId);
            return createSuccessResponse(deletedGiftCardId);
        } catch (Exception e) {
            return createExceptionResponse(e);
        }
    }
}
