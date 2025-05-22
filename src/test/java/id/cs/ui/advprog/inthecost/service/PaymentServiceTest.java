package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.builder.PaymentBuilder;
import id.cs.ui.advprog.inthecost.enums.PaymentStatusEnum;
import id.cs.ui.advprog.inthecost.enums.PaymentTypeEnum;
import id.cs.ui.advprog.inthecost.enums.StatusPenyewaan;
import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.Payment;
import id.cs.ui.advprog.inthecost.model.PenyewaanKos;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.repository.PaymentRepository;
import id.cs.ui.advprog.inthecost.repository.PenyewaanKosRepository;
import id.cs.ui.advprog.inthecost.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PenyewaanKosRepository penyewaanKosRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        // Any general setup if needed
    }

    @Test
    void recordTopUpPayment_shouldCreateTopUpPaymentRecord() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Double amount = 100000.0;
        String description = "Top up via bank transfer";

        // Mock User
        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

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
        UUID userId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID kostId = UUID.randomUUID();
        Double amount = 300000.0;
        String description = "Kost payment for May";

        // Mock user with sufficient balance
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setBalance(500000.0);  // Balance more than amount to avoid failed payment
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // Mock owner with any balance (optional but recommended)
        User mockOwner = new User();
        mockOwner.setId(ownerId);
        mockOwner.setBalance(100000.0);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(mockOwner));

        Kost mockKost = new Kost();
        mockKost.setKostID(kostId);

        PenyewaanKos pendingReservation = new PenyewaanKos();
        pendingReservation.setId(UUID.randomUUID());
        pendingReservation.setKos(mockKost);
        pendingReservation.setUserId(userId);
        pendingReservation.setStatus(StatusPenyewaan.DIAJUKAN);

        when(penyewaanKosRepository.findByKos_KostIDAndUserIdAndStatus(
                eq(kostId), eq(userId), eq(StatusPenyewaan.DIAJUKAN)))
                .thenReturn(List.of(pendingReservation));

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Payment result = paymentService.recordKostPayment(userId, ownerId, kostId, amount, description);

        // Assert successful payment status
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
        UUID userId = UUID.randomUUID();
        List<Payment> userPayments = new ArrayList<>();
        userPayments.add(createPayment(1L, 100000.0, LocalDateTime.now().minusDays(2),
                PaymentTypeEnum.TOP_UP, "Top up", PaymentStatusEnum.SUCCESS, userId, null, null));
        userPayments.add(createPayment(2L, 200000.0, LocalDateTime.now().minusDays(1),
                PaymentTypeEnum.KOST_PAYMENT, "Kost payment", PaymentStatusEnum.SUCCESS, userId, UUID.randomUUID(), UUID.randomUUID()));

        when(paymentRepository.findByUserId(userId)).thenReturn(userPayments);

        List<Payment> result = paymentService.getTransactionHistory(userId);

        assertEquals(2, result.size());
        verify(paymentRepository).findByUserId(userId);
    }

    @Test
    void getFilteredTransactionHistory_shouldFilterByDateRange() {
        UUID userId = UUID.randomUUID();
        LocalDateTime startDateTime = LocalDateTime.now().minusDays(5);
        LocalDateTime endDateTime = LocalDateTime.now();

        List<Payment> allUserPayments = new ArrayList<>();
        // Included (inside date range)
        allUserPayments.add(createPayment(1L, 100000.0, LocalDateTime.now().minusDays(2),
                PaymentTypeEnum.TOP_UP, "Top up", PaymentStatusEnum.SUCCESS, userId, null, null));
        allUserPayments.add(createPayment(2L, 200000.0, LocalDateTime.now().minusDays(1),
                PaymentTypeEnum.KOST_PAYMENT, "Kost payment", PaymentStatusEnum.SUCCESS, userId, UUID.randomUUID(), UUID.randomUUID()));
        // Excluded (outside date range)
        allUserPayments.add(createPayment(3L, 150000.0, LocalDateTime.now().minusDays(10),
                PaymentTypeEnum.TOP_UP, "Old top up", PaymentStatusEnum.SUCCESS, userId, null, null));

        when(paymentRepository.findByUserId(userId)).thenReturn(allUserPayments);

        List<Payment> result = paymentService.getFilteredTransactionHistory(
                userId, null, startDateTime, endDateTime);

        assertEquals(2, result.size());
        for (Payment payment : result) {
            assertTrue(!payment.getTransactionDateTime().isBefore(startDateTime) &&
                    !payment.getTransactionDateTime().isAfter(endDateTime));
        }
        verify(paymentRepository).findByUserId(userId);
    }

    @Test
    void getFilteredTransactionHistory_shouldFilterByPaymentType() {
        UUID userId = UUID.randomUUID();
        List<Payment> allUserPayments = new ArrayList<>();
        // Included (TOP_UP)
        allUserPayments.add(createPayment(1L, 100000.0, LocalDateTime.now().minusDays(2),
                PaymentTypeEnum.TOP_UP, "Top up", PaymentStatusEnum.SUCCESS, userId, null, null));
        // Excluded (KOST_PAYMENT)
        allUserPayments.add(createPayment(2L, 200000.0, LocalDateTime.now().minusDays(1),
                PaymentTypeEnum.KOST_PAYMENT, "Kost payment", PaymentStatusEnum.SUCCESS, userId, UUID.randomUUID(), UUID.randomUUID()));

        when(paymentRepository.findByUserId(userId)).thenReturn(allUserPayments);

        List<Payment> result = paymentService.getFilteredTransactionHistory(
                userId, PaymentTypeEnum.TOP_UP, null, null);

        assertEquals(1, result.size());
        assertEquals(PaymentTypeEnum.TOP_UP, result.get(0).getPaymentType());
        verify(paymentRepository).findByUserId(userId);
    }

    @Test
    void getFilteredTransactionHistory_shouldFilterByPaymentTypeAndDateRange() {
        UUID userId = UUID.randomUUID();
        LocalDateTime startDateTime = LocalDateTime.now().minusDays(5);
        LocalDateTime endDateTime = LocalDateTime.now();

        List<Payment> allUserPayments = new ArrayList<>();
        // Included (TOP_UP and within date range)
        allUserPayments.add(createPayment(1L, 100000.0, LocalDateTime.now().minusDays(2),
                PaymentTypeEnum.TOP_UP, "Top up", PaymentStatusEnum.SUCCESS, userId, null, null));
        // Excluded (KOST_PAYMENT)
        allUserPayments.add(createPayment(2L, 200000.0, LocalDateTime.now().minusDays(1),
                PaymentTypeEnum.KOST_PAYMENT, "Kost payment", PaymentStatusEnum.SUCCESS, userId, UUID.randomUUID(), UUID.randomUUID()));
        // Excluded (outside date range)
        allUserPayments.add(createPayment(3L, 150000.0, LocalDateTime.now().minusDays(10),
                PaymentTypeEnum.TOP_UP, "Old top up", PaymentStatusEnum.SUCCESS, userId, null, null));

        when(paymentRepository.findByUserId(userId)).thenReturn(allUserPayments);

        List<Payment> result = paymentService.getFilteredTransactionHistory(
                userId, PaymentTypeEnum.TOP_UP, startDateTime, endDateTime);

        assertEquals(1, result.size());
        assertEquals(PaymentTypeEnum.TOP_UP, result.get(0).getPaymentType());
        LocalDateTime paymentDateTime = result.get(0).getTransactionDateTime();
        assertTrue(!paymentDateTime.isBefore(startDateTime) && !paymentDateTime.isAfter(endDateTime));
        verify(paymentRepository).findByUserId(userId);
    }

    @Test
    void getOwnerTransactionHistory_shouldReturnOwnerTransactions() {
        UUID ownerId = UUID.randomUUID();
        List<Payment> ownerPayments = new ArrayList<>();
        ownerPayments.add(createPayment(1L, 200000.0, LocalDateTime.now().minusDays(2),
                PaymentTypeEnum.KOST_PAYMENT, "Kost payment from user 1", PaymentStatusEnum.SUCCESS,
                UUID.randomUUID(), ownerId, UUID.randomUUID()));
        ownerPayments.add(createPayment(2L, 300000.0, LocalDateTime.now().minusDays(1),
                PaymentTypeEnum.KOST_PAYMENT, "Kost payment from user 3", PaymentStatusEnum.SUCCESS,
                UUID.randomUUID(), ownerId, UUID.randomUUID()));

        when(paymentRepository.findByOwnerId(ownerId)).thenReturn(ownerPayments);

        List<Payment> result = paymentService.getOwnerTransactionHistory(ownerId);

        assertEquals(2, result.size());
        verify(paymentRepository).findByOwnerId(ownerId);
    }

    @Test
    void getFilteredOwnerTransactionHistory_shouldFilterByDateRange() {
        UUID ownerId = UUID.randomUUID();
        LocalDateTime startDateTime = LocalDateTime.now().minusDays(3);
        LocalDateTime endDateTime = LocalDateTime.now();

        List<Payment> allOwnerPayments = new ArrayList<>();
        allOwnerPayments.add(createPayment(1L, 200000.0, LocalDateTime.now().minusDays(2),
                PaymentTypeEnum.KOST_PAYMENT, "Kost payment", PaymentStatusEnum.SUCCESS,
                UUID.randomUUID(), ownerId, UUID.randomUUID()));
        allOwnerPayments.add(createPayment(2L, 300000.0, LocalDateTime.now().minusDays(5),
                PaymentTypeEnum.KOST_PAYMENT, "Old kost payment", PaymentStatusEnum.SUCCESS,
                UUID.randomUUID(), ownerId, UUID.randomUUID()));

        when(paymentRepository.findByOwnerId(ownerId)).thenReturn(allOwnerPayments);

        List<Payment> result = paymentService.getFilteredOwnerTransactionHistory(
                ownerId, null, startDateTime, endDateTime);

        assertEquals(1, result.size());
        verify(paymentRepository).findByOwnerId(ownerId);
    }

    private Payment createPayment(Long id, Double amount, LocalDateTime transactionDateTime, PaymentTypeEnum paymentType,
                                  String description, PaymentStatusEnum paymentStatus,
                                  UUID userId, UUID ownerId, UUID kostId) {
        Payment payment = new PaymentBuilder()
                .amount(amount)
                .transactionDateTime(transactionDateTime)
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