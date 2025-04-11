package id.cs.ui.advprog.inthecost.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentTest {

    private Payment payment;
    private Payment.PaymentBuilder paymentBuilder;

    @BeforeEach
    void setUp() {
        paymentBuilder = new Payment.PaymentBuilder();
    }

    @Test
    void testPaymentBuilderAndGetters() {
        // Arrange
        Long id = 1L;
        Double amount = 100000.0;
        LocalDate date = LocalDate.of(2023, 5, 15);
        String paymentType = "TOP_UP";
        String description = "Top up saldo";
        PaymentStatus paymentStatus = PaymentStatus.SUCCESS;
        Long userId = 1L;

        // Act
        payment = paymentBuilder
                .id(id)
                .amount(amount)
                .date(date)
                .paymentType(paymentType)
                .description(description)
                .paymentStatus(paymentStatus)
                .userId(userId)
                .build();

        // Assert
        assertEquals(id, payment.getId());
        assertEquals(amount, payment.getAmount());
        assertEquals(date, payment.getDate());
        assertEquals(paymentType, payment.getPaymentType());
        assertEquals(description, payment.getDescription());
        assertEquals(paymentStatus, payment.getPaymentStatus());
        assertEquals(userId, payment.getUserId());
    }

    @Test
    void testPaymentBuilderWithAdditionalFields() {
        // Arrange
        Long id = 2L;
        Double amount = 500000.0;
        LocalDate date = LocalDate.of(2023, 5, 15);
        String paymentType = "KOST_PAYMENT";
        String description = "Pembayaran kos bulan Mei";
        PaymentStatus paymentStatus = PaymentStatus.SUCCESS;
        Long userId = 1L;
        Long ownerId = 2L;
        Long kostId = 3L;

        // Act
        payment = paymentBuilder
                .id(id)
                .amount(amount)
                .date(date)
                .paymentType(paymentType)
                .description(description)
                .paymentStatus(paymentStatus)
                .userId(userId)
                .ownerId(ownerId)
                .kostId(kostId)
                .build();

        // Assert
        assertEquals(id, payment.getId());
        assertEquals(amount, payment.getAmount());
        assertEquals(date, payment.getDate());
        assertEquals(paymentType, payment.getPaymentType());
        assertEquals(description, payment.getDescription());
        assertEquals(paymentStatus, payment.getPaymentStatus());
        assertEquals(userId, payment.getUserId());
        assertEquals(ownerId, payment.getOwnerId());
        assertEquals(kostId, payment.getKostId());
    }

    @Test
    void testSetters() {
        // Arrange
        payment = new Payment();
        Long id = 3L;
        Double amount = 750000.0;
        LocalDate date = LocalDate.of(2023, 6, 15);
        String paymentType = "KOST_PAYMENT";
        String description = "Pembayaran kos bulan Juni";
        PaymentStatus paymentStatus = PaymentStatus.PENDING;
        Long userId = 1L;
        Long ownerId = 2L;
        Long kostId = 3L;

        // Act
        payment.setId(id);
        payment.setAmount(amount);
        payment.setDate(date);
        payment.setPaymentType(paymentType);
        payment.setDescription(description);
        payment.setPaymentStatus(paymentStatus);
        payment.setUserId(userId);
        payment.setOwnerId(ownerId);
        payment.setKostId(kostId);

        // Assert
        assertEquals(id, payment.getId());
        assertEquals(amount, payment.getAmount());
        assertEquals(date, payment.getDate());
        assertEquals(paymentType, payment.getPaymentType());
        assertEquals(description, payment.getDescription());
        assertEquals(paymentStatus, payment.getPaymentStatus());
        assertEquals(userId, payment.getUserId());
        assertEquals(ownerId, payment.getOwnerId());
        assertEquals(kostId, payment.getKostId());
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        payment = new Payment();

        // Assert
        assertNull(payment.getId());
        assertNull(payment.getAmount());
        assertNull(payment.getDate());
        assertNull(payment.getPaymentType());
        assertNull(payment.getDescription());
        assertNull(payment.getPaymentStatus());
        assertNull(payment.getUserId());
        assertNull(payment.getOwnerId());
        assertNull(payment.getKostId());
    }

    @Test
    void testPaymentStatusEnum() {
        // Test all enum values
        assertEquals("SUCCESS", PaymentStatus.SUCCESS.name());
        assertEquals("PENDING", PaymentStatus.PENDING.name());
        assertEquals("FAILED", PaymentStatus.FAILED.name());
        assertEquals("CANCELLED", PaymentStatus.CANCELLED.name());
    }
}