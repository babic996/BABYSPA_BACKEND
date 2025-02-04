package com.backend.babyspa.v1.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.backend.babyspa.v1.config.TenantContext;
import com.backend.babyspa.v1.dtos.CreateBabyDto;
import com.backend.babyspa.v1.dtos.ShortDetailsDto;
import com.backend.babyspa.v1.dtos.UpdateBabyDto;
import com.backend.babyspa.v1.exceptions.NotFoundException;
import com.backend.babyspa.v1.models.Baby;
import com.backend.babyspa.v1.repositories.ArrangementRepository;
import com.backend.babyspa.v1.repositories.BabyRepository;
import com.backend.babyspa.v1.utils.DateTimeUtil;
import com.backend.babyspa.v1.utils.SecurityUtil;

import jakarta.transaction.Transactional;

@Service
public class BabyService {

	@Autowired
	BabyRepository babyRepository;

	@Autowired
	ArrangementRepository arrangementRepository;

	@Autowired
	UserService userService;

	public Baby findById(Integer babyId) throws NotFoundException {

		Baby baby = babyRepository.findById(babyId)
				.orElseThrow(() -> new NotFoundException("Nije pronadjena beba sa ID: " + babyId + "!"));

		return baby;
	}

	public Baby save(CreateBabyDto createBabyDto) throws Exception {
		Baby baby = new Baby();

		if (babyRepository.existsByPhoneNumberAndBabyNameAndTenantId(createBabyDto.getPhoneNumber(),
				createBabyDto.getBabyName(), TenantContext.getTenant())) {
			throw new Exception("Ova beba je već unesena u sistem!");
		}

		baby.setBabyName(createBabyDto.getBabyName());
		baby.setBabySurname(createBabyDto.getBabySurname());
		baby.setBirthDate(createBabyDto.getBirthDate());
		baby.setMotherName(createBabyDto.getMotherName());
		baby.setNote(createBabyDto.getNote());
		baby.setNumberOfMonths(createBabyDto.getNumberOfMonths());
		baby.setPhoneNumber(createBabyDto.getPhoneNumber());
		baby.setCreatedByUser(SecurityUtil.getCurrentUser(userService));

		return babyRepository.save(baby);
	}

	public Baby update(UpdateBabyDto updateBabyDto) throws Exception {

		Baby baby = findById(updateBabyDto.getBabyId());

		if (babyRepository.existsByPhoneNumberAndBabyNameAndTenantIdAndBabyIdNot(updateBabyDto.getPhoneNumber(),
				updateBabyDto.getBabyName(), TenantContext.getTenant(), updateBabyDto.getBabyId())) {
			throw new Exception("Ova beba je već unesena u sistem!");
		}

		baby.setBabyName(updateBabyDto.getBabyName());
		baby.setBabySurname(updateBabyDto.getBabySurname());
		baby.setBirthDate(updateBabyDto.getBirthDate());
		baby.setMotherName(updateBabyDto.getMotherName());
		baby.setNote(updateBabyDto.getNote());
		baby.setNumberOfMonths(updateBabyDto.getNumberOfMonths());
		baby.setPhoneNumber(updateBabyDto.getPhoneNumber());
		baby.setUpdatedByUser(SecurityUtil.getCurrentUser(userService));

		return babyRepository.save(baby);

	}

	@Transactional
	public int delete(int babyId) throws NotFoundException, Exception {

		Baby baby = findById(babyId);
		if (arrangementRepository.existsByBaby(baby)) {
			throw new Exception("Nije moguće obrisati bebu ako postoji aranžman kojem je dodijeljena.");
		}

		babyRepository.delete(baby);
		return babyId;
	}

	public Page<Baby> findAllByQueryParametars(String searchText, LocalDateTime start, LocalDateTime end, int page,
			int size) {

		if (Objects.isNull(start) && Objects.nonNull(end)) {
			start = DateTimeUtil.getDateTimeFromString("1999-01-01 00:00:00");
		} else if (Objects.nonNull(start) && Objects.isNull(end)) {
			end = LocalDateTime.now().plusMinutes(15);
		}

		Pageable pageable = PageRequest.of(page, size);

		if (Objects.isNull(start) && Objects.isNull(end)) {
			return babyRepository.findAllNativeWithoutDate(searchText, TenantContext.getTenant(), pageable);
		} else {
			return babyRepository.findAllNativeWithDate(searchText, start, end, TenantContext.getTenant(), pageable);
		}
	}

	public List<ShortDetailsDto> findAllList() {

		return babyRepository.findAll().stream().map(x -> buildShortDetailsDtoFromBaby(x)).collect(Collectors.toList());
	}

	private ShortDetailsDto buildShortDetailsDtoFromBaby(Baby baby) {

		ShortDetailsDto shortDetailsDto = new ShortDetailsDto();
		shortDetailsDto.setId(baby.getBabyId());
		shortDetailsDto.setValue(
				baby.getBabyName() + (Objects.nonNull(baby.getBabySurname()) ? " " + baby.getBabySurname() : "") + " ("
						+ baby.getPhoneNumber() + " )");

		return shortDetailsDto;
	}
}
