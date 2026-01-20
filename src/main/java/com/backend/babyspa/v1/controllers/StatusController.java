package com.backend.babyspa.v1.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.babyspa.v1.dtos.CreateStatusDto;
import com.backend.babyspa.v1.dtos.UpdateStatusDto;
import com.backend.babyspa.v1.models.Status;
import com.backend.babyspa.v1.services.StatusService;
import com.backend.babyspa.v1.utils.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/status")
public class StatusController {

  @Autowired private StatusService statusService;

  @GetMapping("/find-by-id")
  public ResponseEntity<ApiResponse<Status>> findById(@RequestParam int statusId) {

    return ResponseEntity.ok(ApiResponse.success(statusService.findById(statusId)));
  }

  @PostMapping("/save")
  public ResponseEntity<ApiResponse<Status>> save(
      @RequestBody @Valid CreateStatusDto createStatusDto) {

    return ResponseEntity.ok(ApiResponse.success(statusService.save(createStatusDto)));
  }

  @PutMapping("/update")
  public ResponseEntity<ApiResponse<Status>> update(
      @RequestBody @Valid UpdateStatusDto updateStatusDto) {

    return ResponseEntity.ok(ApiResponse.success(statusService.update(updateStatusDto)));
  }

  @DeleteMapping("/delete")
  public ResponseEntity<ApiResponse<Integer>> delete(@RequestParam int statusId) {

    return ResponseEntity.ok(ApiResponse.success(statusService.delete(statusId)));
  }

  @GetMapping("/find-all")
  public ResponseEntity<ApiResponse<List<Status>>> findAll() {

    return ResponseEntity.ok(ApiResponse.success(statusService.findAll()));
  }

  @GetMapping("/find-all-status-type-code")
  public ResponseEntity<ApiResponse<List<Status>>> findByStatusCode(
      @RequestParam String statusTypeCode) {

    return ResponseEntity.ok(
        ApiResponse.success(statusService.findAllByStatusTypeCode(statusTypeCode)));
  }
}
