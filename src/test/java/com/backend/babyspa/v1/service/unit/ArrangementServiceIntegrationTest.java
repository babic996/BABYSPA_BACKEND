package com.backend.babyspa.v1.service.unit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import com.backend.babyspa.v1.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.backend.babyspa.v1.dtos.CreateArrangementDto;
import com.backend.babyspa.v1.dtos.FindAllArrangementDto;
import com.backend.babyspa.v1.dtos.UpdateArrangementDto;
import com.backend.babyspa.v1.exceptions.BusinessException;
import com.backend.babyspa.v1.exceptions.NotFoundException;
import com.backend.babyspa.v1.models.Arrangement;
import com.backend.babyspa.v1.models.Baby;
import com.backend.babyspa.v1.models.Discount;
import com.backend.babyspa.v1.models.GiftCard;
import com.backend.babyspa.v1.models.PaymentType;
import com.backend.babyspa.v1.models.Reservation;
import com.backend.babyspa.v1.models.ServicePackage;
import com.backend.babyspa.v1.models.Status;
import com.backend.babyspa.v1.models.User;
import com.backend.babyspa.v1.repositories.ArrangementRepository;
import com.backend.babyspa.v1.repositories.ReservationRepository;
import com.backend.babyspa.v1.utils.SecurityUtil;

@ExtendWith(MockitoExtension.class)
@DisplayName("ArrangementService Integracijiski Testovi")
public class ArrangementServiceIntegrationTest {

  @Mock private ArrangementRepository arrangementRepository;

  @Mock private ReservationRepository reservationRepository;

  @Mock private ServicePackageService servicePackageService;

  @Mock private DiscountService discountService;

  @Mock private BabyService babyService;

  @Mock private StatusService statusService;

  @Mock private PaymentTypeService paymentTypeService;

  @Mock private GiftCardService giftCardService;

  @Mock private ArrangementAudService arrangementAudService;

  @Mock private SecurityUtil securityUtil;

  @InjectMocks private ArrangementService arrangementService;

  private User currentUser;
  private Baby testBaby;
  private ServicePackage testServicePackage;
  private Status createdStatus;
  private Status paidStatus;
  private Status notPaidStatus;
  private Discount percentageDiscount;
  private Discount fixedDiscount;
  private GiftCard testGiftCard;
  private PaymentType paymentType;
  private Arrangement testArrangement;

  @BeforeEach
  public void setUp() {
    // Inicijalizacija test podataka
    currentUser =
        new User("test@example.com", "testuser", "password123", "Test", "User", "tenant-1");
    currentUser.setUserId(1);

    testBaby = new Baby();
    testBaby.setBabyId(1);
    testBaby.setBabyName("Marko");
    testBaby.setBabySurname("Marković");
    testBaby.setNumberOfMonths(6);
    testBaby.setPhoneNumber("062123456");
    testBaby.setMotherName("Marija Marković");
    testBaby.setCreatedByUser(currentUser);

    testServicePackage = new ServicePackage();
    testServicePackage.setServicePackageId(1);
    testServicePackage.setServicePackageName("Premium paket");
    testServicePackage.setTermNumber(5);
    testServicePackage.setServicePackageDurationDays(30);
    testServicePackage.setPrice(new BigDecimal("500.00"));
    testServicePackage.setCreatedByUser(currentUser);

    createdStatus = new Status();
    createdStatus.setStatusId(1);
    createdStatus.setStatusName("Kreirano");
    createdStatus.setStatusCode("created");

    paidStatus = new Status();
    paidStatus.setStatusId(2);
    paidStatus.setStatusName("Plaćeno");
    paidStatus.setStatusCode("paid");

    notPaidStatus = new Status();
    notPaidStatus.setStatusId(3);
    notPaidStatus.setStatusName("Nije plaćeno");
    notPaidStatus.setStatusCode("not_paid");

    percentageDiscount = new Discount();
    percentageDiscount.setDiscountId(1);
    percentageDiscount.setValue(new BigDecimal("10"));
    percentageDiscount.setPrecentage(true);
    percentageDiscount.setDiscountName("10% popust");

    fixedDiscount = new Discount();
    fixedDiscount.setDiscountId(2);
    fixedDiscount.setValue(new BigDecimal("80.00"));
    fixedDiscount.setPrecentage(false);
    fixedDiscount.setDiscountName("80 KM popust");

    testGiftCard = new GiftCard();
    testGiftCard.setGiftCardId(1);
    testGiftCard.setSerialNumber("GC001");
    testGiftCard.setExpirationDate(LocalDateTime.now().plusMonths(6));
    testGiftCard.setUsed(false);

    paymentType = new PaymentType();
    paymentType.setPaymentTypeId(1);
    paymentType.setPaymentTypeName("Gotovina");
    paymentType.setPaymentTypeCode("cash");

    testArrangement = new Arrangement();
    testArrangement.setArrangementId(1);
    testArrangement.setBaby(testBaby);
    testArrangement.setServicePackage(testServicePackage);
    testArrangement.setStatus(createdStatus);
    testArrangement.setPrice(testServicePackage.getPrice());
    testArrangement.setRemainingTerm(testServicePackage.getTermNumber());
    testArrangement.setCreatedByUser(currentUser);
    testArrangement.setDeleted(false);
  }

  // ==================== FINDBYID TESTOVI ====================

  @Test
  @DisplayName("findById - Pronalazi aranžman sa validnim ID")
  public void testFindByIdSuccess() {
    // Arrange
    when(arrangementRepository.findById(1)).thenReturn(Optional.of(testArrangement));

    // Act
    Arrangement result = arrangementService.findById(1);

    // Assert
    assertNotNull(result);
    assertEquals(1, result.getArrangementId());
    assertEquals("Marko", result.getBaby().getBabyName());
    verify(arrangementRepository, times(1)).findById(1);
  }

  @Test
  @DisplayName("findById - Baca NotFoundException sa nevalidnim ID")
  public void testFindByIdNotFound() {
    // Arrange
    when(arrangementRepository.findById(999)).thenReturn(Optional.empty());

    // Act & Assert
    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> arrangementService.findById(999));
    assertTrue(exception.getMessage().contains("Nije pronađen aranžman sa ID: 999"));
  }

  // ==================== SAVE TESTOVI ====================

  @Test
  @DisplayName("save - Kreira novi aranžman bez popusta i poklon kartice")
  public void testSaveArrangementWithoutDiscountAndGiftCard() {
    // Arrange
    CreateArrangementDto dto = new CreateArrangementDto();
    dto.setBabyId(1);
    dto.setServicePackageId(1);
    dto.setNote("Testna beleška");
    dto.setDiscountId(null);
    dto.setGiftCardId(null);

    when(babyService.findById(1)).thenReturn(testBaby);
    when(servicePackageService.findById(1)).thenReturn(testServicePackage);
    when(statusService.findByStatusCode("created")).thenReturn(createdStatus);
    when(securityUtil.getCurrentUser()).thenReturn(currentUser);
    when(arrangementRepository.save(any(Arrangement.class))).thenReturn(testArrangement);

    // Act
    Arrangement result = arrangementService.save(dto);

    // Assert
    assertNotNull(result);
    assertEquals(testBaby.getBabyId(), result.getBaby().getBabyId());
    assertEquals(
        testServicePackage.getServicePackageId(), result.getServicePackage().getServicePackageId());
    assertEquals("created", result.getStatus().getStatusCode());
    assertEquals(testServicePackage.getPrice(), result.getPrice());
    assertEquals(testServicePackage.getTermNumber(), result.getRemainingTerm());
    assertNull(result.getDiscount());
    assertNull(result.getGiftCard());

    verify(arrangementRepository, times(1)).save(any(Arrangement.class));
    verify(arrangementAudService, times(1)).save(any(), any(), any());
  }

  @Test
  @DisplayName("save - Kreira aranžman sa procentualnim popustom")
  public void testSaveArrangementWithPercentageDiscount() {
    // Arrange
    CreateArrangementDto dto = new CreateArrangementDto();
    dto.setBabyId(1);
    dto.setServicePackageId(1);
    dto.setDiscountId(1);
    dto.setGiftCardId(null);

    when(babyService.findById(1)).thenReturn(testBaby);
    when(servicePackageService.findById(1)).thenReturn(testServicePackage);
    when(statusService.findByStatusCode("created")).thenReturn(createdStatus);
    when(discountService.findById(1)).thenReturn(percentageDiscount);
    when(securityUtil.getCurrentUser()).thenReturn(currentUser);
    when(arrangementRepository.save(any(Arrangement.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    Arrangement result = arrangementService.save(dto);

    // Assert
    assertNotNull(result);
    assertNotNull(result.getDiscount());
    assertEquals(1, result.getDiscount().getDiscountId());
    assertEquals(new BigDecimal("450.00"), result.getPrice());
    verify(discountService, times(1)).findById(1);
  }

  @Test
  @DisplayName("save - Kreira aranžman sa fiksnim popustom")
  public void testSaveArrangementWithFixedDiscount() {
    // Arrange
    CreateArrangementDto dto = new CreateArrangementDto();
    dto.setBabyId(1);
    dto.setServicePackageId(1);
    dto.setDiscountId(2);
    dto.setGiftCardId(null);

    when(babyService.findById(1)).thenReturn(testBaby);
    when(servicePackageService.findById(1)).thenReturn(testServicePackage);
    when(statusService.findByStatusCode("created")).thenReturn(createdStatus);
    when(discountService.findById(2)).thenReturn(fixedDiscount);
    when(securityUtil.getCurrentUser()).thenReturn(currentUser);
    when(arrangementRepository.save(any(Arrangement.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    Arrangement result = arrangementService.save(dto);

    // Assert
    assertEquals(new BigDecimal("420.00"), result.getPrice());
    verify(discountService, times(1)).findById(2);
  }

  @Test
  @DisplayName("save - Baca BusinessException ako je popust veći od cijene")
  public void testSaveArrangementDiscountGreaterThanPrice() {
    // Arrange
    CreateArrangementDto dto = new CreateArrangementDto();
    dto.setBabyId(1);
    dto.setServicePackageId(1);
    dto.setDiscountId(2);
    dto.setGiftCardId(null);

    Discount invalidDiscount = new Discount();
    invalidDiscount.setDiscountId(2);
    invalidDiscount.setValue(new BigDecimal("600.00"));
    invalidDiscount.setPrecentage(false);

    when(babyService.findById(1)).thenReturn(testBaby);
    when(servicePackageService.findById(1)).thenReturn(testServicePackage);
    when(statusService.findByStatusCode("created")).thenReturn(createdStatus);
    when(discountService.findById(2)).thenReturn(invalidDiscount);
    when(securityUtil.getCurrentUser()).thenReturn(currentUser);

    // Act & Assert
    BusinessException exception =
        assertThrows(BusinessException.class, () -> arrangementService.save(dto));
    assertTrue(exception.getMessage().contains("Popust je veći od cijene paketa usluge"));
  }

  @Test
  @DisplayName("save - Kreira aranžman sa poklon karticom (sa popustom)")
  public void testSaveArrangementWithGiftCard() {
    // Arrange
    CreateArrangementDto dto = new CreateArrangementDto();
    dto.setBabyId(1);
    dto.setServicePackageId(1);
    dto.setDiscountId(1);
    dto.setGiftCardId(1);

    when(babyService.findById(1)).thenReturn(testBaby);
    when(servicePackageService.findById(1)).thenReturn(testServicePackage);
    when(statusService.findByStatusCode("created")).thenReturn(createdStatus);
    when(discountService.findById(1)).thenReturn(percentageDiscount);
    when(giftCardService.findById(1)).thenReturn(testGiftCard);
    when(giftCardService.updateGiftCardStatus(true, testGiftCard)).thenReturn(testGiftCard);
    when(securityUtil.getCurrentUser()).thenReturn(currentUser);
    when(arrangementRepository.save(any(Arrangement.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    Arrangement result = arrangementService.save(dto);

    // Assert
    assertNotNull(result.getGiftCard());
    assertEquals(1, result.getGiftCard().getGiftCardId());
    assertEquals(1, result.getDiscount().getDiscountId());
    assertEquals(new BigDecimal("450.00"), result.getPrice());
  }

  @Test
  @DisplayName("save - Baca BusinessException ako je poklon kartica istekla")
  public void testSaveArrangementWithExpiredGiftCard() {
    // Arrange
    CreateArrangementDto dto = new CreateArrangementDto();
    dto.setBabyId(1);
    dto.setServicePackageId(1);
    dto.setDiscountId(1);
    dto.setGiftCardId(1);

    GiftCard expiredGiftCard = new GiftCard();
    expiredGiftCard.setGiftCardId(1);
    expiredGiftCard.setSerialNumber("GC001");
    expiredGiftCard.setExpirationDate(LocalDateTime.now().minusDays(1)); // Istekla
    expiredGiftCard.setUsed(false);

    when(babyService.findById(1)).thenReturn(testBaby);
    when(servicePackageService.findById(1)).thenReturn(testServicePackage);
    when(statusService.findByStatusCode("created")).thenReturn(createdStatus);
    when(discountService.findById(1)).thenReturn(percentageDiscount);
    when(giftCardService.findById(1)).thenReturn(expiredGiftCard);
    when(securityUtil.getCurrentUser()).thenReturn(currentUser);

    // Act & Assert
    BusinessException exception =
        assertThrows(BusinessException.class, () -> arrangementService.save(dto));
    assertTrue(exception.getMessage().contains("Poklon kartica je istekla"));
  }

  @Test
  @DisplayName("save - Baca BusinessException ako se koristi poklon kartica bez popusta")
  public void testSaveArrangementGiftCardWithoutDiscount() {
    CreateArrangementDto dto = new CreateArrangementDto();
    dto.setBabyId(1);
    dto.setServicePackageId(1);
    dto.setDiscountId(null);
    dto.setGiftCardId(1);

    when(babyService.findById(1)).thenReturn(testBaby);
    when(servicePackageService.findById(1)).thenReturn(testServicePackage);
    when(statusService.findByStatusCode("created")).thenReturn(createdStatus);
    when(securityUtil.getCurrentUser()).thenReturn(currentUser);

    // Act & Assert
    BusinessException exception =
        assertThrows(BusinessException.class, () -> arrangementService.save(dto));
    assertTrue(
        exception.getMessage().contains("Morate izabrati popust da primijenite poklon karticu"));

    verify(giftCardService, never()).findById(anyInt());
  }

  // ==================== UPDATE TESTOVI ====================

  @Test
  @DisplayName("update - Ažurira aranžman sa svim poljima (bez rezervacija)")
  public void testUpdateArrangementWithoutReservations() {
    // Arrange
    UpdateArrangementDto dto = new UpdateArrangementDto();
    dto.setArrangementId(1);
    dto.setBabyId(1);
    dto.setServicePackageId(1);
    dto.setStatusId(2);
    dto.setPaymentTypeId(1);
    dto.setDiscountId(1);
    dto.setGiftCardId(null);
    dto.setExtendDurationDays(7);
    dto.setNote("Ažurirana bilješka");

    FindAllArrangementDto expectedDto = new FindAllArrangementDto();
    expectedDto.setArrangementId(1);
    expectedDto.setRemainingTerm(5);
    expectedDto.setExtendDurationDays(7);
    expectedDto.setPrice(new BigDecimal("450.00")); // 500 - 10% popust
    expectedDto.setNote("Ažurirana bilješka");

    testArrangement.setArrangementId(1);
    testArrangement.setBaby(testBaby);
    testArrangement.setServicePackage(testServicePackage);
    testArrangement.setStatus(createdStatus);

    when(arrangementRepository.findById(1)).thenReturn(Optional.of(testArrangement));
    when(reservationRepository.existsByArrangementAndIsDeleted(testArrangement, false))
        .thenReturn(false);
    when(babyService.findById(1)).thenReturn(testBaby);
    when(servicePackageService.findById(1)).thenReturn(testServicePackage);
    when(statusService.findById(2)).thenReturn(paidStatus);
    when(paymentTypeService.findById(1)).thenReturn(paymentType);
    when(discountService.findById(1)).thenReturn(percentageDiscount);
    when(securityUtil.getCurrentUser()).thenReturn(currentUser);
    when(arrangementRepository.save(any(Arrangement.class)))
        .thenAnswer(
            invocation -> {
              Arrangement saved = invocation.getArgument(0);
              saved.setArrangementId(1);
              return saved;
            });

    FindAllArrangementDto result = arrangementService.update(dto);

    assertNotNull(result);
    assertEquals(1, result.getArrangementId());
    assertEquals(5, result.getRemainingTerm());
    assertEquals(7, result.getExtendDurationDays());
    assertEquals(new BigDecimal("450.00"), result.getPrice());
    assertEquals("Ažurirana bilješka", result.getNote());
    assertNotNull(result.getDiscount());
    assertNull(result.getGiftCard());

    verify(arrangementRepository, times(1)).save(any(Arrangement.class));
    verify(arrangementAudService, times(1)).save(any(), any(), any());
  }

  @Test
  @DisplayName("update - Ne mijenja bebu i servis paket ako postoji rezervacija")
  public void testUpdateArrangementWithReservations() {
    // Arrange
    UpdateArrangementDto dto = new UpdateArrangementDto();
    dto.setArrangementId(1);
    dto.setBabyId(2);
    dto.setServicePackageId(2);
    dto.setStatusId(3);
    dto.setPaymentTypeId(null);
    dto.setDiscountId(null);
    dto.setGiftCardId(null);
    dto.setExtendDurationDays(null);

    Reservation reservation = new Reservation();
    reservation.setReservationId(1);
    reservation.setArrangement(testArrangement);
    reservation.setDeleted(false);

    Arrangement updatedArrangement = new Arrangement();
    updatedArrangement.setArrangementId(1);
    updatedArrangement.setBaby(testBaby);
    updatedArrangement.setServicePackage(testServicePackage);
    updatedArrangement.setStatus(notPaidStatus);
    updatedArrangement.setRemainingTerm(testServicePackage.getTermNumber());
    updatedArrangement.setPrice(new BigDecimal("500.00"));
    updatedArrangement.setUpdatedByUser(currentUser);

    when(arrangementRepository.findById(1)).thenReturn(Optional.of(testArrangement));
    when(reservationRepository.existsByArrangementAndIsDeleted(testArrangement, false))
        .thenReturn(true);
    when(statusService.findById(3)).thenReturn(notPaidStatus);
    when(securityUtil.getCurrentUser()).thenReturn(currentUser);
    when(arrangementRepository.save(any(Arrangement.class))).thenReturn(updatedArrangement);

    // Act
    FindAllArrangementDto result = arrangementService.update(dto);

    // Assert
    assertNotNull(result);
    // Verifikuj da se beba i servis paket nisu promenili
    verify(babyService, times(0)).findById(anyInt());
    verify(servicePackageService, times(0)).findById(anyInt());
  }

  @Test
  @DisplayName("update - Ažurira aranžman sa novom poklon karticom")
  public void testUpdateArrangementChangeGiftCard() {
    // Arrange
    UpdateArrangementDto dto = new UpdateArrangementDto();
    dto.setArrangementId(1);
    dto.setBabyId(1);
    dto.setServicePackageId(1);
    dto.setStatusId(3);
    dto.setPaymentTypeId(null);
    dto.setDiscountId(1);
    dto.setGiftCardId(1);
    dto.setExtendDurationDays(null);

    GiftCard newGiftCard = new GiftCard();
    newGiftCard.setGiftCardId(1);
    newGiftCard.setSerialNumber("GC001");
    newGiftCard.setExpirationDate(LocalDateTime.now().plusMonths(6));
    newGiftCard.setUsed(false);

    Arrangement updatedArrangement = new Arrangement();
    updatedArrangement.setArrangementId(1);
    updatedArrangement.setBaby(testBaby);
    updatedArrangement.setServicePackage(testServicePackage);
    updatedArrangement.setStatus(notPaidStatus);
    updatedArrangement.setDiscount(percentageDiscount);
    updatedArrangement.setGiftCard(newGiftCard);
    updatedArrangement.setPrice(new BigDecimal("450.00"));
    updatedArrangement.setRemainingTerm(testServicePackage.getTermNumber());
    updatedArrangement.setUpdatedByUser(currentUser);

    when(arrangementRepository.findById(1)).thenReturn(Optional.of(testArrangement));
    when(reservationRepository.existsByArrangementAndIsDeleted(testArrangement, false))
        .thenReturn(false);
    when(babyService.findById(1)).thenReturn(testBaby);
    when(servicePackageService.findById(1)).thenReturn(testServicePackage);
    when(statusService.findById(3)).thenReturn(notPaidStatus);
    when(discountService.findById(1)).thenReturn(percentageDiscount);
    when(giftCardService.findById(1)).thenReturn(newGiftCard);
    when(giftCardService.updateGiftCardStatus(true, newGiftCard)).thenReturn(newGiftCard);
    when(securityUtil.getCurrentUser()).thenReturn(currentUser);
    when(arrangementRepository.save(any(Arrangement.class))).thenReturn(updatedArrangement);

    // Act
    FindAllArrangementDto result = arrangementService.update(dto);

    // Assert
    assertNotNull(result);
    verify(giftCardService, times(1)).updateGiftCardStatus(true, newGiftCard);
  }

  // ==================== DELETE TESTOVI ====================

  @Test
  @DisplayName("delete - Briše aranžman bez rezervacija")
  public void testDeleteArrangementWithoutReservations() {
    // Arrange
    when(arrangementRepository.findById(1)).thenReturn(Optional.of(testArrangement));
    when(reservationRepository.existsByArrangementAndIsDeleted(testArrangement, false))
        .thenReturn(false);
    when(securityUtil.getCurrentUser()).thenReturn(currentUser);
    when(arrangementRepository.save(any(Arrangement.class))).thenReturn(testArrangement);

    // Act
    int result = arrangementService.delete(1);

    // Assert
    assertEquals(1, result);
    verify(arrangementRepository, times(1)).save(any(Arrangement.class));
    verify(arrangementAudService, times(1)).save(any(), any(), any());
  }

  @Test
  @DisplayName("delete - Baca BusinessException ako aranžman ima rezervacije")
  public void testDeleteArrangementWithReservations() {
    // Arrange
    when(arrangementRepository.findById(1)).thenReturn(Optional.of(testArrangement));
    when(reservationRepository.existsByArrangementAndIsDeleted(testArrangement, false))
        .thenReturn(true);

    // Act & Assert
    BusinessException exception =
        assertThrows(BusinessException.class, () -> arrangementService.delete(1));
    assertTrue(
        exception.getMessage().contains("Nije moguće obrisati aranžman ako ima rezervacija"));
  }

  @Test
  @DisplayName("delete - Baca NotFoundException za nevalidan ID")
  public void testDeleteArrangementNotFound() {
    // Arrange
    when(arrangementRepository.findById(999)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(NotFoundException.class, () -> arrangementService.delete(999));
  }

  // ==================== REMAINING TERM TESTOVI ====================

  @Test
  @DisplayName("decreaseRemainingTerm - Smanjuje preostale termine")
  public void testDecreaseRemainingTerm() {
    // Arrange
    testArrangement.setRemainingTerm(5);
    when(arrangementRepository.save(any(Arrangement.class))).thenReturn(testArrangement);

    // Act
    arrangementService.decreaseRemainingTerm(testArrangement);

    // Assert
    assertEquals(4, testArrangement.getRemainingTerm());
    verify(arrangementRepository, times(1)).save(testArrangement);
  }

  @Test
  @DisplayName("increaseRemainingTerm - Povećava preostale termine")
  public void testIncreaseRemainingTerm() {
    // Arrange
    testArrangement.setRemainingTerm(5);
    when(arrangementRepository.save(any(Arrangement.class))).thenReturn(testArrangement);

    // Act
    arrangementService.increaseRemainingTerm(testArrangement);

    // Assert
    assertEquals(6, testArrangement.getRemainingTerm());
    verify(arrangementRepository, times(1)).save(testArrangement);
  }

  // ==================== EXIST TESTOVI ====================

  @Test
  @DisplayName("existsByServicePackage - Vraća true ako postoji aranžman sa paketom")
  public void testExistsByServicePackageTrue() {
    // Arrange
    when(servicePackageService.findById(1)).thenReturn(testServicePackage);
    when(arrangementRepository.existsByServicePackageAndIsDeleted(testServicePackage, false))
        .thenReturn(true);

    // Act
    boolean result = arrangementService.existsByServicePackage(1);

    // Assert
    assertTrue(result);
  }

  @Test
  @DisplayName("existsByServicePackage - Vraća false ako ne postoji aranžman sa paketom")
  public void testExistsByServicePackageFalse() {
    // Arrange
    when(servicePackageService.findById(1)).thenReturn(testServicePackage);
    when(arrangementRepository.existsByServicePackageAndIsDeleted(testServicePackage, false))
        .thenReturn(false);

    // Act
    boolean result = arrangementService.existsByServicePackage(1);

    // Assert
    assertFalse(result);
  }

  // ==================== EDGE CASE TESTOVI ====================

  @Test
  @DisplayName("save - Rukuje nulom i negativnim vrijednostima u popustu")
  public void testSaveWithZeroPercentageDiscount() {
    // Arrange
    CreateArrangementDto dto = new CreateArrangementDto();
    dto.setBabyId(1);
    dto.setServicePackageId(1);
    dto.setDiscountId(1);
    dto.setGiftCardId(null);

    Discount zeroDiscount = new Discount();
    zeroDiscount.setDiscountId(1);
    zeroDiscount.setValue(BigDecimal.ZERO);
    zeroDiscount.setPrecentage(true);
    zeroDiscount.setDiscountName("0% popust");

    Arrangement arrangedWithZeroDiscount = new Arrangement();
    arrangedWithZeroDiscount.setArrangementId(1);
    arrangedWithZeroDiscount.setBaby(testBaby);
    arrangedWithZeroDiscount.setServicePackage(testServicePackage);
    arrangedWithZeroDiscount.setStatus(createdStatus);
    arrangedWithZeroDiscount.setDiscount(zeroDiscount);
    arrangedWithZeroDiscount.setPrice(new BigDecimal("500.00")); // 500 - 0%
    arrangedWithZeroDiscount.setRemainingTerm(testServicePackage.getTermNumber());
    arrangedWithZeroDiscount.setCreatedByUser(currentUser);

    when(babyService.findById(1)).thenReturn(testBaby);
    when(servicePackageService.findById(1)).thenReturn(testServicePackage);
    when(statusService.findByStatusCode("created")).thenReturn(createdStatus);
    when(discountService.findById(1)).thenReturn(zeroDiscount);
    when(securityUtil.getCurrentUser()).thenReturn(currentUser);
    when(arrangementRepository.save(any(Arrangement.class))).thenReturn(arrangedWithZeroDiscount);

    // Act
    Arrangement result = arrangementService.save(dto);

    // Assert
    assertNotNull(result);
    assertEquals(new BigDecimal("500.00"), result.getPrice());
  }

  @Test
  @DisplayName("update - Rukuje novom poklon karticom sa zamjenom stare")
  public void testUpdateReplaceGiftCard() {
    // Arrange
    GiftCard oldGiftCard = new GiftCard();
    oldGiftCard.setGiftCardId(1);
    oldGiftCard.setSerialNumber("GC001");
    oldGiftCard.setExpirationDate(LocalDateTime.now().plusMonths(6));
    oldGiftCard.setUsed(true);

    GiftCard newGiftCard = new GiftCard();
    newGiftCard.setGiftCardId(2);
    newGiftCard.setSerialNumber("GC002");
    newGiftCard.setExpirationDate(LocalDateTime.now().plusMonths(12));
    newGiftCard.setUsed(false);

    testArrangement.setGiftCard(oldGiftCard);
    testArrangement.setDiscount(percentageDiscount);

    UpdateArrangementDto dto = new UpdateArrangementDto();
    dto.setArrangementId(1);
    dto.setBabyId(1);
    dto.setServicePackageId(1);
    dto.setStatusId(3);
    dto.setPaymentTypeId(null);
    dto.setDiscountId(1);
    dto.setGiftCardId(2);
    dto.setExtendDurationDays(null);

    Arrangement updatedArrangement = new Arrangement();
    updatedArrangement.setArrangementId(1);
    updatedArrangement.setBaby(testBaby);
    updatedArrangement.setServicePackage(testServicePackage);
    updatedArrangement.setStatus(notPaidStatus);
    updatedArrangement.setDiscount(percentageDiscount);
    updatedArrangement.setGiftCard(newGiftCard);
    updatedArrangement.setPrice(new BigDecimal("450.00"));
    updatedArrangement.setRemainingTerm(testServicePackage.getTermNumber());
    updatedArrangement.setUpdatedByUser(currentUser);

    when(arrangementRepository.findById(1)).thenReturn(Optional.of(testArrangement));
    when(reservationRepository.existsByArrangementAndIsDeleted(testArrangement, false))
        .thenReturn(false);
    when(babyService.findById(1)).thenReturn(testBaby);
    when(servicePackageService.findById(1)).thenReturn(testServicePackage);
    when(statusService.findById(3)).thenReturn(notPaidStatus);
    when(discountService.findById(1)).thenReturn(percentageDiscount);
    when(giftCardService.findById(2)).thenReturn(newGiftCard);
    when(giftCardService.updateGiftCardStatus(false, oldGiftCard)).thenReturn(oldGiftCard);
    when(giftCardService.updateGiftCardStatus(true, newGiftCard)).thenReturn(newGiftCard);
    when(securityUtil.getCurrentUser()).thenReturn(currentUser);
    when(arrangementRepository.save(any(Arrangement.class))).thenReturn(updatedArrangement);

    // Act
    FindAllArrangementDto result = arrangementService.update(dto);

    // Assert
    assertNotNull(result);
    verify(giftCardService, times(1)).updateGiftCardStatus(false, oldGiftCard);
    verify(giftCardService, times(1)).updateGiftCardStatus(true, newGiftCard);
  }
}
