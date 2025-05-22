package id.cs.ui.advprog.inthecost.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import id.cs.ui.advprog.inthecost.builder.PaymentBuilder;
import id.cs.ui.advprog.inthecost.enums.PaymentStatusEnum;
import id.cs.ui.advprog.inthecost.enums.PaymentTypeEnum;
import id.cs.ui.advprog.inthecost.model.Payment;
import id.cs.ui.advprog.inthecost.service.PaymentService;
import id.cs.ui.advprog.inthecost.service.PenyewaanKosService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PaymentControllerTest {

    @InjectMocks
    private PaymentController paymentController;

    @Mock
    private PaymentService paymentService;

    @Mock
    private PenyewaanKosService penyewaanKosService; // Mock tambahan untuk service yang null

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testTopUp() {
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
                .thenReturn(expectedPayment);

        String testHeaderValue = "true";

        var response = paymentController.topUp(req, testHeaderValue);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedPayment, response.getBody());
    }

    @Test
    void testKostPayment_withoutCoupon() {
        PaymentController.KostPaymentRequest req = new PaymentController.KostPaymentRequest();

        String userIdStr = UUID.randomUUID().toString();
        String ownerIdStr = UUID.randomUUID().toString();
        String kostIdStr = UUID.randomUUID().toString();

        req.setUserId(userIdStr);
        req.setOwnerId(ownerIdStr);
        req.setKostId(kostIdStr);
        req.setDescription("Monthly Kost Payment");
        req.setCouponCode(null); // no coupon for this test

        UUID userId = UUID.fromString(userIdStr);
        UUID ownerId = UUID.fromString(ownerIdStr);
        UUID kostId = UUID.fromString(kostIdStr);

        LocalDateTime now = LocalDateTime.now();

        Payment expectedPayment = new PaymentBuilder()
                .id(2L)
                .userId(userId)
                .ownerId(ownerId)
                .kostId(kostId)
                .amount(500000.0)
                .description("Monthly Kost Payment")
                .paymentType(PaymentTypeEnum.KOST_PAYMENT)
                .paymentStatus(PaymentStatusEnum.SUCCESS)
                .transactionDateTime(now)
                .build();

        // Mock penyewaanKosService supaya tidak null dan mengembalikan nilai benar
        when(penyewaanKosService.hasPendingPenyewaan(userId, kostId)).thenReturn(true);

        when(paymentService.getKostPrice(kostId)).thenReturn(500000.0);
        when(paymentService.recordKostPayment(userId, ownerId, kostId, 500000.0, "Monthly Kost Payment"))
                .thenReturn(expectedPayment);

        String testHeaderValue = "true";

        var response = paymentController.kostPayment(req, testHeaderValue);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedPayment, response.getBody());
    }

    @Test
    void testGetTransactionHistory() {
        UUID userId = UUID.randomUUID();
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

        when(paymentService.getTransactionHistory(userId)).thenReturn(payments);

        var response = paymentController.getTransactionHistory(userId.toString(), null);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(payments, response.getBody());
    }

    @Test
    void testGetFilteredTransactionHistory() {
        UUID userId = UUID.randomUUID();
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
                .thenReturn(filteredPayments);

        var response = paymentController.getFilteredTransactionHistory(userId.toString(), paymentType, startDateTime, endDateTime, null);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(filteredPayments, response.getBody());
    }
}