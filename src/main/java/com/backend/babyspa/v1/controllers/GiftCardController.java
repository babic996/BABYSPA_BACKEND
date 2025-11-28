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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/gift-card")
public class GiftCardController {

    @Autowired
    GiftCardService giftCardService;

    @PostMapping("/save")
    public ResponseEntity<ApiResponse<GiftCard>> save(@RequestBody @Valid CreateGiftCardDto createGiftCardDto) {

        return ResponseEntity.ok(ApiResponse.success(giftCardService.save(createGiftCardDto)));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<GiftCard>> update(@RequestBody @Valid UpdateGiftCardDto updateGiftCardDto) {

        return ResponseEntity.ok(ApiResponse.success(giftCardService.update(updateGiftCardDto)));
    }

    @GetMapping("/find-all-list")
    public ResponseEntity<ApiResponse<List<GiftCard>>> findAllList(@RequestParam(required = false) Boolean isUsed, @RequestParam(required = false) Integer arrangementId) {

        return ResponseEntity.ok(ApiResponse.success(giftCardService.findAllList(isUsed, arrangementId)));
    }


    @GetMapping("/find-all")
    public ResponseEntity<ApiResponse<Page<FindAllGiftCardDto>>> findAll(@RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int size,
                                                                         @RequestParam(required = false) String serialNumber,
                                                                         @RequestParam(required = false) Boolean isUsed,
                                                                         @RequestParam(required = false) Integer giftCardId,
                                                                         @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(required = false) LocalDateTime startRangeDate,
                                                                         @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(required = false) LocalDateTime endRangeDate) {

        return ResponseEntity.ok(ApiResponse.success(giftCardService.findAll(page, size, serialNumber, isUsed, giftCardId, startRangeDate, endRangeDate)));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Integer>> delete(@RequestParam int giftCardId) {

        return ResponseEntity.ok(ApiResponse.success(giftCardService.delete(giftCardId)));
    }
}
