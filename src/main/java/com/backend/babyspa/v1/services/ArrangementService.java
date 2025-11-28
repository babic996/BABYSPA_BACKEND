package com.backend.babyspa.v1.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import com.backend.babyspa.v1.exceptions.BusinessException;
import com.backend.babyspa.v1.models.*;
import com.backend.babyspa.v1.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

import jakarta.transaction.Transactional;

@Service
public class ArrangementService {

    @Autowired
    ArrangementRepository arrangementRepository;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    ServicePackageService servicePackageService;

    @Autowired
    DiscountService discountService;

    @Autowired
    BabyService babyService;

    @Autowired
    StatusService statusService;

    @Autowired
    PaymentTypeService paymentTypeService;

    @Autowired
    UserService userService;

    @Autowired
    GiftCardService giftCardService;

    @Autowired
    ArrangementAudService arrangementAudService;

    @Autowired
    SecurityUtil securityUtil;

    private final String createdStatus = "created";

    public Arrangement findById(int arrangementId) {
        return arrangementRepository.findById(arrangementId)
                .orElseThrow(() -> new NotFoundException("Nije pronadjen aranzman sa id: " + arrangementId + "!"));
    }

    @Transactional
    public Arrangement save(CreateArrangementDto createArrangementDto) {
        Arrangement arrangement = new Arrangement();
        Baby baby = babyService.findById(createArrangementDto.getBabyId());
        Status status = statusService.findByStatusCode(createdStatus);
        ServicePackage servicePackage = servicePackageService.findById(createArrangementDto.getServicePackageId());

        if (Objects.nonNull(createArrangementDto.getDiscountId())) {
            Discount discount = discountService.findById(createArrangementDto.getDiscountId());
            arrangement.setDiscount(discount);
            if (discount.isPrecentage()) {
                BigDecimal discountValue = servicePackage.getPrice().multiply(discount.getValue())
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                arrangement.setPrice(servicePackage.getPrice().subtract(discountValue));
            } else {
                if (discount.getValue().compareTo(servicePackage.getPrice()) > 0) {
                    throw new BusinessException("Popust je veći od cijene paketa usluge!");
                } else {
                    arrangement.setPrice(servicePackage.getPrice().subtract(discount.getValue()));
                }
            }
        } else {
            arrangement.setPrice(servicePackage.getPrice());
        }

        if (Objects.nonNull(createArrangementDto.getGiftCardId())) {
            if (Objects.isNull(arrangement.getDiscount())) {
                throw new BusinessException("Morate izabrati popust!");
            } else {
                arrangement.setGiftCard(giftCardService.findById(createArrangementDto.getGiftCardId()));
            }
        }

        arrangement.setNote(createArrangementDto.getNote());
        arrangement.setBaby(baby);
        arrangement.setRemainingTerm(servicePackage.getTermNumber());
        arrangement.setServicePackage(servicePackage);
        arrangement.setStatus(status);
        arrangement.setCreatedByUser(securityUtil.getCurrentUser());

        arrangementRepository.save(arrangement);
        arrangementAudService.save(arrangement, securityUtil.getCurrentUser(), UserActionType.create);

        return arrangement;
    }

    @Transactional
    public FindAllArrangementDto update(UpdateArrangementDto updateArrangementDto) {
        Arrangement arrangement = findById(updateArrangementDto.getArrangementId());
        Baby baby;
        ServicePackage servicePackage;
        Arrangement arrangementBeforeUpdate = arrangement;

        // ako postoji rezervacija moze mijenjati samo status, discount i paymentType

        if (reservationRepository.existsByArrangementAndIsDeleted(arrangement, false)) {
            baby = arrangement.getBaby();
            servicePackage = arrangement.getServicePackage();
            arrangement.setRemainingTerm(arrangement.getRemainingTerm());
        } else {
            baby = babyService.findById(updateArrangementDto.getBabyId());
            servicePackage = servicePackageService.findById(updateArrangementDto.getServicePackageId());
            arrangement.setRemainingTerm(servicePackage.getTermNumber());
        }

        Status status = statusService.findById(updateArrangementDto.getStatusId());

        if (!Objects.equals(updateArrangementDto.getPaymentTypeId(), 0)
                && Objects.nonNull(updateArrangementDto.getPaymentTypeId())) {
            PaymentType paymentType = paymentTypeService.findById(updateArrangementDto.getPaymentTypeId());
            arrangement.setPaymentType(paymentType);
        } else {
            arrangement.setPaymentType(null);
        }

        if (!Objects.equals(updateArrangementDto.getDiscountId(), 0)
                && Objects.nonNull(updateArrangementDto.getDiscountId())) {
            if (Objects.nonNull(arrangement.getDiscount())) {
                arrangement.setDiscount(null);
                arrangement.setPrice(servicePackage.getPrice());
            }
            Discount discount = discountService.findById(updateArrangementDto.getDiscountId());
            arrangement.setDiscount(discount);
            if (discount.isPrecentage()) {
                BigDecimal discountValue = servicePackage.getPrice().multiply(discount.getValue())
                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
                arrangement.setPrice(servicePackage.getPrice().subtract(discountValue));

            } else {
                if (discount.getValue().compareTo(arrangement.getPrice()) > 0) {
                    throw new BusinessException("Popust je veći od cijene aranžmana!");
                } else {
                    arrangement.setPrice(servicePackage.getPrice().subtract(discount.getValue()));
                }
            }
        } else {
            arrangement.setPrice(servicePackage.getPrice());
            arrangement.setDiscount(null);
        }

        if (Objects.nonNull(updateArrangementDto.getGiftCardId())) {
            if (Objects.isNull(arrangement.getDiscount())) {
                throw new BusinessException("Morate izabrati popust!");
            }
            GiftCard giftCard = giftCardService.findById(updateArrangementDto.getGiftCardId());
            if (Objects.nonNull(giftCard.getExpirationDate())) {
                LocalDate expirationDate = giftCard.getExpirationDate().toLocalDate();
                LocalDate today = LocalDate.now();
                if (today.isAfter(expirationDate)) {
                    throw new BusinessException("Poklon kartica je istekla!");
                }
            }
            if (Objects.nonNull(arrangement.getGiftCard()) && !giftCard.equals(arrangement.getGiftCard())) {
                giftCardService.updateGiftCardStatus(false, arrangement.getGiftCard());
            }
            arrangement.setGiftCard(giftCardService.updateGiftCardStatus(true, giftCard));
        } else {
            if (Objects.nonNull(arrangement.getGiftCard())) {
                giftCardService.updateGiftCardStatus(false, arrangement.getGiftCard());
            }
            arrangement.setGiftCard(null);
        }

        arrangement.setBaby(baby);
        arrangement.setServicePackage(servicePackage);
        arrangement.setNote(updateArrangementDto.getNote());
        arrangement.setStatus(status);
        arrangement.setExtendDurationDays(updateArrangementDto.getExtendDurationDays());
        arrangement.setUpdatedByUser(securityUtil.getCurrentUser());

        arrangementRepository.save(arrangement);
        arrangementAudService.save(arrangementBeforeUpdate, securityUtil.getCurrentUser(), UserActionType.update);
        return buildFindAllArrangementDtoFromArrangement(arrangement);
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

            throw new BusinessException("Nije moguće obrisati aranžman ako ima rezervacija vezanih za njega!");
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

    public Page<FindAllArrangementDto> findAll(int page, int size, Integer babyId, Integer statusId,
                                               Integer servicePackageId, Integer paymentTypeId, Integer giftCardId, Integer remaingingTerm, BigDecimal startPrice,
                                               BigDecimal endPrice, Integer arrangementId, LocalDateTime startDate, LocalDateTime endDate) {
        List<FindAllArrangementDto> arrangementDto;
        List<Arrangement> arrangement;

        if (Objects.isNull(startDate) && Objects.nonNull(endDate)) {
            startDate = DateTimeUtil.getDateTimeFromString("1999-01-01 00:00:00");
        } else if (Objects.nonNull(startDate) && Objects.isNull(endDate)) {
            endDate = LocalDateTime.now().plusMinutes(15);
        }

        if (Objects.isNull(startDate) && Objects.isNull(endDate)) {
            arrangement = arrangementRepository.findAllArrangementNative(statusId, babyId, paymentTypeId, giftCardId, startPrice,
                    endPrice, remaingingTerm, servicePackageId, arrangementId, TenantContext.getTenant(), false);
        } else {
            arrangement = arrangementRepository.findAllArrangementNativeWithStartDateAndDate(statusId, babyId,
                    paymentTypeId, giftCardId, startPrice, endPrice, remaingingTerm, servicePackageId, arrangementId, startDate,
                    endDate, TenantContext.getTenant(), false);
        }

        arrangementDto = arrangement.stream().map(this::buildFindAllArrangementDtoFromArrangement)
                .toList();

        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), arrangementDto.size());
        if (start > end) {
            start = end = 0;
        }

        final Page<FindAllArrangementDto> pageItem = new PageImpl<>(arrangementDto.subList(start, end), pageable,
                arrangementDto.size());

        return pageItem;
    }

    public BigDecimal findTotalSum(Integer babyId, Integer statusId, Integer servicePackageId, Integer paymentTypeId, Integer giftCardId,
                                   Integer remaingingTerm, BigDecimal startPrice, BigDecimal endPrice, Integer arrangementId,
                                   LocalDateTime startDate, LocalDateTime endDate) {
        if (Objects.isNull(startDate) && Objects.nonNull(endDate)) {
            startDate = DateTimeUtil.getDateTimeFromString("1999-01-01 00:00:00");
        } else if (Objects.nonNull(startDate) && Objects.isNull(endDate)) {
            endDate = LocalDateTime.now().plusMinutes(15);
        }

        BigDecimal price;

        if (Objects.isNull(startDate) && Objects.isNull(endDate)) {
            price = arrangementRepository.findPriceForAllArrangementNative(statusId, babyId, paymentTypeId, giftCardId, startPrice,
                    endPrice, remaingingTerm, servicePackageId, arrangementId, TenantContext.getTenant(), false);
        } else {
            price = arrangementRepository.findPriceForAllArrangementNativeWithStartDateAndDate(statusId, babyId,
                    paymentTypeId, giftCardId, startPrice, endPrice, remaingingTerm, servicePackageId, arrangementId, startDate,
                    endDate, TenantContext.getTenant(), false);
        }

        return price;
    }

    public List<ShortDetailsDto> findAllArrangementList() {
        return arrangementRepository
                .findByRemainingTermGreaterThanAndIsDeleted(0, false).stream()
                .map(this::buildShortDetailsFromArrangement).toList();
    }

    private ShortDetailsDto buildShortDetailsFromArrangement(Arrangement arrangement) {
        ShortDetailsDto shortDetailsDto = new ShortDetailsDto();

        shortDetailsDto.setId(arrangement.getArrangementId());
        shortDetailsDto.setValue("(" + arrangement.getArrangementId() + ") " + arrangement.getBaby().getBabyName()
                + (Objects.nonNull(arrangement.getBaby().getBabySurname())
                ? " " + arrangement.getBaby().getBabySurname()
                : "")
                + "(" + arrangement.getBaby().getPhoneNumber() + ")" + " - "
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
        findAllArrangementDto.setBabyDetails(new ShortDetailsDto(arrangement.getBaby().getBabyId(),
                arrangement.getBaby().getBabyName() + (Objects.nonNull(arrangement.getBaby().getBabySurname())
                        ? " " + arrangement.getBaby().getBabySurname()
                        : "") + " (" + arrangement.getBaby().getPhoneNumber() + ")"));
        if (Objects.nonNull(arrangement.getDiscount())) {
            String value;
            if (arrangement.getDiscount().isPrecentage()) {
                value = arrangement.getDiscount().getValue().toString() + "%";
            } else {
                value = arrangement.getDiscount().getValue().toString() + "KM";
            }
            findAllArrangementDto.setDiscount(new ShortDetailsDto(arrangement.getDiscount().getDiscountId(), value));
        }
        findAllArrangementDto
                .setServicePackage(new ShortDetailsDto(arrangement.getServicePackage().getServicePackageId(),
                        arrangement.getServicePackage().getServicePackageName()));
        findAllArrangementDto.setStatus(
                new ShortDetailsDto(arrangement.getStatus().getStatusId(), arrangement.getStatus().getStatusCode()));
        if (Objects.nonNull(arrangement.getPaymentType())) {
            findAllArrangementDto.setPaymentType(new ShortDetailsDto(arrangement.getPaymentType().getPaymentTypeId(),
                    arrangement.getPaymentType().getPaymentTypeName()));
        }
        findAllArrangementDto.setExtendDurationDays(arrangement.getExtendDurationDays());
        if (Objects.nonNull(arrangement.getGiftCard())) {
            findAllArrangementDto.setGiftCard(new ShortDetailsDto(arrangement.getGiftCard().getGiftCardId(), arrangement.getGiftCard().getSerialNumber()));
        }

        return findAllArrangementDto;
    }

}
