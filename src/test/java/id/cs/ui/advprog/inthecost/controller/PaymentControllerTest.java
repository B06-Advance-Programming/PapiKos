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
        req.setUserId(1L);
        req.setAmount(150000.0);
        req.setDescription("Top Up via bank");

        Payment expectedPayment = new Payment.PaymentBuilder()
                .id(1L)
                .userId(1L)
                .amount(150000.0)
                .description("Top Up via bank")
                .paymentType(PaymentTypeEnum.TOP_UP)
                .paymentStatus(PaymentStatusEnum.SUCCESS)
                .date(LocalDate.now())
                .build();

        when(paymentService.recordTopUpPayment(1L, 150000.0, "Top Up via bank"))
                .thenReturn(expectedPayment);

        // Act
        var response = paymentController.topUp(req);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedPayment, response.getBody());
    }

    @Test
    void testKostPayment() {
        // Arrange
        PaymentController.KostPaymentRequest req = new PaymentController.KostPaymentRequest();
        req.setUserId(2L);
        req.setOwnerId(20L);
        req.setKostId(5L);
        req.setAmount(500000.0);
        req.setDescription("Monthly Kost Payment");

        Payment expectedPayment = new Payment.PaymentBuilder()
                .id(2L)
                .userId(2L)
                .ownerId(20L)
                .kostId(5L)
                .amount(500000.0)
                .description("Monthly Kost Payment")
                .paymentType(PaymentTypeEnum.KOST_PAYMENT)
                .paymentStatus(PaymentStatusEnum.SUCCESS)
                .date(LocalDate.now())
                .build();

        when(paymentService.recordKostPayment(2L, 20L, 5L, 500000.0, "Monthly Kost Payment"))
                .thenReturn(expectedPayment);

        // Act
        var response = paymentController.kostPayment(req);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expectedPayment, response.getBody());
    }

    @Test
    void testGetTransactionHistory() {
        // Arrange
        Long userId = 1L;
        List<Payment> payments = Arrays.asList(
                new Payment.PaymentBuilder()
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
        var response = paymentController.getTransactionHistory(userId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(payments, response.getBody());
    }

    @Test
    void testGetFilteredTransactionHistory() {
        // Arrange
        Long userId = 1L;
        PaymentTypeEnum paymentType = PaymentTypeEnum.TOP_UP;
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        List<Payment> filteredPayments = Arrays.asList(
                new Payment.PaymentBuilder()
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
        var response = paymentController.getFilteredTransactionHistory(userId, paymentType, startDate, endDate);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(filteredPayments, response.getBody());
    }
}