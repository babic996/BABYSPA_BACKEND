package com.backend.babyspa.v1.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.babyspa.v1.models.PaymentType;
import com.backend.babyspa.v1.services.PaymentTypeService;
import com.backend.babyspa.v1.utils.ApiResponse;

@RestController
@RequestMapping("/payment-type")
public class PaymentTypeController extends BaseController {

	@Autowired
	PaymentTypeService paymentTypeService;

	@GetMapping("/find-all")
	public ResponseEntity<ApiResponse<List<PaymentType>>> findAll() {

		return createSuccessResponse(paymentTypeService.findAll());
	}

}
