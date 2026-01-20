package com.backend.babyspa.v1.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import com.backend.babyspa.v1.exceptions.BusinessException;
import com.backend.babyspa.v1.specifications.BabySpecifications;
import com.backend.babyspa.v1.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.backend.babyspa.v1.config.TenantContext;
import com.backend.babyspa.v1.dtos.CreateBabyDto;
import com.backend.babyspa.v1.dtos.ShortDetailsDto;
import com.backend.babyspa.v1.dtos.UpdateBabyDto;
import com.backend.babyspa.v1.exceptions.NotFoundException;
import com.backend.babyspa.v1.models.Baby;
import com.backend.babyspa.v1.repositories.ArrangementRepository;
import com.backend.babyspa.v1.repositories.BabyRepository;

import jakarta.transaction.Transactional;

@Service
public class BabyService {

  @Autowired private BabyRepository babyRepository;

  @Autowired private ArrangementRepository arrangementRepository;

  @Autowired private SecurityUtil securityUtil;

  public Baby findById(Integer babyId) {
    return babyRepository
        .findById(babyId)
        .orElseThrow(() -> new NotFoundException("Nije pronadjena beba sa ID: " + babyId + "!"));
  }

  public Baby save(CreateBabyDto createBabyDto) {
    if (babyRepository.existsByPhoneNumberAndBabyNameAndIsDeleted(
        createBabyDto.getPhoneNumber(), createBabyDto.getBabyName(), false)) {
      throw new BusinessException("Ova beba je već unesena u sistem!");
    }
    Baby baby = new Baby();

    baby.setBabyName(createBabyDto.getBabyName());
    baby.setBabySurname(createBabyDto.getBabySurname());
    baby.setBirthDate(createBabyDto.getBirthDate());
    baby.setMotherName(createBabyDto.getMotherName());
    baby.setNote(createBabyDto.getNote());
    baby.setNumberOfMonths(createBabyDto.getNumberOfMonths());
    baby.setPhoneNumber(createBabyDto.getPhoneNumber());
    baby.setCreatedByUser(securityUtil.getCurrentUser());

    return babyRepository.save(baby);
  }

  public Baby update(UpdateBabyDto updateBabyDto) {
    if (babyRepository.existsByPhoneNumberAndBabyNameAndBabyIdNotAndIsDeleted(
        updateBabyDto.getPhoneNumber(),
        updateBabyDto.getBabyName(),
        updateBabyDto.getBabyId(),
        false)) {
      throw new BusinessException("Ova beba je već unesena u sistem!");
    }
    Baby baby = findById(updateBabyDto.getBabyId());

    baby.setBabyName(updateBabyDto.getBabyName());
    baby.setBabySurname(updateBabyDto.getBabySurname());
    baby.setBirthDate(updateBabyDto.getBirthDate());
    baby.setMotherName(updateBabyDto.getMotherName());
    baby.setNote(updateBabyDto.getNote());
    baby.setNumberOfMonths(updateBabyDto.getNumberOfMonths());
    baby.setPhoneNumber(updateBabyDto.getPhoneNumber());
    baby.setUpdatedByUser(securityUtil.getCurrentUser());

    return babyRepository.save(baby);
  }

  @Transactional
  public int delete(int babyId) {
    Baby baby = findById(babyId);

    if (arrangementRepository.existsByBabyAndIsDeleted(baby, false)) {
      throw new BusinessException(
          "Nije moguće obrisati bebu ako postoji aranžman kojem je dodijeljena.");
    }

    baby.setDeleted(true);
    baby.setDeletedAt(LocalDateTime.now());
    baby.setDeletedByUser(securityUtil.getCurrentUser());

    babyRepository.save(baby);
    return babyId;
  }

  public Page<Baby> findAllPage(
      String searchText, LocalDateTime start, LocalDateTime end, int page, int size) {
    if (start == null && end != null) {
      start = LocalDateTime.of(1999, 1, 1, 0, 0);
    } else if (start != null && end == null) {
      end = LocalDateTime.now().plusMinutes(15);
    }

    Pageable pageable = PageRequest.of(page, size, Sort.by("babyId").descending());

    Specification<Baby> spec =
        Specification.where(BabySpecifications.withSearchText(searchText))
            .and(BabySpecifications.dateRange(start, end))
            .and(BabySpecifications.tenantAndNotDeleted(TenantContext.getTenant()));

    return babyRepository.findAll(spec, pageable);
  }

  public List<ShortDetailsDto> findAllList() {
    return babyRepository.findByIsDeleted(false).stream()
        .map(this::buildShortDetailsDtoFromBaby)
        .toList();
  }

  public void updateMonthsForAll() {
    babyRepository.updateAllNumberOfMonths(LocalDateTime.now(), false);
  }

  private ShortDetailsDto buildShortDetailsDtoFromBaby(Baby baby) {
    ShortDetailsDto shortDetailsDto = new ShortDetailsDto();
    shortDetailsDto.setId(baby.getBabyId());
    shortDetailsDto.setValue(
        baby.getBabyName()
            + (Objects.nonNull(baby.getBabySurname()) ? " " + baby.getBabySurname() : "")
            + " ("
            + baby.getPhoneNumber()
            + " )");

    return shortDetailsDto;
  }
}
