package com.backend.babyspa.v1.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.babyspa.v1.config.TenantContext;
import com.backend.babyspa.v1.exceptions.NotFoundException;
import com.backend.babyspa.v1.models.PaymentType;
import com.backend.babyspa.v1.repositories.PaymentTypeRepository;

@Service
public class PaymentTypeService {

	@Autowired
	PaymentTypeRepository paymentTypeRepository;

	public PaymentType findById(Integer paymentTypeId) throws NotFoundException {

		return paymentTypeRepository.findById(paymentTypeId)
				.orElseThrow(() -> new NotFoundException("Nije pronađen tip plaćanja sa ID: " + paymentTypeId + "!"));
	}

	public List<PaymentType> findAll() {

		return paymentTypeRepository.findByTenantId(TenantContext.getTenant());
	}

}
