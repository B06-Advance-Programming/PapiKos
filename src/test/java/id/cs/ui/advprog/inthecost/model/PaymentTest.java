package id.cs.ui.advprog.inthecost.model;

import id.cs.ui.advprog.inthecost.enums.PaymentStatusEnum;
import id.cs.ui.advprog.inthecost.enums.PaymentTypeEnum;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentTest {

    private Payment payment;

    @Test
    void testPaymentBuilderAndGetters() {
        // Arrange
        Long id = 1L;
        Double amount = 100000.0;
        LocalDateTime date = LocalDateTime.of(2023, 6, 15, 0, 0);
        PaymentTypeEnum paymentType = PaymentTypeEnum.TOP_UP;
        String description = "Top up saldo";
        PaymentStatusEnum paymentStatus = PaymentStatusEnum.SUCCESS;
        UUID userId = UUID.randomUUID();

        // Act
        payment = Payment.builder()
                .id(id)
                .amount(amount)
                .transactionDateTime(date)
                .paymentType(paymentType)
                .description(description)
                .paymentStatus(paymentStatus)
                .userId(userId)
                .build();

        // Assert
        assertEquals(id, payment.getId());
        assertEquals(amount, payment.getAmount());
        assertEquals(date, payment.getTransactionDateTime());
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
        LocalDateTime date = LocalDateTime.of(2023, 6, 15, 0, 0);
        PaymentTypeEnum paymentType = PaymentTypeEnum.KOST_PAYMENT;
        String description = "Pembayaran kos bulan Mei";
        PaymentStatusEnum paymentStatus = PaymentStatusEnum.SUCCESS;
        UUID userId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID kostId = UUID.randomUUID();

        // Act
        payment = Payment.builder()
                .id(id)
                .amount(amount)
                .transactionDateTime(date)
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
        assertEquals(date, payment.getTransactionDateTime());
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
        LocalDateTime date = LocalDateTime.of(2023, 6, 15, 0, 0);
        PaymentTypeEnum paymentType = PaymentTypeEnum.KOST_PAYMENT;
        String description = "Pembayaran kos bulan Juni";
        PaymentStatusEnum paymentStatus = PaymentStatusEnum.PENDING;
        UUID userId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID kostId = UUID.randomUUID();

        // Act
        payment.setId(id);
        payment.setAmount(amount);
        payment.setTransactionDateTime(date);
        payment.setPaymentType(paymentType);
        payment.setDescription(description);
        payment.setPaymentStatus(paymentStatus);
        payment.setUserId(userId);
        payment.setOwnerId(ownerId);
        payment.setKostId(kostId);

        // Assert
        assertEquals(id, payment.getId());
        assertEquals(amount, payment.getAmount());
        assertEquals(date, payment.getTransactionDateTime());
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
        assertNull(payment.getTransactionDateTime());
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
        assertEquals("SUCCESS", PaymentStatusEnum.SUCCESS.name());
        assertEquals("PENDING", PaymentStatusEnum.PENDING.name());
        assertEquals("FAILED", PaymentStatusEnum.FAILED.name());
        assertEquals("CANCELLED", PaymentStatusEnum.CANCELLED.name());
    }

    @Test
    void testPaymentTypeEnum() {
        // Test all enum values
        assertEquals("TOP_UP", PaymentTypeEnum.TOP_UP.name());
        assertEquals("KOST_PAYMENT", PaymentTypeEnum.KOST_PAYMENT.name());
        assertEquals("WITHDRAWAL", PaymentTypeEnum.WITHDRAWAL.name());
        assertEquals("REFUND", PaymentTypeEnum.REFUND.name());
    }

    @Test
    void testPaymentWithTypeEnum() {
        // Arrange
        Long id = 4L;
        Double amount = 250000.0;
        LocalDateTime date = LocalDateTime.now();
        PaymentTypeEnum paymentType = PaymentTypeEnum.TOP_UP;
        String description = "Top up via bank transfer";
        PaymentStatusEnum paymentStatus = PaymentStatusEnum.SUCCESS;
        UUID userId = UUID.randomUUID();

        // Act
        payment = Payment.builder()
                .id(id)
                .amount(amount)
                .transactionDateTime(date)
                .paymentType(paymentType)
                .description(description)
                .paymentStatus(paymentStatus)
                .userId(userId)
                .build();

        // Assert
        assertEquals(id, payment.getId());
        assertEquals(amount, payment.getAmount());
        assertEquals(date, payment.getTransactionDateTime());
        assertEquals(paymentType, payment.getPaymentType());
        assertEquals(description, payment.getDescription());
        assertEquals(paymentStatus, payment.getPaymentStatus());
        assertEquals(userId, payment.getUserId());
    }
}