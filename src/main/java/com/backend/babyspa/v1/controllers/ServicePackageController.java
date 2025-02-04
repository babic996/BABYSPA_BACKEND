package com.backend.babyspa.v1.controllers;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

import com.backend.babyspa.v1.dtos.CreateServicePackageDto;
import com.backend.babyspa.v1.dtos.ShortDetailsDto;
import com.backend.babyspa.v1.dtos.UpdateServicePackageDto;
import com.backend.babyspa.v1.exceptions.NotFoundException;
import com.backend.babyspa.v1.models.ServicePackage;
import com.backend.babyspa.v1.services.ServicePackageService;
import com.backend.babyspa.v1.utils.ApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/service-package")
public class ServicePackageController extends BaseController {

	@Autowired
	ServicePackageService servicePackageService;

	@GetMapping("/find-by-id")
	public ServicePackage findById(@RequestParam Integer servicePackageId) throws NotFoundException {

		return servicePackageService.findById(servicePackageId);
	}

	@PostMapping("/save")
	public ResponseEntity<ApiResponse<ServicePackage>> save(
			@RequestBody @Valid CreateServicePackageDto createServicePackageDto, BindingResult bindingResult) {

		if (hasErrors(bindingResult)) {
			return createErrorResponse(bindingResult);
		}

		try {
			ServicePackage servicePackage = servicePackageService.save(createServicePackageDto);
			return createSuccessResponse(servicePackage);
		} catch (Exception e) {
			return createExceptionResponse(e);
		}

	}

	@PutMapping("/update")
	public ResponseEntity<ApiResponse<ServicePackage>> update(
			@RequestBody @Valid UpdateServicePackageDto updateServicePackageDto, BindingResult bindingResult) {

		if (hasErrors(bindingResult)) {
			return createErrorResponse(bindingResult);
		}
		try {
			ServicePackage servicePackage = servicePackageService.update(updateServicePackageDto);
			return createSuccessResponse(servicePackage);
		} catch (Exception e) {
			return createExceptionResponse(e);
		}
	}

	@DeleteMapping("/delete")
	public ResponseEntity<ApiResponse<Integer>> delete(@RequestParam int servicePackageId) {

		try {
			int deletedServicePackageId = servicePackageService.delete(servicePackageId);
			return createSuccessResponse(deletedServicePackageId);
		} catch (Exception e) {
			return createExceptionResponse(e);
		}
	}

	@GetMapping("/find-all-list")
	public ResponseEntity<ApiResponse<List<ShortDetailsDto>>> findAllList() {

		try {
			return createSuccessResponse(servicePackageService.findAllList());
		} catch (Exception e) {
			return createExceptionResponse(e);
		}
	}

	@GetMapping("/find-all")
	public ResponseEntity<ApiResponse<Page<ServicePackage>>> findAll(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String searchText,
			@RequestParam(required = false) BigDecimal startPrice,
			@RequestParam(required = false) BigDecimal endPrice) {

		try {
			return createSuccessResponse(servicePackageService.findAll(page, size, searchText, startPrice, endPrice));
		} catch (Exception e) {
			return createExceptionResponse(e);
		}
	}

	@GetMapping("/find-max-price")
	public ResponseEntity<ApiResponse<Double>> findMaxPrice() {

		try {
			return createSuccessResponse(servicePackageService.findMaxPriceServicePackage());
		} catch (Exception e) {
			return createExceptionResponse(e);
		}
	}

}
