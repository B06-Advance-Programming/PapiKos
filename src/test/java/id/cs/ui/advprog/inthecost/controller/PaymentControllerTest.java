package id.cs.ui.advprog.inthecost.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import id.cs.ui.advprog.inthecost.builder.PaymentBuilder;
import id.cs.ui.advprog.inthecost.enums.PaymentStatusEnum;
import id.cs.ui.advprog.inthecost.enums.PaymentTypeEnum;
import id.cs.ui.advprog.inthecost.enums.StatusPenyewaan;
import id.cs.ui.advprog.inthecost.model.Payment;
import id.cs.ui.advprog.inthecost.model.PenyewaanKos;
import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.service.PaymentService;
import id.cs.ui.advprog.inthecost.service.PenyewaanKosService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class PaymentControllerTest {

    @InjectMocks
    private PaymentController paymentController;

    @Mock
    private PaymentService paymentService;

    @Mock
    private PenyewaanKosService penyewaanKosService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ========== TOP UP TESTS ==========

    @Test
    void testTopUp_Success() throws Exception {
        PaymentController.TopUpRequest req = new PaymentController.TopUpRequest();
        String userIdStr = UUID.randomUUID().toString();
        req.setUserId(userIdStr);
        req.setAmount(150000.0);
        req.setDescription("Top Up via bank");

        UUID userId = UUID.fromString(userIdStr);

        LocalDateTime now = LocalDateTime.now();

        Payment expectedPayment = new PaymentBuilder()
                .id(1L)
                .userId(userId)
                .amount(150000.0)
                .description("Top Up via bank")
                .paymentType(PaymentTypeEnum.TOP_UP)
                .paymentStatus(PaymentStatusEnum.SUCCESS)
                .transactionDateTime(now)
                .build();

        when(paymentService.recordTopUpPayment(userId, 150000.0, "Top Up via bank"))
                .thenReturn(CompletableFuture.completedFuture(expectedPayment));

        String testHeaderValue = "true";

        CompletableFuture<ResponseEntity<?>> responseFuture = paymentController.topUp(req, testHeaderValue);
        ResponseEntity<?> response = responseFuture.get();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedPayment, response.getBody());
    }

    @Test
    void testTopUp_InvalidUUIDFormat() throws Exception {
        PaymentController.TopUpRequest req = new PaymentController.TopUpRequest();
        req.setUserId("invalid-uuid");
        req.setAmount(150000.0);
        req.setDescription("Top Up via bank");

        CompletableFuture<ResponseEntity<?>> responseFuture = paymentController.topUp(req, null);
        ResponseEntity<?> response = responseFuture.get();

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid UUID format for userId", response.getBody());
    }

    @Test
    void testTopUp_WithoutTestingModeHeader() throws Exception {
        PaymentController.TopUpRequest req = new PaymentController.TopUpRequest();
        String userIdStr = UUID.randomUUID().toString();
        req.setUserId(userIdStr);
        req.setAmount(150000.0);
        req.setDescription("Top Up via bank");

        UUID userId = UUID.fromString(userIdStr);

        Payment expectedPayment = new PaymentBuilder()
                .id(1L)
                .userId(userId)
                .amount(150000.0)
                .description("Top Up via bank")
                .paymentType(PaymentTypeEnum.TOP_UP)
                .paymentStatus(PaymentStatusEnum.SUCCESS)
                .transactionDateTime(LocalDateTime.now())
                .build();

        when(paymentService.recordTopUpPayment(userId, 150000.0, "Top Up via bank"))
                .thenReturn(CompletableFuture.completedFuture(expectedPayment));

        CompletableFuture<ResponseEntity<?>> responseFuture = paymentController.topUp(req, null);
        ResponseEntity<?> response = responseFuture.get();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedPayment, response.getBody());
    }

    // ========== KOST PAYMENT TESTS ==========

    @Test
    void testKostPayment_Success_WithoutCoupon() throws Exception {
        PaymentController.KostPaymentRequest req = new PaymentController.KostPaymentRequest();

        String userIdStr = UUID.randomUUID().toString();
        String ownerIdStr = UUID.randomUUID().toString();
        String kostIdStr = UUID.randomUUID().toString();

        req.setUserId(userIdStr);
        req.setOwnerId(ownerIdStr);
        req.setKostId(kostIdStr);
        req.setDescription("Monthly Kost Payment");
        req.setCouponCode(null);

        UUID userId = UUID.fromString(userIdStr);
        UUID ownerId = UUID.fromString(ownerIdStr);
        UUID kostId = UUID.fromString(kostIdStr);

        Payment expectedPayment = new PaymentBuilder()
                .id(2L)
                .userId(userId)
                .ownerId(ownerId)
                .kostId(kostId)
                .amount(500000.0)
                .description("Monthly Kost Payment")
                .paymentType(PaymentTypeEnum.KOST_PAYMENT)
                .paymentStatus(PaymentStatusEnum.SUCCESS)
                .transactionDateTime(LocalDateTime.now())
                .build();

        when(penyewaanKosService.hasPendingPenyewaan(userId, kostId)).thenReturn(true);
        when(paymentService.getKostPrice(kostId)).thenReturn(500000.0);
        when(paymentService.recordKostPayment(userId, ownerId, kostId, 500000.0, "Monthly Kost Payment"))
                .thenReturn(CompletableFuture.completedFuture(expectedPayment));

        CompletableFuture<ResponseEntity<?>> responseFuture = paymentController.kostPayment(req, "true");
        ResponseEntity<?> response = responseFuture.get();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedPayment, response.getBody());
    }

    @Test
    void testKostPayment_Success_WithCoupon() throws Exception {
        PaymentController.KostPaymentRequest req = new PaymentController.KostPaymentRequest();

        String userIdStr = UUID.randomUUID().toString();
        String ownerIdStr = UUID.randomUUID().toString();
        String kostIdStr = UUID.randomUUID().toString();

        req.setUserId(userIdStr);
        req.setOwnerId(ownerIdStr);
        req.setKostId(kostIdStr);
        req.setDescription("Monthly Kost Payment");
        req.setCouponCode("DISCOUNT50");

        UUID userId = UUID.fromString(userIdStr);
        UUID ownerId = UUID.fromString(ownerIdStr);
        UUID kostId = UUID.fromString(kostIdStr);

        Payment expectedPayment = new PaymentBuilder()
                .id(3L)
                .userId(userId)
                .ownerId(ownerId)
                .kostId(kostId)
                .amount(250000.0)
                .description("Monthly Kost Payment")
                .paymentType(PaymentTypeEnum.KOST_PAYMENT)
                .paymentStatus(PaymentStatusEnum.SUCCESS)
                .transactionDateTime(LocalDateTime.now())
                .build();

        when(penyewaanKosService.hasPendingPenyewaan(userId, kostId)).thenReturn(true);
        when(paymentService.getKostPrice(kostId)).thenReturn(500000.0);
        when(paymentService.processKostPaymentWithKupon(userId, ownerId, kostId, 500000.0, "DISCOUNT50"))
                .thenReturn(CompletableFuture.completedFuture(expectedPayment));

        CompletableFuture<ResponseEntity<?>> responseFuture = paymentController.kostPayment(req, null);
        ResponseEntity<?> response = responseFuture.get();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedPayment, response.getBody());
    }

    @Test
    void testKostPayment_Success_WithBlankCoupon() throws Exception {
        PaymentController.KostPaymentRequest req = new PaymentController.KostPaymentRequest();

        String userIdStr = UUID.randomUUID().toString();
        String ownerIdStr = UUID.randomUUID().toString();
        String kostIdStr = UUID.randomUUID().toString();

        req.setUserId(userIdStr);
        req.setOwnerId(ownerIdStr);
        req.setKostId(kostIdStr);
        req.setDescription("Monthly Kost Payment");
        req.setCouponCode("   "); // blank coupon

        UUID userId = UUID.fromString(userIdStr);
        UUID ownerId = UUID.fromString(ownerIdStr);
        UUID kostId = UUID.fromString(kostIdStr);

        Payment expectedPayment = new PaymentBuilder()
                .id(4L)
                .userId(userId)
                .ownerId(ownerId)
                .kostId(kostId)
                .amount(500000.0)
                .description("Monthly Kost Payment")
                .paymentType(PaymentTypeEnum.KOST_PAYMENT)
                .paymentStatus(PaymentStatusEnum.SUCCESS)
                .transactionDateTime(LocalDateTime.now())
                .build();

        when(penyewaanKosService.hasPendingPenyewaan(userId, kostId)).thenReturn(true);
        when(paymentService.getKostPrice(kostId)).thenReturn(500000.0);
        when(paymentService.recordKostPayment(userId, ownerId, kostId, 500000.0, "Monthly Kost Payment"))
                .thenReturn(CompletableFuture.completedFuture(expectedPayment));

        CompletableFuture<ResponseEntity<?>> responseFuture = paymentController.kostPayment(req, null);
        ResponseEntity<?> response = responseFuture.get();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedPayment, response.getBody());
    }

    @Test
    void testKostPayment_InvalidUUIDFormat() throws Exception {
        PaymentController.KostPaymentRequest req = new PaymentController.KostPaymentRequest();
        req.setUserId("invalid-uuid");
        req.setOwnerId(UUID.randomUUID().toString());
        req.setKostId(UUID.randomUUID().toString());

        CompletableFuture<ResponseEntity<?>> responseFuture = paymentController.kostPayment(req, null);
        ResponseEntity<?> response = responseFuture.get();

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid UUID format for userId, ownerId, or kostId", response.getBody());
    }

    @Test
    void testKostPayment_NoPendingPenyewaan() throws Exception {
        PaymentController.KostPaymentRequest req = new PaymentController.KostPaymentRequest();

        String userIdStr = UUID.randomUUID().toString();
        String ownerIdStr = UUID.randomUUID().toString();
        String kostIdStr = UUID.randomUUID().toString();

        req.setUserId(userIdStr);
        req.setOwnerId(ownerIdStr);
        req.setKostId(kostIdStr);

        UUID userId = UUID.fromString(userIdStr);
        UUID kostId = UUID.fromString(kostIdStr);

        when(penyewaanKosService.hasPendingPenyewaan(userId, kostId)).thenReturn(false);

        CompletableFuture<ResponseEntity<?>> responseFuture = paymentController.kostPayment(req, null);
        ResponseEntity<?> response = responseFuture.get();

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Tidak ada penyewaan kos dengan status DIAJUKAN untuk user ini", response.getBody());
    }

    @Test
    void testKostPayment_GetKostPriceException() throws Exception {
        PaymentController.KostPaymentRequest req = new PaymentController.KostPaymentRequest();

        String userIdStr = UUID.randomUUID().toString();
        String ownerIdStr = UUID.randomUUID().toString();
        String kostIdStr = UUID.randomUUID().toString();

        req.setUserId(userIdStr);
        req.setOwnerId(ownerIdStr);
        req.setKostId(kostIdStr);

        UUID userId = UUID.fromString(userIdStr);
        UUID kostId = UUID.fromString(kostIdStr);

        when(penyewaanKosService.hasPendingPenyewaan(userId, kostId)).thenReturn(true);
        when(paymentService.getKostPrice(kostId)).thenThrow(new RuntimeException("Kost not found"));

        CompletableFuture<ResponseEntity<?>> responseFuture = paymentController.kostPayment(req, null);
        ResponseEntity<?> response = responseFuture.get();

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Kost not found", response.getBody());
    }

    public void testKostPayment_PaymentServiceException() throws Exception {
        PaymentController.KostPaymentRequest req = new PaymentController.KostPaymentRequest();

        String userIdStr = UUID.randomUUID().toString();
        String ownerIdStr = UUID.randomUUID().toString();
        String kostIdStr = UUID.randomUUID().toString();

        req.setUserId(userIdStr);
        req.setOwnerId(ownerIdStr);
        req.setKostId(kostIdStr);
        req.setDescription("Monthly Kost Payment");

        UUID userId = UUID.fromString(userIdStr);
        UUID ownerId = UUID.fromString(ownerIdStr);
        UUID kostId = UUID.fromString(kostIdStr);

        when(penyewaanKosService.hasPendingPenyewaan(userId, kostId)).thenReturn(true);
        when(paymentService.getKostPrice(kostId)).thenReturn(500000.0);

        CompletableFuture<Payment> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Payment failed"));

        when(paymentService.recordKostPayment(userId, ownerId, kostId, 500000.0, "Monthly Kost Payment"))
                .thenReturn(failedFuture);

        CompletableFuture<ResponseEntity<?>> responseFuture = paymentController.kostPayment(req, null);
        ResponseEntity<?> response = responseFuture.get();

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Payment failed", response.getBody());
    }

    // ========== TRANSACTION HISTORY TESTS ==========

    @Test
    void testGetTransactionHistory_Success() throws Exception {
        String userIdStr = UUID.randomUUID().toString();
        UUID userId = UUID.fromString(userIdStr);

        List<Payment> payments = Arrays.asList(
                new PaymentBuilder()
                        .id(10L)
                        .userId(userId)
                        .amount(100000.0)
                        .paymentType(PaymentTypeEnum.TOP_UP)
                        .paymentStatus(PaymentStatusEnum.SUCCESS)
                        .transactionDateTime(LocalDateTime.now())
                        .build()
        );

        when(paymentService.getTransactionHistory(userId))
                .thenReturn(CompletableFuture.completedFuture(payments));

        CompletableFuture<ResponseEntity<?>> responseFuture = paymentController.getTransactionHistory(userIdStr, null);
        ResponseEntity<?> response = responseFuture.get();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(payments, response.getBody());
    }

    @Test
    void testGetTransactionHistory_InvalidUUID() throws Exception {
        CompletableFuture<ResponseEntity<?>> responseFuture = paymentController.getTransactionHistory("invalid-uuid", null);
        ResponseEntity<?> response = responseFuture.get();

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid UUID format for userId", response.getBody());
    }

    // ========== FILTERED TRANSACTION HISTORY TESTS ==========

    @Test
    void testGetFilteredTransactionHistory_Success() throws Exception {
        String userIdStr = UUID.randomUUID().toString();
        UUID userId = UUID.fromString(userIdStr);
        PaymentTypeEnum paymentType = PaymentTypeEnum.TOP_UP;
        LocalDateTime startDateTime = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2024, 12, 31, 23, 59);

        List<Payment> filteredPayments = Arrays.asList(
                new PaymentBuilder()
                        .id(11L)
                        .userId(userId)
                        .amount(120000.0)
                        .paymentType(paymentType)
                        .paymentStatus(PaymentStatusEnum.SUCCESS)
                        .transactionDateTime(LocalDateTime.of(2024, 5, 10, 14, 30))
                        .build()
        );

        when(paymentService.getFilteredTransactionHistory(userId, paymentType, startDateTime, endDateTime))
                .thenReturn(CompletableFuture.completedFuture(filteredPayments));

        CompletableFuture<ResponseEntity<?>> responseFuture = paymentController.getFilteredTransactionHistory(userIdStr, paymentType, startDateTime, endDateTime, null);
        ResponseEntity<?> response = responseFuture.get();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(filteredPayments, response.getBody());
    }

    @Test
    void testGetFilteredTransactionHistory_InvalidUUID() throws Exception {
        PaymentTypeEnum paymentType = PaymentTypeEnum.TOP_UP;
        LocalDateTime startDateTime = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2024, 12, 31, 23, 59);

        CompletableFuture<ResponseEntity<?>> responseFuture = paymentController.getFilteredTransactionHistory("invalid-uuid", paymentType, startDateTime, endDateTime, null);
        ResponseEntity<?> response = responseFuture.get();

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid UUID format for userId", response.getBody());
    }

    // ========== PENYEWAAN KOS TESTS ==========

    @Test
    void testGetDiajukanPenyewaanKosByUser_Success() {
        String userIdStr = UUID.randomUUID().toString();
        UUID userId = UUID.fromString(userIdStr);

        Kost mockKos = mock(Kost.class);
        when(mockKos.getKostID()).thenReturn(UUID.randomUUID());
        when(mockKos.getOwnerId()).thenReturn(UUID.randomUUID());

        PenyewaanKos penyewaanKos = new PenyewaanKos();
        penyewaanKos.setId(UUID.randomUUID());
        penyewaanKos.setNamaLengkap("John Doe");
        penyewaanKos.setNomorTelepon("081234567890");
        penyewaanKos.setTanggalCheckIn(LocalDate.now());
        penyewaanKos.setDurasiBulan(6);
        penyewaanKos.setKos(mockKos);
        penyewaanKos.setStatus(StatusPenyewaan.DIAJUKAN);
        penyewaanKos.setUserId(userId);

        when(penyewaanKosService.getAllByUserIdAndStatus(userId, StatusPenyewaan.DIAJUKAN))
                .thenReturn(Arrays.asList(penyewaanKos));

        ResponseEntity<?> response = paymentController.getDiajukanPenyewaanKosByUser(userIdStr);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<PaymentController.PenyewaanKosDTO> dtos = (List<PaymentController.PenyewaanKosDTO>) response.getBody();
        assertNotNull(dtos);
        assertEquals(1, dtos.size());

        PaymentController.PenyewaanKosDTO dto = dtos.get(0);
        assertEquals(penyewaanKos.getId(), dto.getId());
        assertEquals(penyewaanKos.getNamaLengkap(), dto.getNamaLengkap());
        assertEquals(penyewaanKos.getNomorTelepon(), dto.getNomorTelepon());
        assertEquals(penyewaanKos.getTanggalCheckIn(), dto.getTanggalCheckIn());
        assertEquals(penyewaanKos.getDurasiBulan(), dto.getDurasiBulan());
        assertEquals(penyewaanKos.getStatus(), dto.getStatus());
        assertEquals(penyewaanKos.getUserId(), dto.getUserId());
    }

    @Test
    void testGetDiajukanPenyewaanKosByUser_InvalidUUID() {
        ResponseEntity<?> response = paymentController.getDiajukanPenyewaanKosByUser("invalid-uuid");

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Invalid UUID for userId", response.getBody());
    }
}