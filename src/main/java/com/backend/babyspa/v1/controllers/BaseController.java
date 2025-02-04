package com.backend.babyspa.v1.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import com.backend.babyspa.v1.utils.ApiResponse;

public abstract class BaseController {
	protected <T> ResponseEntity<ApiResponse<T>> createErrorResponse(BindingResult bindingResult) {
		ApiResponse<T> errorResponse = new ApiResponse<>();
		errorResponse.setStatus("Error");
		errorResponse.setMessage("Validation failed!");

		List<Map<String, String>> errors = new ArrayList<>();
		bindingResult.getFieldErrors().forEach(error -> {
			Map<String, String> errorDetail = new HashMap<>();
			errorDetail.put("field", error.getField());
			errorDetail.put("message", error.getDefaultMessage());
			errors.add(errorDetail);
		});
		errorResponse.setErrors(errors);

		return ResponseEntity.badRequest().body(errorResponse);
	}

	protected <T> ResponseEntity<ApiResponse<T>> createSuccessResponse(T data) {
		ApiResponse<T> successResponse = new ApiResponse<>();
		successResponse.setData(data);
		successResponse.setMessage("Successfully!");
		successResponse.setStatus("Success");

		return ResponseEntity.ok(successResponse);
	}

	protected <T> ResponseEntity<ApiResponse<T>> createExceptionResponse(Exception e) {
		ApiResponse<T> exceptionResponse = new ApiResponse<>();
		exceptionResponse.setStatus("Error");
		exceptionResponse.setMessage(e.getMessage());

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionResponse);
	}

	protected boolean hasErrors(BindingResult bindingResult) {
		return Objects.nonNull(bindingResult) && bindingResult.hasErrors();
	}
}
