package com.backend.babyspa.v1.services;

import com.backend.babyspa.v1.config.TenantContext;
import com.backend.babyspa.v1.dtos.CreateGiftCardDto;
import com.backend.babyspa.v1.dtos.UpdateGiftCardDto;
import com.backend.babyspa.v1.exceptions.BusinessException;
import com.backend.babyspa.v1.exceptions.NotFoundException;
import com.backend.babyspa.v1.models.Arrangement;
import com.backend.babyspa.v1.models.GiftCard;
import com.backend.babyspa.v1.projections.FindAllGiftCardDto;
import com.backend.babyspa.v1.repositories.ArrangementRepository;
import com.backend.babyspa.v1.repositories.GiftCardRepository;
import com.backend.babyspa.v1.utils.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class GiftCardService {

  @Autowired private GiftCardRepository giftCardRepository;

  @Autowired private ArrangementRepository arrangementRepository;

  public GiftCard findBySerialNumber(String serialNumber) {
    return giftCardRepository
        .findBySerialNumber(serialNumber)
        .orElseThrow(
            () ->
                new NotFoundException(
                    "Nije pronađena poklon kartica sa serijskim brojem: " + serialNumber + "!"));
  }

  @Transactional
  public GiftCard findById(Integer giftCardId) {
    return giftCardRepository
        .findById(giftCardId)
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Nije pronađena poklon kartica sa ID: " + giftCardId + "!"));
  }

  public GiftCard save(CreateGiftCardDto createGiftCardDto) {
    if (giftCardRepository.existsBySerialNumber(createGiftCardDto.getSerialNumber())) {
      throw new BusinessException("Postoji poklon kartica sa ovim serijskim brojem!");
    }

    GiftCard giftCard = new GiftCard();
    giftCard.setSerialNumber(createGiftCardDto.getSerialNumber());
    giftCard.setExpirationDate(createGiftCardDto.getExpirationDate());

    return giftCardRepository.save(giftCard);
  }

  public GiftCard update(UpdateGiftCardDto updateGiftCardDto) {
    if (giftCardRepository.existsBySerialNumberAndGiftCardIdNot(
        updateGiftCardDto.getSerialNumber(), updateGiftCardDto.getGiftCardId())) {
      throw new BusinessException("Postoji poklon kartica sa ovim serijskim brojem!");
    }

    GiftCard giftCard = findById(updateGiftCardDto.getGiftCardId());
    giftCard.setUsed(updateGiftCardDto.isUsed());
    giftCard.setSerialNumber(updateGiftCardDto.getSerialNumber());
    giftCard.setExpirationDate(updateGiftCardDto.getExpirationDate());

    return giftCardRepository.save(giftCard);
  }

  public List<GiftCard> findAllList(Boolean isUsed, Integer arrangementId) {
    List<GiftCard> giftCardsList;
    if (Objects.nonNull(isUsed)) {
      giftCardsList = giftCardRepository.findByUsed(isUsed);
      if (Objects.nonNull(arrangementId) && !isUsed) {
        Arrangement arrangement =
            arrangementRepository
                .findById(arrangementId)
                .orElseThrow(
                    () ->
                        new NotFoundException("Ne postoji aranžman sa ID: " + arrangementId + "!"));
        if (Objects.nonNull(arrangement) && Objects.nonNull(arrangement.getGiftCard())) {
          giftCardsList.add(arrangement.getGiftCard());
        }
      }
    } else {
      giftCardsList = giftCardRepository.findAll();
    }
    return giftCardsList;
  }

  @Transactional
  public int delete(int giftCardId) {
    GiftCard giftCard = findById(giftCardId);
    if (arrangementRepository.existsByGiftCardAndIsDeleted(giftCard, false)) {
      throw new BusinessException(
          "Ova kartica je dodijeljena aranžmanu i nije moguće njeno brisanje!");
    }
    giftCardRepository.delete(giftCard);
    return giftCardId;
  }

  @Transactional
  public GiftCard updateGiftCardStatus(boolean isUsed, GiftCard giftCard) {
    giftCard.setUsed(isUsed);

    return giftCardRepository.save(giftCard);
  }

  public Page<FindAllGiftCardDto> findAll(
      int page,
      int size,
      String serialNumber,
      Boolean isUsed,
      Integer giftCardId,
      LocalDateTime startDate,
      LocalDateTime endDate) {
    List<FindAllGiftCardDto> giftCards;

    if (Objects.isNull(startDate) && Objects.nonNull(endDate)) {
      startDate = DateTimeUtil.getDateTimeFromString("1999-01-01 00:00:00");
    } else if (Objects.nonNull(startDate) && Objects.isNull(endDate)) {
      endDate = LocalDateTime.now().plusMinutes(15);
    }

    if (Objects.isNull(startDate) && Objects.isNull(endDate)) {
      giftCards =
          giftCardRepository.findAllGiftCardNative(
              serialNumber, isUsed, giftCardId, TenantContext.getTenant());
    } else {
      giftCards =
          giftCardRepository.findAllGiftCardNativeWithStartDateAndDate(
              serialNumber, isUsed, giftCardId, startDate, endDate, TenantContext.getTenant());
    }

    Pageable pageable = PageRequest.of(page, size);
    int start = (int) pageable.getOffset();
    int end = Math.min((start + pageable.getPageSize()), giftCards.size());
    if (start > end) {
      start = end = 0;
    }

    final Page<FindAllGiftCardDto> pageItem =
        new PageImpl<>(giftCards.subList(start, end), pageable, giftCards.size());

    return pageItem;
  }
}
