package com.backend.babyspa.v1.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import com.backend.babyspa.v1.exceptions.BuisnessException;
import com.backend.babyspa.v1.utils.SecurityUtil;
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

import jakarta.transaction.Transactional;

@Service
public class BabyService {

    @Autowired
    BabyRepository babyRepository;

    @Autowired
    ArrangementRepository arrangementRepository;

    @Autowired
    SecurityUtil securityUtil;

    public Baby findById(Integer babyId) {
        return babyRepository.findById(babyId)
                .orElseThrow(() -> new NotFoundException("Nije pronadjena beba sa ID: " + babyId + "!"));
    }

    public Baby save(CreateBabyDto createBabyDto) {
        if (babyRepository.existsByPhoneNumberAndBabyNameAndTenantIdAndIsDeleted(createBabyDto.getPhoneNumber(),
                createBabyDto.getBabyName(), TenantContext.getTenant(), false)) {
            throw new BuisnessException("Ova beba je već unesena u sistem!");
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
        if (babyRepository.existsByPhoneNumberAndBabyNameAndTenantIdAndBabyIdNotAndIsDeleted(updateBabyDto.getPhoneNumber(),
                updateBabyDto.getBabyName(), TenantContext.getTenant(), updateBabyDto.getBabyId(), false)) {
            throw new BuisnessException("Ova beba je već unesena u sistem!");
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
            throw new BuisnessException("Nije moguće obrisati bebu ako postoji aranžman kojem je dodijeljena.");
        }

        baby.setDeleted(true);
        baby.setDeletedAt(LocalDateTime.now());
        baby.setDeletedByUser(securityUtil.getCurrentUser());

        babyRepository.save(baby);
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
            return babyRepository.findAllNativeWithoutDate(searchText, TenantContext.getTenant(), false, pageable);
        } else {
            return babyRepository.findAllNativeWithDate(searchText, start, end, TenantContext.getTenant(), false, pageable);
        }
    }

    public List<ShortDetailsDto> findAllList() {
        return babyRepository.findByTenantIdAndIsDeleted(TenantContext.getTenant(), false).stream()
                .map(this::buildShortDetailsDtoFromBaby).toList();
    }

    public void updateMonthsForAll() {
        babyRepository.updateAllNumberOfMonths(LocalDateTime.now(), false);
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
