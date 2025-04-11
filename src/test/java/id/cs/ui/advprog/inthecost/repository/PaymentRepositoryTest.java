package id.cs.ui.advprog.inthecost.repository;

import id.cs.ui.advprog.inthecost.enums.PaymentStatusEnum;
import id.cs.ui.advprog.inthecost.enums.PaymentTypeEnum;
import id.cs.ui.advprog.inthecost.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

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
        Payment payment1 = createPayment(100000.0, LocalDate.now(), PaymentTypeEnum.TOP_UP,
                "Top up via bank transfer", PaymentStatusEnum.SUCCESS, 1L, null, null);
        Payment payment2 = createPayment(250000.0, LocalDate.now(), PaymentTypeEnum.KOST_PAYMENT,
                "Kost payment for May", PaymentStatusEnum.SUCCESS, 1L, 2L, 3L);
        Payment payment3 = createPayment(300000.0, LocalDate.now(), PaymentTypeEnum.TOP_UP,
                "Top up via credit card", PaymentStatusEnum.SUCCESS, 2L, null, null);

        paymentRepository.save(payment1);
        paymentRepository.save(payment2);
        paymentRepository.save(payment3);

        // Act
        List<Payment> result = paymentRepository.findByUserId(1L);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(p -> p.getAmount().equals(100000.0)));
        assertTrue(result.stream().anyMatch(p -> p.getAmount().equals(250000.0)));
        assertFalse(result.stream().anyMatch(p -> p.getAmount().equals(300000.0)));
    }

    @Test
    void findByOwnerId_shouldReturnOwnerPayments() {
        // Arrange
        Payment payment1 = createPayment(250000.0, LocalDate.now(), PaymentTypeEnum.KOST_PAYMENT,
                "Kost payment for May", PaymentStatusEnum.SUCCESS, 1L, 2L, 3L);
        Payment payment2 = createPayment(300000.0, LocalDate.now(), PaymentTypeEnum.KOST_PAYMENT,
                "Kost payment for May", PaymentStatusEnum.SUCCESS, 3L, 2L, 4L);
        Payment payment3 = createPayment(350000.0, LocalDate.now(), PaymentTypeEnum.KOST_PAYMENT,
                "Kost payment for May", PaymentStatusEnum.SUCCESS, 4L, 5L, 6L);

        paymentRepository.save(payment1);
        paymentRepository.save(payment2);
        paymentRepository.save(payment3);

        // Act
        List<Payment> result = paymentRepository.findByOwnerId(2L);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(p -> p.getUserId().equals(1L)));
        assertTrue(result.stream().anyMatch(p -> p.getUserId().equals(3L)));
        assertFalse(result.stream().anyMatch(p -> p.getUserId().equals(4L)));
    }

    @Test
    void findByPaymentType_shouldReturnPaymentsByType() {
        // Arrange
        Payment payment1 = createPayment(100000.0, LocalDate.now(), PaymentTypeEnum.TOP_UP,
                "Top up via bank transfer", PaymentStatusEnum.SUCCESS, 1L, null, null);
        Payment payment2 = createPayment(250000.0, LocalDate.now(), PaymentTypeEnum.KOST_PAYMENT,
                "Kost payment for May", PaymentStatusEnum.SUCCESS, 1L, 2L, 3L);
        Payment payment3 = createPayment(300000.0, LocalDate.now(), PaymentTypeEnum.TOP_UP,
                "Top up via credit card", PaymentStatusEnum.SUCCESS, 2L, null, null);

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

        Payment payment1 = createPayment(100000.0, today, PaymentTypeEnum.TOP_UP,
                "Top up today", PaymentStatusEnum.SUCCESS, 1L, null, null);
        Payment payment2 = createPayment(250000.0, yesterday, PaymentTypeEnum.KOST_PAYMENT,
                "Kost payment yesterday", PaymentStatusEnum.SUCCESS, 1L, 2L, 3L);
        Payment payment3 = createPayment(300000.0, twoDaysAgo, PaymentTypeEnum.TOP_UP,
                "Top up two days ago", PaymentStatusEnum.SUCCESS, 2L, null, null);
        Payment payment4 = createPayment(350000.0, threeDaysAgo, PaymentTypeEnum.KOST_PAYMENT,
                "Kost payment three days ago", PaymentStatusEnum.SUCCESS, 2L, 3L, 4L);

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
                                  Long userId, Long ownerId, Long kostId) {
        Payment payment = new Payment.PaymentBuilder()
                .amount(amount)
                .date(date)
                .paymentType(paymentType)
                .description(description)
                .paymentStatus(paymentStatus)
                .userId(userId)
                .ownerId(ownerId)
                .kostId(kostId)
                .build();

        return payment;
    }
}