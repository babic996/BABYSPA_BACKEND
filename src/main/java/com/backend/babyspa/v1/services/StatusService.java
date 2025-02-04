package com.backend.babyspa.v1.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.babyspa.v1.dtos.CreateStatusDto;
import com.backend.babyspa.v1.dtos.UpdateStatusDto;
import com.backend.babyspa.v1.exceptions.NotFoundException;
import com.backend.babyspa.v1.models.Status;
import com.backend.babyspa.v1.models.StatusType;
import com.backend.babyspa.v1.repositories.StatusRepository;

import jakarta.transaction.Transactional;

@Service
public class StatusService {

	@Autowired
	StatusRepository statusRepository;

	@Autowired
	StatusTypeService statusTypeService;

	public Status findById(int statusId) throws NotFoundException {

		return statusRepository.findById(statusId)
				.orElseThrow(() -> new NotFoundException("Nije pronadjen status sa id: " + statusId + "!"));
	}

	public Status findByStatusCode(String statusCode) throws NotFoundException {

		return statusRepository.findByStatusCode(statusCode)
				.orElseThrow(() -> new NotFoundException("Nije pronadjen status sa kodom: " + statusCode + "!"));
	}

	public Status save(CreateStatusDto createStatusDto) throws Exception {

		Status status = new Status();
		StatusType statusType = statusTypeService.findById(createStatusDto.getStatusTypeId());

		if (statusRepository.existsByStatusNameAndStatusCodeAndStatusType(createStatusDto.getStatusName(),
				createStatusDto.getStatusCode(), statusType)) {
			throw new Exception("Postoji status sa ovom kombinacijom imena i koda!");
		}

		status.setStatusCode(createStatusDto.getStatusCode());
		status.setStatusName(createStatusDto.getStatusName());
		status.setStatusType(statusType);

		return statusRepository.save(status);
	}

	public Status update(UpdateStatusDto updateStatusDto) throws Exception {

		Status status = findById(updateStatusDto.getStatusId());
		StatusType statusType = statusTypeService.findById(updateStatusDto.getStatusTypeId());

		if (statusRepository.existsByStatusNameAndStatusCodeAndStatusTypeAndStatusIdNot(updateStatusDto.getStatusName(),
				updateStatusDto.getStatusCode(), statusType, status.getStatusId())) {
			throw new Exception("Postoji status sa ovom kombinacijom imena i koda!");
		}

		status.setStatusCode(updateStatusDto.getStatusCode());
		status.setStatusName(updateStatusDto.getStatusName());
		status.setStatusType(statusType);

		return statusRepository.save(status);
	}

	@Transactional
	public int delete(int statusId) throws NotFoundException {

		Status status = findById(statusId);

		statusRepository.delete(status);
		return statusId;
	}

	public List<Status> findAll() {

		return statusRepository.findAll();
	}

	public List<Status> findAllByStatusTypeCode(String statusTypeCode) {

		return statusRepository.findByStatusType_StatusTypeCode(statusTypeCode);
	}

}
