package id.cs.ui.advprog.inthecost.repository;

import id.cs.ui.advprog.inthecost.enums.PaymentStatusEnum;
import id.cs.ui.advprog.inthecost.enums.PaymentTypeEnum;
import id.cs.ui.advprog.inthecost.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentRepositoryTest {

    private PaymentRepository paymentRepository;

    @BeforeEach
    void setUp() {
        paymentRepository = new PaymentRepositoryImpl();
    }

    @Test
    void findByUserId_shouldReturnUserPayments() {
        // Arrange
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID kostId = UUID.randomUUID();

        Payment payment1 = createPayment(100000.0, LocalDate.now(), PaymentTypeEnum.TOP_UP,
                "Top up via bank transfer", PaymentStatusEnum.SUCCESS, userId1, null, null);
        Payment payment2 = createPayment(250000.0, LocalDate.now(), PaymentTypeEnum.KOST_PAYMENT,
                "Kost payment for May", PaymentStatusEnum.SUCCESS, userId1, ownerId, kostId);
        Payment payment3 = createPayment(300000.0, LocalDate.now(), PaymentTypeEnum.TOP_UP,
                "Top up via credit card", PaymentStatusEnum.SUCCESS, userId2, null, null);

        paymentRepository.save(payment1);
        paymentRepository.save(payment2);
        paymentRepository.save(payment3);

        // Act
        List<Payment> result = paymentRepository.findByUserId(userId1);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(p -> p.getAmount().equals(100000.0)));
        assertTrue(result.stream().anyMatch(p -> p.getAmount().equals(250000.0)));
        assertFalse(result.stream().anyMatch(p -> p.getAmount().equals(300000.0)));
    }

    @Test
    void findByOwnerId_shouldReturnOwnerPayments() {
        // Arrange
        UUID ownerId1 = UUID.randomUUID();
        UUID ownerId2 = UUID.randomUUID();
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        UUID kostId1 = UUID.randomUUID();
        UUID kostId2 = UUID.randomUUID();

        Payment payment1 = createPayment(250000.0, LocalDate.now(), PaymentTypeEnum.KOST_PAYMENT,
                "Kost payment for May", PaymentStatusEnum.SUCCESS, userId1, ownerId1, kostId1);
        Payment payment2 = createPayment(300000.0, LocalDate.now(), PaymentTypeEnum.KOST_PAYMENT,
                "Kost payment for May", PaymentStatusEnum.SUCCESS, userId2, ownerId1, kostId2);
        Payment payment3 = createPayment(350000.0, LocalDate.now(), PaymentTypeEnum.KOST_PAYMENT,
                "Kost payment for May", PaymentStatusEnum.SUCCESS, UUID.randomUUID(), ownerId2, UUID.randomUUID());

        paymentRepository.save(payment1);
        paymentRepository.save(payment2);
        paymentRepository.save(payment3);

        // Act
        List<Payment> result = paymentRepository.findByOwnerId(ownerId1);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(p -> p.getUserId().equals(userId1)));
        assertTrue(result.stream().anyMatch(p -> p.getUserId().equals(userId2)));
        assertFalse(result.stream().anyMatch(p -> !p.getUserId().equals(userId1) && !p.getUserId().equals(userId2)));
    }

    @Test
    void findByPaymentType_shouldReturnPaymentsByType() {
        // Arrange
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID kostId = UUID.randomUUID();

        Payment payment1 = createPayment(100000.0, LocalDate.now(), PaymentTypeEnum.TOP_UP,
                "Top up via bank transfer", PaymentStatusEnum.SUCCESS, userId1, null, null);
        Payment payment2 = createPayment(250000.0, LocalDate.now(), PaymentTypeEnum.KOST_PAYMENT,
                "Kost payment for May", PaymentStatusEnum.SUCCESS, userId1, ownerId, kostId);
        Payment payment3 = createPayment(300000.0, LocalDate.now(), PaymentTypeEnum.TOP_UP,
                "Top up via credit card", PaymentStatusEnum.SUCCESS, userId2, null, null);

        paymentRepository.save(payment1);
        paymentRepository.save(payment2);
        paymentRepository.save(payment3);

        // Act
        List<Payment> result = paymentRepository.findByPaymentType(PaymentTypeEnum.TOP_UP);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(p -> p.getAmount().equals(100000.0)));
        assertTrue(result.stream().anyMatch(p -> p.getAmount().equals(300000.0)));
        assertFalse(result.stream().anyMatch(p -> p.getAmount().equals(250000.0)));
    }

    @Test
    void findByDateBetween_shouldReturnPaymentsInDateRange() {
        // Arrange
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate twoDaysAgo = today.minusDays(2);
        LocalDate threeDaysAgo = today.minusDays(3);

        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID kostId = UUID.randomUUID();

        Payment payment1 = createPayment(100000.0, today, PaymentTypeEnum.TOP_UP,
                "Top up today", PaymentStatusEnum.SUCCESS, userId1, null, null);
        Payment payment2 = createPayment(250000.0, yesterday, PaymentTypeEnum.KOST_PAYMENT,
                "Kost payment yesterday", PaymentStatusEnum.SUCCESS, userId1, ownerId, kostId);
        Payment payment3 = createPayment(300000.0, twoDaysAgo, PaymentTypeEnum.TOP_UP,
                "Top up two days ago", PaymentStatusEnum.SUCCESS, userId2, null, null);
        Payment payment4 = createPayment(350000.0, threeDaysAgo, PaymentTypeEnum.KOST_PAYMENT,
                "Kost payment three days ago", PaymentStatusEnum.SUCCESS, userId2, UUID.randomUUID(), UUID.randomUUID());

        paymentRepository.save(payment1);
        paymentRepository.save(payment2);
        paymentRepository.save(payment3);
        paymentRepository.save(payment4);

        // Act
        List<Payment> result = paymentRepository.findByDateBetween(yesterday, today);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(p -> p.getAmount().equals(100000.0)));
        assertTrue(result.stream().anyMatch(p -> p.getAmount().equals(250000.0)));
        assertFalse(result.stream().anyMatch(p -> p.getAmount().equals(300000.0)));
        assertFalse(result.stream().anyMatch(p -> p.getAmount().equals(350000.0)));
    }

    private Payment createPayment(Double amount, LocalDate date, PaymentTypeEnum paymentType,
                                  String description, PaymentStatusEnum paymentStatus,
                                  UUID userId, UUID ownerId, UUID kostId) {
        return Payment.builder()
                .amount(amount)
                .date(date)
                .paymentType(paymentType)
                .description(description)
                .paymentStatus(paymentStatus)
                .userId(userId)
                .ownerId(ownerId)
                .kostId(kostId)
                .build();
    }
}