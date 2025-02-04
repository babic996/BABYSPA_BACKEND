package com.backend.babyspa.v1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.babyspa.v1.exceptions.NotFoundException;
import com.backend.babyspa.v1.models.StatusType;
import com.backend.babyspa.v1.repositories.StatusTypeRepository;

@Service
public class StatusTypeService {

	@Autowired
	StatusTypeRepository statusTypeRepository;

	public StatusType findById(int statusTypeId) throws NotFoundException {

		return statusTypeRepository.findById(statusTypeId)
				.orElseThrow(() -> new NotFoundException("Nije pronadjen tip status sa ID: " + statusTypeId + "!"));
	}

}
