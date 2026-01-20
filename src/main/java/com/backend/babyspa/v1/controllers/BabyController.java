package com.backend.babyspa.v1.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.babyspa.v1.dtos.CreateBabyDto;
import com.backend.babyspa.v1.dtos.ShortDetailsDto;
import com.backend.babyspa.v1.dtos.UpdateBabyDto;
import com.backend.babyspa.v1.models.Baby;
import com.backend.babyspa.v1.services.BabyService;
import com.backend.babyspa.v1.utils.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/baby")
public class BabyController {

  @Autowired private BabyService babyService;

  @GetMapping("/find-by-id")
  public ResponseEntity<ApiResponse<Baby>> findById(@RequestParam Integer babyId) {

    return ResponseEntity.ok(ApiResponse.success(babyService.findById(babyId)));
  }

  @PostMapping("/save")
  public ResponseEntity<ApiResponse<Baby>> save(@RequestBody @Valid CreateBabyDto createBabyDto) {

    return ResponseEntity.ok(ApiResponse.success(babyService.save(createBabyDto)));
  }

  @PutMapping("/update")
  public ResponseEntity<ApiResponse<Baby>> update(@RequestBody @Valid UpdateBabyDto updateBabyDto) {

    return ResponseEntity.ok(ApiResponse.success(babyService.update(updateBabyDto)));
  }

  @DeleteMapping("/delete")
  public ResponseEntity<ApiResponse<Integer>> delete(@RequestParam int babyId) {

    return ResponseEntity.ok(ApiResponse.success(babyService.delete(babyId)));
  }

  @GetMapping("/find-all")
  public ResponseEntity<ApiResponse<Page<Baby>>> findAllPage(
      @RequestParam(required = false) String searchText,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(required = false)
          LocalDateTime startRangeDate,
      @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") @RequestParam(required = false)
          LocalDateTime endRangeDate) {

    return ResponseEntity.ok(
        ApiResponse.success(
            babyService.findAllPage(searchText, startRangeDate, endRangeDate, page, size)));
  }

  @GetMapping("/find-all-list")
  public ResponseEntity<ApiResponse<List<ShortDetailsDto>>> findAllList() {

    return ResponseEntity.ok(ApiResponse.success(babyService.findAllList()));
  }
}
