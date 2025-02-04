package com.backend.babyspa.v1.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
import com.backend.babyspa.v1.models.Arrangement;
import com.backend.babyspa.v1.models.Baby;
import com.backend.babyspa.v1.models.Discount;
import com.backend.babyspa.v1.models.PaymentType;
import com.backend.babyspa.v1.models.ServicePackage;
import com.backend.babyspa.v1.models.Status;
import com.backend.babyspa.v1.repositories.ArrangementRepository;
import com.backend.babyspa.v1.repositories.ReservationRepository;
import com.backend.babyspa.v1.utils.DateTimeUtil;
import com.backend.babyspa.v1.utils.SecurityUtil;

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

	private final String createdStatus = "created";

	public Arrangement findById(int arrangementId) throws NotFoundException {

		return arrangementRepository.findById(arrangementId)
				.orElseThrow(() -> new NotFoundException("Nije pronadjen aranzman sa id: " + arrangementId + "!"));
	}

	public Arrangement save(CreateArrangementDto createArrangementDto) throws Exception {

		Arrangement arrangement = new Arrangement();
		Baby baby = babyService.findById(createArrangementDto.getBabyId());
		Status status = statusService.findByStatusCode(createdStatus);
		ServicePackage servicePackage = servicePackageService.findById(createArrangementDto.getServicePackageId());

		if (Objects.nonNull(createArrangementDto.getDiscountId())
				&& !Objects.equals(createArrangementDto.getDiscountId(), 0)) {
			Discount discount = discountService.findById(createArrangementDto.getDiscountId());
			arrangement.setDiscount(discount);
			if (discount.isPrecentage()) {
				BigDecimal discountValue = servicePackage.getPrice().multiply(discount.getValue())
						.divide(new BigDecimal(100));
				arrangement.setPrice(servicePackage.getPrice().subtract(discountValue));

			} else {
				if (discount.getValue().compareTo(servicePackage.getPrice()) > 0) {
					throw new Exception("Popust je veći od cijene aranžmana!");
				} else {
					arrangement.setPrice(servicePackage.getPrice().subtract(discount.getValue()));
				}
			}
		} else {
			arrangement.setPrice(servicePackage.getPrice());
		}

		arrangement.setNote(createArrangementDto.getNote());
		arrangement.setBaby(baby);
		arrangement.setRemainingTerm(servicePackage.getTermNumber());
		arrangement.setServicePackage(servicePackage);
		arrangement.setStatus(status);
		arrangement.setCreatedByUser(SecurityUtil.getCurrentUser(userService));

		return arrangementRepository.save(arrangement);
	}

	public FindAllArrangementDto update(UpdateArrangementDto updateArrangementDto) throws NotFoundException, Exception {

		Arrangement arrangement = findById(updateArrangementDto.getArrangementId());
		Baby baby = new Baby();
		ServicePackage servicePackage = new ServicePackage();

		// ako postoji rezervacija moze mijenjati samo status, discount i paymentType

		if (reservationRepository.existsByArrangement(arrangement)) {
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
			Discount discount = discountService.findById(updateArrangementDto.getDiscountId());
			arrangement.setDiscount(discount);
			if (discount.isPrecentage()) {
				BigDecimal discountValue = servicePackage.getPrice().multiply(discount.getValue())
						.divide(new BigDecimal(100));
				arrangement.setPrice(servicePackage.getPrice().subtract(discountValue));

			} else {
				if (discount.getValue().compareTo(arrangement.getPrice()) > 0) {
					throw new Exception("Popust je veći od cijene aranžmana!");
				} else {
					arrangement.setPrice(servicePackage.getPrice().subtract(discount.getValue()));
				}
			}
		} else {
			arrangement.setPrice(servicePackage.getPrice());
			arrangement.setDiscount(null);
		}

		arrangement.setBaby(baby);
		arrangement.setServicePackage(servicePackage);
		arrangement.setNote(updateArrangementDto.getNote());
		arrangement.setStatus(status);
		arrangement.setExtendDurationDays(updateArrangementDto.getExtendDurationDays());
		arrangement.setUpdatedByUser(SecurityUtil.getCurrentUser(userService));

		arrangementRepository.save(arrangement);
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
	public int delete(int arrangementId) throws NotFoundException, Exception {

		Arrangement arrangement = findById(arrangementId);

		if (reservationRepository.existsByArrangement(arrangement)) {

			throw new Exception("Nije moguće obrisati aranžman ako ima rezervacija vezanih za njega!");
		}

		arrangementRepository.delete(arrangement);
		return arrangementId;
	}

	public boolean existsByServicePackage(int servicePackageId) {
		ServicePackage servicePackage = servicePackageService.findById(servicePackageId);

		return arrangementRepository.existsByServicePackage(servicePackage);
	}

	public Page<FindAllArrangementDto> findAll(int page, int size, Integer babyId, Integer statusId,
			Integer servicePackageId, Integer paymentTypeId, Integer remaingingTerm, BigDecimal startPrice,
			BigDecimal endPrice, Integer arrangementId, LocalDateTime startDate, LocalDateTime endDate) {

		List<FindAllArrangementDto> arrangementDto = new ArrayList<FindAllArrangementDto>();
		List<Arrangement> arrangement = new ArrayList<Arrangement>();

		if (Objects.isNull(startDate) && Objects.nonNull(endDate)) {
			startDate = DateTimeUtil.getDateTimeFromString("1999-01-01 00:00:00");
		} else if (Objects.nonNull(startDate) && Objects.isNull(endDate)) {
			endDate = LocalDateTime.now().plusMinutes(15);
		}

		if (Objects.isNull(startDate) && Objects.isNull(endDate)) {
			arrangement = arrangementRepository.findAllArrangementNative(statusId, babyId, paymentTypeId, startPrice,
					endPrice, remaingingTerm, servicePackageId, arrangementId, TenantContext.getTenant());
		} else {
			arrangement = arrangementRepository.findAllArrangementNativeWithStartDateAndDate(statusId, babyId,
					paymentTypeId, startPrice, endPrice, remaingingTerm, servicePackageId, arrangementId, startDate,
					endDate, TenantContext.getTenant());
		}

		arrangementDto = arrangement.stream().map(x -> buildFindAllArrangementDtoFromArrangement(x))
				.collect(Collectors.toList());

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

	public BigDecimal findTotalSum(Integer babyId, Integer statusId, Integer servicePackageId, Integer paymentTypeId,
			Integer remaingingTerm, BigDecimal startPrice, BigDecimal endPrice, Integer arrangementId,
			LocalDateTime startDate, LocalDateTime endDate) {

		if (Objects.isNull(startDate) && Objects.nonNull(endDate)) {
			startDate = DateTimeUtil.getDateTimeFromString("1999-01-01 00:00:00");
		} else if (Objects.nonNull(startDate) && Objects.isNull(endDate)) {
			endDate = LocalDateTime.now().plusMinutes(15);
		}

		BigDecimal price;

		if (Objects.isNull(startDate) && Objects.isNull(endDate)) {
			price = arrangementRepository.findPriceForAllArrangementNative(statusId, babyId, paymentTypeId, startPrice,
					endPrice, remaingingTerm, servicePackageId, arrangementId, TenantContext.getTenant());
		} else {
			price = arrangementRepository.findPriceForAllArrangementNativeWithStartDateAndDate(statusId, babyId,
					paymentTypeId, startPrice, endPrice, remaingingTerm, servicePackageId, arrangementId, startDate,
					endDate, TenantContext.getTenant());
		}

		return price;
	}

	public List<ShortDetailsDto> findAllArrangementList() {

		List<ShortDetailsDto> arrangementList = arrangementRepository.findByRemainingTermGreaterThan(0).stream()
				.map(x -> buildShortDetailsFromArrangement(x)).collect(Collectors.toList());

		return arrangementList;
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
				new ShortDetailsDto(arrangement.getStatus().getStatusId(), arrangement.getStatus().getStatusName()));
		if (Objects.nonNull(arrangement.getPaymentType())) {
			findAllArrangementDto.setPaymentType(new ShortDetailsDto(arrangement.getPaymentType().getPaymentTypeId(),
					arrangement.getPaymentType().getPaymentTypeName()));
		}
		findAllArrangementDto.setExtendDurationDays(arrangement.getExtendDurationDays());

		return findAllArrangementDto;
	}

}
