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

    protected boolean hasErrors(BindingResult bindingResult) {
        return Objects.nonNull(bindingResult) && bindingResult.hasErrors();
    }
    
}
