package id.cs.ui.advprog.inthecost.controller;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import id.cs.ui.advprog.inthecost.enums.PaymentStatusEnum;
import id.cs.ui.advprog.inthecost.enums.PaymentTypeEnum;
import id.cs.ui.advprog.inthecost.model.Payment;
import id.cs.ui.advprog.inthecost.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PaymentControllerTest {

    @InjectMocks
    private PaymentController paymentController;

    @Mock
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testTopUp() {
        // Arrange
        PaymentController.TopUpRequest req = new PaymentController.TopUpRequest();
        String userIdStr = UUID.randomUUID().toString();
        req.setUserId(userIdStr);
        req.setAmount(150000.0);
        req.setDescription("Top Up via bank");

        UUID userId = UUID.fromString(userIdStr);

        Payment expectedPayment = Payment.builder()
                .id(1L)
                .userId(userId)
                .amount(150000.0)
                .description("Top Up via bank")
                .paymentType(PaymentTypeEnum.TOP_UP)
                .paymentStatus(PaymentStatusEnum.SUCCESS)
                .date(LocalDate.now())
                .build();

        when(paymentService.recordTopUpPayment(userId, 150000.0, "Top Up via bank"))
                .thenReturn(expectedPayment);

        String testHeaderValue = "true";

        // Act
        var response = paymentController.topUp(req, testHeaderValue);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedPayment, response.getBody());
    }

    @Test
    void testKostPayment_withDiscount() {
        // Arrange
        PaymentController.KostPaymentRequest req = new PaymentController.KostPaymentRequest();

        String userIdStr = UUID.randomUUID().toString();
        String ownerIdStr = UUID.randomUUID().toString();
        String kostIdStr = UUID.randomUUID().toString();

        req.setUserId(userIdStr);
        req.setOwnerId(ownerIdStr);
        req.setKostId(kostIdStr);
        req.setAmount(500000.0);
        req.setDescription("Monthly Kost Payment");
        req.setCouponCode("DISCOUNT2025");
        req.setCouponQuantity(1);
        req.setDiscountPrice(50000.0);  // discount 50k

        UUID userId = UUID.fromString(userIdStr);
        UUID ownerId = UUID.fromString(ownerIdStr);
        UUID kostId = UUID.fromString(kostIdStr);

        double discountedAmount = req.getAmount() - req.getDiscountPrice(); // 450000.0

        Payment expectedPayment = Payment.builder()
                .id(2L)
                .userId(userId)
                .ownerId(ownerId)
                .kostId(kostId)
                .amount(discountedAmount)
                .description("Monthly Kost Payment")
                .paymentType(PaymentTypeEnum.KOST_PAYMENT)
                .paymentStatus(PaymentStatusEnum.SUCCESS)
                .date(LocalDate.now())
                .build();

        when(paymentService.recordKostPayment(userId, ownerId, kostId, discountedAmount, "Monthly Kost Payment"))
                .thenReturn(expectedPayment);

        String testHeaderValue = "true";

        // Act
        var response = paymentController.kostPayment(req, testHeaderValue);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedPayment, response.getBody());
    }

    @Test
    void testGetTransactionHistory() {
        // Arrange
        UUID userId = UUID.randomUUID();
        List<Payment> payments = Arrays.asList(
                Payment.builder()
                        .id(10L)
                        .userId(userId)
                        .amount(100000.0)
                        .paymentType(PaymentTypeEnum.TOP_UP)
                        .paymentStatus(PaymentStatusEnum.SUCCESS)
                        .date(LocalDate.now())
                        .build()
        );

        when(paymentService.getTransactionHistory(userId)).thenReturn(payments);

        // Act
        var response = paymentController.getTransactionHistory(userId.toString(), null);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(payments, response.getBody());
    }

    @Test
    void testGetFilteredTransactionHistory() {
        // Arrange
        UUID userId = UUID.randomUUID();
        PaymentTypeEnum paymentType = PaymentTypeEnum.TOP_UP;
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        List<Payment> filteredPayments = Arrays.asList(
                Payment.builder()
                        .id(11L)
                        .userId(userId)
                        .amount(120000.0)
                        .paymentType(paymentType)
                        .paymentStatus(PaymentStatusEnum.SUCCESS)
                        .date(LocalDate.of(2024, 5, 10))
                        .build()
        );

        when(paymentService.getFilteredTransactionHistory(userId, paymentType, startDate, endDate))
                .thenReturn(filteredPayments);

        // Act
        var response = paymentController.getFilteredTransactionHistory(userId.toString(), paymentType, startDate, endDate, null);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(filteredPayments, response.getBody());
    }
}