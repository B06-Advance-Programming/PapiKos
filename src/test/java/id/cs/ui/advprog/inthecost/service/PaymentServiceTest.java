package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.enums.PaymentStatusEnum;
import id.cs.ui.advprog.inthecost.enums.PaymentTypeEnum;
import id.cs.ui.advprog.inthecost.model.Payment;
import id.cs.ui.advprog.inthecost.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        // Any general setup
    }

    @Test
    void recordTopUpPayment_shouldCreateTopUpPaymentRecord() {
        // Arrange
        Long userId = 1L;
        Double amount = 100000.0;
        String description = "Top up via bank transfer";
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Payment result = paymentService.recordTopUpPayment(userId, amount, description);

        // Assert
        assertEquals(PaymentTypeEnum.TOP_UP, result.getPaymentType());
        assertEquals(PaymentStatusEnum.SUCCESS, result.getPaymentStatus());
        assertEquals(amount, result.getAmount());
        assertEquals(description, result.getDescription());
        assertEquals(userId, result.getUserId());
        assertNull(result.getOwnerId());
        assertNull(result.getKostId());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void recordKostPayment_shouldCreateKostPaymentRecord() {
        // Arrange
        Long userId = 1L;
        Long ownerId = 2L;
        Long kostId = 3L;
        Double amount = 300000.0;
        String description = "Kost payment for May";
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Payment result = paymentService.recordKostPayment(userId, ownerId, kostId, amount, description);

        // Assert
        assertEquals(PaymentTypeEnum.KOST_PAYMENT, result.getPaymentType());
        assertEquals(PaymentStatusEnum.SUCCESS, result.getPaymentStatus());
        assertEquals(amount, result.getAmount());
        assertEquals(description, result.getDescription());
        assertEquals(userId, result.getUserId());
        assertEquals(ownerId, result.getOwnerId());
        assertEquals(kostId, result.getKostId());
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void getTransactionHistory_shouldReturnUserTransactions() {
        // Arrange
        Long userId = 1L;
        List<Payment> userPayments = new ArrayList<>();
        userPayments.add(createPayment(1L, 100000.0, LocalDate.now().minusDays(2),
                PaymentTypeEnum.TOP_UP, "Top up", PaymentStatusEnum.SUCCESS, userId, null, null));
        userPayments.add(createPayment(2L, 200000.0, LocalDate.now().minusDays(1),
                PaymentTypeEnum.KOST_PAYMENT, "Kost payment", PaymentStatusEnum.SUCCESS, userId, 2L, 3L));

        when(paymentRepository.findByUserId(userId)).thenReturn(userPayments);

        // Act
        List<Payment> result = paymentService.getTransactionHistory(userId);

        // Assert
        assertEquals(2, result.size());
        verify(paymentRepository).findByUserId(userId);
    }

    @Test
    void getFilteredTransactionHistory_shouldFilterByDateRange() {
        // Arrange
        Long userId = 1L;
        LocalDate startDate = LocalDate.now().minusDays(5);
        LocalDate endDate = LocalDate.now();

        List<Payment> allUserPayments = new ArrayList<>();
        // These should be included in the result (within date range)
        allUserPayments.add(createPayment(1L, 100000.0, LocalDate.now().minusDays(2),
                PaymentTypeEnum.TOP_UP, "Top up", PaymentStatusEnum.SUCCESS, userId, null, null));
        allUserPayments.add(createPayment(2L, 200000.0, LocalDate.now().minusDays(1),
                PaymentTypeEnum.KOST_PAYMENT, "Kost payment", PaymentStatusEnum.SUCCESS, userId, 2L, 3L));
        // This one should be filtered out (outside date range)
        allUserPayments.add(createPayment(3L, 150000.0, LocalDate.now().minusDays(10),
                PaymentTypeEnum.TOP_UP, "Old top up", PaymentStatusEnum.SUCCESS, userId, null, null));

        // We'll filter in the service, so we just return all user payments here
        when(paymentRepository.findByUserId(userId)).thenReturn(allUserPayments);

        // Act
        List<Payment> result = paymentService.getFilteredTransactionHistory(
                userId, null, startDate, endDate);

        // Assert
        assertEquals(2, result.size());
        // Verify payments are within the date range
        for (Payment payment : result) {
            assertTrue(
                    !payment.getDate().isBefore(startDate) &&
                            !payment.getDate().isAfter(endDate)
            );
        }
        verify(paymentRepository).findByUserId(userId);
    }

    @Test
    void getFilteredTransactionHistory_shouldFilterByPaymentType() {
        // Arrange
        Long userId = 1L;
        List<Payment> allUserPayments = new ArrayList<>();
        // This should be included (type = TOP_UP)
        allUserPayments.add(createPayment(1L, 100000.0, LocalDate.now().minusDays(2),
                PaymentTypeEnum.TOP_UP, "Top up", PaymentStatusEnum.SUCCESS, userId, null, null));
        // This should be filtered out (type = KOST_PAYMENT)
        allUserPayments.add(createPayment(2L, 200000.0, LocalDate.now().minusDays(1),
                PaymentTypeEnum.KOST_PAYMENT, "Kost payment", PaymentStatusEnum.SUCCESS, userId, 2L, 3L));

        // We'll filter in the service
        when(paymentRepository.findByUserId(userId)).thenReturn(allUserPayments);

        // Act
        List<Payment> result = paymentService.getFilteredTransactionHistory(
                userId, PaymentTypeEnum.TOP_UP, null, null);

        // Assert
        assertEquals(1, result.size());
        assertEquals(PaymentTypeEnum.TOP_UP, result.get(0).getPaymentType());
        verify(paymentRepository).findByUserId(userId);
    }

    @Test
    void getFilteredTransactionHistory_shouldFilterByPaymentTypeAndDateRange() {
        // Arrange
        Long userId = 1L;
        LocalDate startDate = LocalDate.now().minusDays(5);
        LocalDate endDate = LocalDate.now();

        List<Payment> allUserPayments = new ArrayList<>();
        // Should be included (TOP_UP and within date range)
        allUserPayments.add(createPayment(1L, 100000.0, LocalDate.now().minusDays(2),
                PaymentTypeEnum.TOP_UP, "Top up", PaymentStatusEnum.SUCCESS, userId, null, null));
        // Should be filtered out (KOST_PAYMENT)
        allUserPayments.add(createPayment(2L, 200000.0, LocalDate.now().minusDays(1),
                PaymentTypeEnum.KOST_PAYMENT, "Kost payment", PaymentStatusEnum.SUCCESS, userId, 2L, 3L));
        // Should be filtered out (outside date range)
        allUserPayments.add(createPayment(3L, 150000.0, LocalDate.now().minusDays(10),
                PaymentTypeEnum.TOP_UP, "Old top up", PaymentStatusEnum.SUCCESS, userId, null, null));

        when(paymentRepository.findByUserId(userId)).thenReturn(allUserPayments);

        // Act
        List<Payment> result = paymentService.getFilteredTransactionHistory(
                userId, PaymentTypeEnum.TOP_UP, startDate, endDate);

        // Assert
        assertEquals(1, result.size());
        assertEquals(PaymentTypeEnum.TOP_UP, result.get(0).getPaymentType());
        // Verify within date range
        LocalDate paymentDate = result.get(0).getDate();
        assertTrue(!paymentDate.isBefore(startDate) && !paymentDate.isAfter(endDate));
        verify(paymentRepository).findByUserId(userId);
    }

    @Test
    void getOwnerTransactionHistory_shouldReturnOwnerTransactions() {
        // Arrange
        Long ownerId = 2L;
        List<Payment> ownerPayments = new ArrayList<>();
        ownerPayments.add(createPayment(1L, 200000.0, LocalDate.now().minusDays(2),
                PaymentTypeEnum.KOST_PAYMENT, "Kost payment from user 1", PaymentStatusEnum.SUCCESS, 1L, ownerId, 3L));
        ownerPayments.add(createPayment(2L, 300000.0, LocalDate.now().minusDays(1),
                PaymentTypeEnum.KOST_PAYMENT, "Kost payment from user 3", PaymentStatusEnum.SUCCESS, 3L, ownerId, 4L));

        when(paymentRepository.findByOwnerId(ownerId)).thenReturn(ownerPayments);

        // Act
        List<Payment> result = paymentService.getOwnerTransactionHistory(ownerId);

        // Assert
        assertEquals(2, result.size());
        verify(paymentRepository).findByOwnerId(ownerId);
    }

    @Test
    void getFilteredOwnerTransactionHistory_shouldFilterByDateRange() {
        // Arrange
        Long ownerId = 2L;
        LocalDate startDate = LocalDate.now().minusDays(3);
        LocalDate endDate = LocalDate.now();

        List<Payment> allOwnerPayments = new ArrayList<>();
        // Should be included (within date range)
        allOwnerPayments.add(createPayment(1L, 200000.0, LocalDate.now().minusDays(2),
                PaymentTypeEnum.KOST_PAYMENT, "Kost payment", PaymentStatusEnum.SUCCESS, 1L, ownerId, 3L));
        // Should be filtered out (outside date range)
        allOwnerPayments.add(createPayment(2L, 300000.0, LocalDate.now().minusDays(5),
                PaymentTypeEnum.KOST_PAYMENT, "Old kost payment", PaymentStatusEnum.SUCCESS, 3L, ownerId, 4L));

        when(paymentRepository.findByOwnerId(ownerId)).thenReturn(allOwnerPayments);

        // Act
        List<Payment> result = paymentService.getFilteredOwnerTransactionHistory(
                ownerId, null, startDate, endDate);

        // Assert
        assertEquals(1, result.size());
        verify(paymentRepository).findByOwnerId(ownerId);
    }

    private Payment createPayment(Long id, Double amount, LocalDate date, PaymentTypeEnum paymentType,
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

        payment.setId(id);
        return payment;
    }
}