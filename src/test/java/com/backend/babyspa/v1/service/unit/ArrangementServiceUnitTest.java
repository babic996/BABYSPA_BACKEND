package com.backend.babyspa.v1.service.unit;

import com.backend.babyspa.v1.dtos.CreateArrangementDto;
import com.backend.babyspa.v1.exceptions.BusinessException;
import com.backend.babyspa.v1.models.*;
import com.backend.babyspa.v1.repositories.ArrangementRepository;
import com.backend.babyspa.v1.services.*;
import com.backend.babyspa.v1.utils.SecurityUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArrangementServiceUnitTest {
    @Mock
    private BabyService babyService;

    @Mock
    private StatusService statusService;

    @Mock
    private ServicePackageService servicePackageService;

    @Mock
    private DiscountService discountService;

    @Mock
    private GiftCardService giftCardService;

    @Mock
    private ArrangementRepository arrangementRepository;

    @Mock
    private ArrangementAudService arrangementAudService;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private ArrangementService arrangementService;


    @Test
    void save_ShouldCreateArrangementWithoutDiscount() {
        // Given
        CreateArrangementDto dto = new CreateArrangementDto();
        dto.setBabyId(1);
        dto.setServicePackageId(1);
        dto.setDiscountId(null);
        dto.setNote("Test note");


        Baby mockBaby = new Baby();
        Status mockStatus = new Status();
        mockStatus.setStatusCode("created");
        ServicePackage mockServicePackage = new ServicePackage();
        mockServicePackage.setPrice(new BigDecimal("100.00"));
        mockServicePackage.setTermNumber(10);
        User mockUser = new User();

        when(babyService.findById(1)).thenReturn(mockBaby);
        when(statusService.findByStatusCode("created")).thenReturn(mockStatus);
        when(servicePackageService.findById(1)).thenReturn(mockServicePackage);
        when(securityUtil.getCurrentUser()).thenReturn(mockUser);
        when(arrangementRepository.save(any(Arrangement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Arrangement result = arrangementService.save(dto);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("100.00"), result.getPrice());
        assertEquals(mockBaby, result.getBaby());
        assertEquals(mockServicePackage, result.getServicePackage());
        assertEquals(mockStatus.getStatusCode(), result.getStatus().getStatusCode());
        assertEquals("Test note", result.getNote());
        assertEquals(10, result.getRemainingTerm());

        verify(arrangementRepository).save(any(Arrangement.class));
        verify(arrangementAudService).save(any(Arrangement.class), eq(mockUser), eq(UserActionType.create));
    }

    @Test
    void save_ShouldApplyPercentageDiscount() {
        // Given
        CreateArrangementDto dto = new CreateArrangementDto();
        dto.setBabyId(1);
        dto.setServicePackageId(1);
        dto.setDiscountId(1);

        Baby mockBaby = new Baby();
        Status mockStatus = new Status();
        ServicePackage mockServicePackage = new ServicePackage();
        mockServicePackage.setPrice(new BigDecimal("120.00"));
        mockServicePackage.setTermNumber(10);

        Discount mockDiscount = new Discount();
        mockDiscount.setPrecentage(true);
        mockDiscount.setValue(new BigDecimal("10"));

        User mockUser = new User();

        when(babyService.findById(1)).thenReturn(mockBaby);
        when(statusService.findByStatusCode("created")).thenReturn(mockStatus);
        when(servicePackageService.findById(1)).thenReturn(mockServicePackage);
        when(discountService.findById(1)).thenReturn(mockDiscount);
        when(securityUtil.getCurrentUser()).thenReturn(mockUser);
        when(arrangementRepository.save(any(Arrangement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Arrangement result = arrangementService.save(dto);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("108.00"), result.getPrice());
        assertEquals(mockDiscount.getValue(), result.getDiscount().getValue());
        assertEquals(mockServicePackage.getPrice(), result.getServicePackage().getPrice());
    }


    @Test
    void save_ShouldApplyFixedDiscount() {
        // Given
        CreateArrangementDto dto = new CreateArrangementDto();
        dto.setBabyId(1);
        dto.setServicePackageId(1);
        dto.setDiscountId(1);

        Baby mockBaby = new Baby();
        Status mockStatus = new Status();
        ServicePackage mockServicePackage = new ServicePackage();
        mockServicePackage.setPrice(new BigDecimal("100.00"));

        Discount mockDiscount = new Discount();
        mockDiscount.setDiscountId(1);
        mockDiscount.setPrecentage(false);
        mockDiscount.setValue(new BigDecimal("20.00"));

        User mockUser = new User();

        when(babyService.findById(1)).thenReturn(mockBaby);
        when(statusService.findByStatusCode("created")).thenReturn(mockStatus);
        when(servicePackageService.findById(1)).thenReturn(mockServicePackage);
        when(discountService.findById(1)).thenReturn(mockDiscount);
        when(securityUtil.getCurrentUser()).thenReturn(mockUser);
        when(arrangementRepository.save(any(Arrangement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Arrangement result = arrangementService.save(dto);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("80.00"), result.getPrice());
    }


    @Test
    void save_ShouldThrowExceptionWhenFixedDiscountExceedsPrice() {
        // Given
        CreateArrangementDto dto = new CreateArrangementDto();
        dto.setBabyId(1);
        dto.setServicePackageId(1);
        dto.setDiscountId(1);

        ServicePackage mockServicePackage = new ServicePackage();
        mockServicePackage.setPrice(new BigDecimal("50.00"));

        Discount mockDiscount = new Discount();
        mockDiscount.setDiscountId(1);
        mockDiscount.setPrecentage(false);
        mockDiscount.setValue(new BigDecimal("60.00"));

        when(servicePackageService.findById(1)).thenReturn(mockServicePackage);
        when(discountService.findById(1)).thenReturn(mockDiscount);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> arrangementService.save(dto));

        assertEquals("Popust je veći od cijene paketa usluge!", exception.getMessage());
    }


    @Test
    void save_ShouldThrowExceptionWhenGiftCardWithoutDiscount() {
        // Given
        CreateArrangementDto dto = new CreateArrangementDto();
        dto.setBabyId(1);
        dto.setServicePackageId(1);
        dto.setGiftCardId(1);

        ServicePackage mockServicePackage = new ServicePackage();
        mockServicePackage.setPrice(new BigDecimal("100.00"));

        when(servicePackageService.findById(1)).thenReturn(mockServicePackage);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class,
                () -> arrangementService.save(dto));

        assertEquals("Morate izabrati popust!", exception.getMessage());
    }

    @Test
    void save_ShouldSetGiftCardWhenDiscountExists() {
        // Given
        CreateArrangementDto dto = new CreateArrangementDto();
        dto.setBabyId(1);
        dto.setServicePackageId(1);
        dto.setDiscountId(1);
        dto.setGiftCardId(1);

        Baby mockBaby = new Baby();
        Status mockStatus = new Status();
        ServicePackage mockServicePackage = new ServicePackage();
        mockServicePackage.setPrice(new BigDecimal("100.00"));

        Discount mockDiscount = new Discount();
        mockDiscount.setPrecentage(true);
        mockDiscount.setValue(new BigDecimal("10"));

        GiftCard mockGiftCard = new GiftCard();
        User mockUser = new User();

        when(babyService.findById(1)).thenReturn(mockBaby);
        when(statusService.findByStatusCode("created")).thenReturn(mockStatus);
        when(servicePackageService.findById(1)).thenReturn(mockServicePackage);
        when(discountService.findById(1)).thenReturn(mockDiscount);
        when(giftCardService.findById(1)).thenReturn(mockGiftCard);
        when(securityUtil.getCurrentUser()).thenReturn(mockUser);
        when(arrangementRepository.save(any(Arrangement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Arrangement result = arrangementService.save(dto);

        // Then
        assertNotNull(result);
        assertEquals(mockGiftCard, result.getGiftCard());
        assertEquals(mockDiscount, result.getDiscount());
        assertEquals(new BigDecimal("90.00"), result.getPrice());
    }
}
