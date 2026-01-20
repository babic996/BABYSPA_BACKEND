package com.backend.babyspa.v1.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import com.backend.babyspa.v1.exceptions.BusinessException;
import com.backend.babyspa.v1.models.*;
import com.backend.babyspa.v1.specifications.ArrangementSpecifications;
import com.backend.babyspa.v1.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.backend.babyspa.v1.config.TenantContext;
import com.backend.babyspa.v1.dtos.CreateArrangementDto;
import com.backend.babyspa.v1.dtos.FindAllArrangementDto;
import com.backend.babyspa.v1.dtos.ShortDetailsDto;
import com.backend.babyspa.v1.dtos.UpdateArrangementDto;
import com.backend.babyspa.v1.exceptions.NotFoundException;
import com.backend.babyspa.v1.repositories.ArrangementRepository;
import com.backend.babyspa.v1.repositories.ReservationRepository;
import com.backend.babyspa.v1.utils.DateTimeUtil;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArrangementService {

  @Autowired private ArrangementRepository arrangementRepository;

  @Autowired private ReservationRepository reservationRepository;

  @Autowired private ServicePackageService servicePackageService;

  @Autowired private DiscountService discountService;

  @Autowired private BabyService babyService;

  @Autowired private StatusService statusService;

  @Autowired private PaymentTypeService paymentTypeService;

  @Autowired private GiftCardService giftCardService;

  @Autowired private ArrangementAudService arrangementAudService;

  @Autowired private SecurityUtil securityUtil;

  private static final String createdStatus = "created";

  public Arrangement findById(int arrangementId) {
    return arrangementRepository
        .findById(arrangementId)
        .orElseThrow(
            () -> new NotFoundException("Nije pronađen aranžman sa ID: " + arrangementId + "!"));
  }

  @Transactional
  public Arrangement save(CreateArrangementDto dto) {
    Baby baby = babyService.findById(dto.getBabyId());
    Status status = statusService.findByStatusCode(createdStatus);
    ServicePackage servicePackage = servicePackageService.findById(dto.getServicePackageId());
    User actionByUser = securityUtil.getCurrentUser();

    Arrangement arrangement = new Arrangement();
    arrangement.setBaby(baby);
    arrangement.setServicePackage(servicePackage);
    arrangement.setStatus(status);
    arrangement.setCreatedByUser(actionByUser);
    arrangement.setNote(dto.getNote());
    arrangement.setRemainingTerm(servicePackage.getTermNumber());

    applyDiscountIfPresent(arrangement, servicePackage, dto.getDiscountId());
    applyGiftCardIfPresent(arrangement, dto.getGiftCardId());

    arrangementRepository.save(arrangement);
    arrangementAudService.save(arrangement, actionByUser, UserActionType.create);

    return arrangement;
  }

  @Transactional
  public FindAllArrangementDto update(UpdateArrangementDto dto) {
    Arrangement arrangement = findById(dto.getArrangementId());
    Arrangement arrangementBeforeUpdate = arrangement;

    Baby baby = arrangement.getBaby();
    ServicePackage servicePackage = arrangement.getServicePackage();

    // ako postoji rezervacija za aranzman, ne moze se mijenjati beba i paket usluga
    if (!reservationRepository.existsByArrangementAndIsDeleted(arrangement, false)) {
      baby = babyService.findById(dto.getBabyId());
      servicePackage = servicePackageService.findById(dto.getServicePackageId());
      arrangement.setRemainingTerm(servicePackage.getTermNumber());
    }

    Status status = statusService.findById(dto.getStatusId());

    applyPaymentType(arrangement, dto.getPaymentTypeId());
    applyDiscountIfPresent(arrangement, servicePackage, dto.getDiscountId());
    applyGiftCardIfPresent(arrangement, dto.getGiftCardId());

    arrangement.setBaby(baby);
    arrangement.setServicePackage(servicePackage);
    arrangement.setNote(dto.getNote());
    arrangement.setStatus(status);
    arrangement.setExtendDurationDays(dto.getExtendDurationDays());
    arrangement.setUpdatedByUser(securityUtil.getCurrentUser());

    arrangementRepository.save(arrangement);
    arrangementAudService.save(
        arrangementBeforeUpdate, securityUtil.getCurrentUser(), UserActionType.update);

    return buildFindAllArrangementDtoFromArrangement(arrangement);
  }

  private void applyPaymentType(Arrangement arrangement, Integer paymentTypeId) {
    if (paymentTypeId == null || paymentTypeId == 0) {
      arrangement.setPaymentType(null);
      return;
    }
    arrangement.setPaymentType(paymentTypeService.findById(paymentTypeId));
  }

  private void applyDiscountIfPresent(
      Arrangement arrangement, ServicePackage servicePackage, Integer discountId) {
    if (discountId == null || discountId == 0) {
      arrangement.setDiscount(null);
      arrangement.setPrice(servicePackage.getPrice());
      return;
    }

    Discount discount = discountService.findById(discountId);
    arrangement.setDiscount(discount);

    BigDecimal basePrice = servicePackage.getPrice();
    if (discount.isPrecentage()) {
      BigDecimal discountValue =
          basePrice
              .multiply(discount.getValue())
              .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
      arrangement.setPrice(basePrice.subtract(discountValue));
    } else {
      if (discount.getValue().compareTo(basePrice) > 0) {
        throw new BusinessException("Popust je veći od cijene paketa usluge!");
      }
      arrangement.setPrice(basePrice.subtract(discount.getValue()));
    }
  }

  private void applyGiftCardIfPresent(Arrangement arrangement, Integer giftCardId) {
    if (giftCardId == null) {
      if (arrangement.getGiftCard() != null) {
        giftCardService.updateGiftCardStatus(false, arrangement.getGiftCard());
      }
      arrangement.setGiftCard(null);
      return;
    }

    if (arrangement.getDiscount() == null) {
      throw new BusinessException("Morate izabrati popust da primijenite poklon karticu!");
    }

    GiftCard giftCard = giftCardService.findById(giftCardId);
    validateGiftCardExpiration(giftCard);

    if (arrangement.getGiftCard() != null && !giftCard.equals(arrangement.getGiftCard())) {
      giftCardService.updateGiftCardStatus(false, arrangement.getGiftCard());
    }
    arrangement.setGiftCard(giftCardService.updateGiftCardStatus(true, giftCard));
  }

  private void validateGiftCardExpiration(GiftCard giftCard) {
    if (giftCard.getExpirationDate() != null) {
      LocalDate expirationDate = giftCard.getExpirationDate().toLocalDate();
      if (LocalDate.now().isAfter(expirationDate)) {
        throw new BusinessException("Poklon kartica je istekla!");
      }
    }
  }

  @Transactional
  public void decreaseRemainingTerm(Arrangement arrangement) {
    arrangement.setRemainingTerm(arrangement.getRemainingTerm() - 1);

    arrangementRepository.save(arrangement);
  }

  @Transactional
  public void increaseRemainingTerm(Arrangement arrangement) {
    arrangement.setRemainingTerm(arrangement.getRemainingTerm() + 1);

    arrangementRepository.save(arrangement);
  }

  @Transactional
  public int delete(int arrangementId) {

    Arrangement arrangement = findById(arrangementId);

    if (reservationRepository.existsByArrangementAndIsDeleted(arrangement, false)) {

      throw new BusinessException(
          "Nije moguće obrisati aranžman ako ima rezervacija vezanih za njega!");
    }

    arrangement.setDeleted(true);
    arrangement.setDeletedAt(LocalDateTime.now());
    arrangement.setDeletedByUser(securityUtil.getCurrentUser());
    arrangementRepository.save(arrangement);
    arrangementAudService.save(arrangement, securityUtil.getCurrentUser(), UserActionType.delete);
    return arrangementId;
  }

  public boolean existsByServicePackage(int servicePackageId) {
    ServicePackage servicePackage = servicePackageService.findById(servicePackageId);

    return arrangementRepository.existsByServicePackageAndIsDeleted(servicePackage, false);
  }

  public Page<FindAllArrangementDto> findAll(
      int page,
      int size,
      Integer babyId,
      Integer statusId,
      Integer servicePackageId,
      Integer paymentTypeId,
      Integer giftCardId,
      Integer remainingTerm,
      BigDecimal startPrice,
      BigDecimal endPrice,
      Integer arrangementId,
      LocalDateTime startDate,
      LocalDateTime endDate) {

    if (startDate == null && endDate != null) {
      startDate = DateTimeUtil.getDateTimeFromString("1999-01-01 00:00:00");
    } else if (startDate != null && endDate == null) {
      endDate = LocalDateTime.now().plusMinutes(15);
    }

    Pageable pageable = PageRequest.of(page, size, Sort.by("arrangementId").descending());

    Specification<Arrangement> spec =
        Specification.where(
                ArrangementSpecifications.withFilters(
                    statusId,
                    babyId,
                    paymentTypeId,
                    giftCardId,
                    servicePackageId,
                    arrangementId,
                    remainingTerm,
                    startPrice,
                    endPrice))
            .and(ArrangementSpecifications.dateRange(startDate, endDate))
            .and(ArrangementSpecifications.tenantAndNotDeleted(TenantContext.getTenant()));

    Page<Arrangement> pageResult = arrangementRepository.findAll(spec, pageable);

    List<FindAllArrangementDto> dtos =
        pageResult.getContent().stream()
            .map(this::buildFindAllArrangementDtoFromArrangement)
            .toList();

    return new PageImpl<>(dtos, pageable, pageResult.getTotalElements());
  }

  public BigDecimal findTotalSum(
      Integer babyId,
      Integer statusId,
      Integer servicePackageId,
      Integer paymentTypeId,
      Integer giftCardId,
      Integer remainingTerm,
      BigDecimal startPrice,
      BigDecimal endPrice,
      Integer arrangementId,
      LocalDateTime startDate,
      LocalDateTime endDate) {

    if (startDate == null && endDate != null) {
      startDate = DateTimeUtil.getDateTimeFromString("1999-01-01 00:00:00");
    } else if (startDate != null && endDate == null) {
      endDate = LocalDateTime.now().plusMinutes(15);
    }

    return arrangementRepository.findTotalPriceSum(
        statusId,
        babyId,
        paymentTypeId,
        giftCardId,
        startPrice,
        endPrice,
        remainingTerm,
        servicePackageId,
        arrangementId,
        startDate,
        endDate,
        TenantContext.getTenant(),
        false);
  }

  public List<ShortDetailsDto> findAllArrangementList() {
    return arrangementRepository.findByRemainingTermGreaterThanAndIsDeleted(0, false).stream()
        .map(this::buildShortDetailsFromArrangement)
        .toList();
  }

  private ShortDetailsDto buildShortDetailsFromArrangement(Arrangement arrangement) {
    ShortDetailsDto shortDetailsDto = new ShortDetailsDto();

    shortDetailsDto.setId(arrangement.getArrangementId());
    shortDetailsDto.setValue(
        "("
            + arrangement.getArrangementId()
            + ") "
            + arrangement.getBaby().getBabyName()
            + (Objects.nonNull(arrangement.getBaby().getBabySurname())
                ? " " + arrangement.getBaby().getBabySurname()
                : "")
            + "("
            + arrangement.getBaby().getPhoneNumber()
            + ")"
            + " - "
            + arrangement.getServicePackage().getServicePackageName());

    return shortDetailsDto;
  }

  public FindAllArrangementDto buildFindAllArrangementDtoFromArrangement(Arrangement arrangement) {
    FindAllArrangementDto findAllArrangementDto = new FindAllArrangementDto();
    findAllArrangementDto.setArrangementId(arrangement.getArrangementId());
    findAllArrangementDto.setCreatedAt(arrangement.getCreatedAt());
    findAllArrangementDto.setNote(arrangement.getNote());
    findAllArrangementDto.setPrice(arrangement.getPrice());
    findAllArrangementDto.setRemainingTerm(arrangement.getRemainingTerm());
    findAllArrangementDto.setBabyDetails(
        new ShortDetailsDto(
            arrangement.getBaby().getBabyId(),
            arrangement.getBaby().getBabyName()
                + (Objects.nonNull(arrangement.getBaby().getBabySurname())
                    ? " " + arrangement.getBaby().getBabySurname()
                    : "")
                + " ("
                + arrangement.getBaby().getPhoneNumber()
                + ")"));
    if (arrangement.getDiscount() != null) {
      String value;
      if (arrangement.getDiscount().isPrecentage()) {
        value = arrangement.getDiscount().getValue().toString() + "%";
      } else {
        value = arrangement.getDiscount().getValue().toString() + "KM";
      }
      findAllArrangementDto.setDiscount(
          new ShortDetailsDto(arrangement.getDiscount().getDiscountId(), value));
    }
    findAllArrangementDto.setServicePackage(
        new ShortDetailsDto(
            arrangement.getServicePackage().getServicePackageId(),
            arrangement.getServicePackage().getServicePackageName()));
    findAllArrangementDto.setStatus(
        new ShortDetailsDto(
            arrangement.getStatus().getStatusId(), arrangement.getStatus().getStatusCode()));
    if (arrangement.getPaymentType() != null) {
      findAllArrangementDto.setPaymentType(
          new ShortDetailsDto(
              arrangement.getPaymentType().getPaymentTypeId(),
              arrangement.getPaymentType().getPaymentTypeName()));
    }
    findAllArrangementDto.setExtendDurationDays(arrangement.getExtendDurationDays());
    if (arrangement.getGiftCard() != null) {
      findAllArrangementDto.setGiftCard(
          new ShortDetailsDto(
              arrangement.getGiftCard().getGiftCardId(),
              arrangement.getGiftCard().getSerialNumber()));
    }

    return findAllArrangementDto;
  }
}
