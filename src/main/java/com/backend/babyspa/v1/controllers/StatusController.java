package com.backend.babyspa.v1.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.backend.babyspa.v1.dtos.CreateStatusDto;
import com.backend.babyspa.v1.dtos.UpdateStatusDto;
import com.backend.babyspa.v1.models.Status;
import com.backend.babyspa.v1.services.StatusService;
import com.backend.babyspa.v1.utils.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/status")
public class StatusController extends BaseController {

	@Autowired
	StatusService statusService;

	@GetMapping("/find-by-id")
	public ResponseEntity<ApiResponse<Status>> findById(@RequestParam int statusId) {

		try {
			return createSuccessResponse(statusService.findById(statusId));
		} catch (Exception e) {
			return createExceptionResponse(e);
		}
	}

	@PostMapping("/save")
	public ResponseEntity<ApiResponse<Status>> save(@RequestBody @Valid CreateStatusDto createStatusDto,
			BindingResult bindingResult) {

		if (hasErrors(bindingResult)) {
			return createErrorResponse(bindingResult);
		}

		try {
			Status status = statusService.save(createStatusDto);
			return createSuccessResponse(status);
		} catch (Exception e) {
			return createExceptionResponse(e);
		}
	}

	@PutMapping("/update")
	public ResponseEntity<ApiResponse<Status>> update(@RequestBody @Valid UpdateStatusDto updateStatusDto,
			BindingResult bindingResult) {

		if (hasErrors(bindingResult)) {
			return createErrorResponse(bindingResult);
		}

		try {
			Status status = statusService.update(updateStatusDto);
			return createSuccessResponse(status);
		} catch (Exception e) {
			return createExceptionResponse(e);
		}
	}

	@DeleteMapping("/delete")
	public ResponseEntity<ApiResponse<Integer>> delete(@RequestParam int statusId) {

		try {
			int deletedStatusId = statusService.delete(statusId);
			return createSuccessResponse(deletedStatusId);
		} catch (Exception e) {
			return createExceptionResponse(e);
		}
	}

	@GetMapping("/find-all")
	public ResponseEntity<ApiResponse<List<Status>>> findAll() {

		try {
			return createSuccessResponse(statusService.findAll());
		} catch (Exception e) {
			return createExceptionResponse(e);
		}
	}

	@GetMapping("/find-all-status-type-code")
	public ResponseEntity<ApiResponse<List<Status>>> findByStatusCode(@RequestParam String statusTypeCode) {

		try {
			return createSuccessResponse(statusService.findAllByStatusTypeCode(statusTypeCode));
		} catch (Exception e) {
			return createExceptionResponse(e);
		}
	}
}
