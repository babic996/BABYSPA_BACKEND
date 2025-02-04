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

import com.backend.babyspa.v1.dtos.CreateDiscountDto;
import com.backend.babyspa.v1.dtos.UpdateDiscountDto;
import com.backend.babyspa.v1.models.Discount;
import com.backend.babyspa.v1.services.DiscountService;
import com.backend.babyspa.v1.utils.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/discount")
public class DiscountController extends BaseController {

	@Autowired
	DiscountService discountService;

	@GetMapping("/find-by-id")
	public ResponseEntity<ApiResponse<Discount>> findById(@RequestParam int discountId) {

		try {
			return createSuccessResponse(discountService.findById(discountId));
		} catch (Exception e) {
			return createExceptionResponse(e);
		}
	}

	@PostMapping("/save")
	public ResponseEntity<ApiResponse<Discount>> save(@RequestBody @Valid CreateDiscountDto createDiscountDto,
			BindingResult bindingResult) {

		if (hasErrors(bindingResult)) {
			return createErrorResponse(bindingResult);
		}

		try {
			Discount discount = discountService.save(createDiscountDto);
			return createSuccessResponse(discount);
		} catch (Exception e) {
			return createExceptionResponse(e);
		}

	}

	@PutMapping("/update")
	public ResponseEntity<ApiResponse<Discount>> update(@RequestBody @Valid UpdateDiscountDto updateDiscountDto,
			BindingResult bindingResult) {

		if (hasErrors(bindingResult)) {
			return createErrorResponse(bindingResult);
		}
		try {
			Discount discount = discountService.update(updateDiscountDto);
			return createSuccessResponse(discount);
		} catch (Exception e) {
			return createExceptionResponse(e);
		}
	}

	@DeleteMapping("/delete")
	public ResponseEntity<ApiResponse<Integer>> delete(@RequestParam int discountId) {

		try {
			int deletedDiscountId = discountService.delete(discountId);
			return createSuccessResponse(deletedDiscountId);
		} catch (Exception e) {
			return createExceptionResponse(e);
		}
	}

	@GetMapping("/find-all")
	public ResponseEntity<ApiResponse<List<Discount>>> findAll() {

		try {
			return createSuccessResponse(discountService.findAll());
		} catch (Exception e) {
			return createExceptionResponse(e);
		}
	}
}
