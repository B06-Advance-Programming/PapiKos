package id.cs.ui.advprog.inthecost.repository;

import id.cs.ui.advprog.inthecost.enums.PaymentTypeEnum;
import id.cs.ui.advprog.inthecost.enums.PaymentStatusEnum;
import id.cs.ui.advprog.inthecost.model.Payment;
import id.cs.ui.advprog.inthecost.builder.PaymentBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentRepositoryImplDbTest {

    private SpringDataPaymentJpaRepository jpaRepository;
    private PaymentRepositoryImplDb paymentRepository;

    @BeforeEach
    void setUp() {
        jpaRepository = mock(SpringDataPaymentJpaRepository.class);
        paymentRepository = new PaymentRepositoryImplDb(jpaRepository);
    }

    private Payment samplePayment() {
        return new PaymentBuilder()
                .amount(12345.0)
                .transactionDateTime(LocalDateTime.now())
                .paymentType(PaymentTypeEnum.TOP_UP)
                .description("desc")
                .paymentStatus(PaymentStatusEnum.SUCCESS)
                .userId(UUID.randomUUID())
                .ownerId(UUID.randomUUID())
                .kostId(UUID.randomUUID())
                .build();
    }

    @Test
    void save_shouldCallRepositoryAndReturnSavedPayment() {
        Payment payment = samplePayment();
        when(jpaRepository.save(payment)).thenReturn(payment);

        Payment result = paymentRepository.save(payment);

        verify(jpaRepository, times(1)).save(payment);
        assertSame(payment, result);
    }

    @Test
    void findById_shouldCallRepositoryAndReturnPaymentIfExists() {
        Payment payment = samplePayment();
        when(jpaRepository.findById(10L)).thenReturn(Optional.of(payment));

        Optional<Payment> result = paymentRepository.findById(10L);

        verify(jpaRepository).findById(10L);
        assertTrue(result.isPresent());
        assertSame(payment, result.get());
    }

    @Test
    void findById_shouldReturnEmptyIfNotFound() {
        when(jpaRepository.findById(22L)).thenReturn(Optional.empty());

        Optional<Payment> result = paymentRepository.findById(22L);

        assertFalse(result.isPresent());
    }

    @Test
    void findAll_shouldDelegateAndReturnAllPayments() {
        List<Payment> payments = Arrays.asList(samplePayment(), samplePayment());
        when(jpaRepository.findAll()).thenReturn(payments);

        List<Payment> result = paymentRepository.findAll();

        verify(jpaRepository).findAll();
        assertEquals(2, result.size());
        assertSame(payments, result);
    }

    @Test
    void delete_shouldCallRepositoryDelete() {
        Payment payment = samplePayment();

        paymentRepository.delete(payment);

        verify(jpaRepository, times(1)).delete(payment);
    }

    @Test
    void findByUserId_shouldDelegateAndReturnPayments() {
        UUID userId = UUID.randomUUID();
        List<Payment> payments = Arrays.asList(samplePayment());
        when(jpaRepository.findByUserId(userId)).thenReturn(payments);

        List<Payment> result = paymentRepository.findByUserId(userId);

        verify(jpaRepository).findByUserId(userId);
        assertSame(payments, result);
    }

    @Test
    void findByUserId_shouldReturnEmptyListIfNoneFound() {
        UUID userId = UUID.randomUUID();
        when(jpaRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        List<Payment> result = paymentRepository.findByUserId(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByOwnerId_shouldDelegate() {
        UUID ownerId = UUID.randomUUID();
        List<Payment> payments = Arrays.asList(samplePayment());
        when(jpaRepository.findByOwnerId(ownerId)).thenReturn(payments);

        List<Payment> result = paymentRepository.findByOwnerId(ownerId);

        verify(jpaRepository).findByOwnerId(ownerId);
        assertSame(payments, result);
    }

    @Test
    void findByOwnerId_shouldReturnEmptyListIfNoneFound() {
        UUID ownerId = UUID.randomUUID();
        when(jpaRepository.findByOwnerId(ownerId)).thenReturn(Collections.emptyList());

        List<Payment> result = paymentRepository.findByOwnerId(ownerId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByPaymentType_shouldDelegate() {
        PaymentTypeEnum type = PaymentTypeEnum.KOST_PAYMENT;
        List<Payment> payments = Arrays.asList(samplePayment(), samplePayment());
        when(jpaRepository.findByPaymentType(type)).thenReturn(payments);

        List<Payment> result = paymentRepository.findByPaymentType(type);

        verify(jpaRepository).findByPaymentType(type);
        assertEquals(2, result.size());
    }

    @Test
    void findByPaymentType_shouldReturnEmptyIfNotExist() {
        PaymentTypeEnum type = PaymentTypeEnum.KOST_PAYMENT;
        when(jpaRepository.findByPaymentType(type)).thenReturn(Collections.emptyList());

        List<Payment> result = paymentRepository.findByPaymentType(type);

        assertTrue(result.isEmpty());
    }

    @Test
    void findByDateBetween_shouldDelegate() {
        LocalDateTime start = LocalDateTime.now().minusDays(5);
        LocalDateTime end = LocalDateTime.now();
        List<Payment> payments = Arrays.asList(samplePayment());
        when(jpaRepository.findByTransactionDateTimeBetween(start, end)).thenReturn(payments);

        List<Payment> result = paymentRepository.findByDateBetween(start, end);

        verify(jpaRepository).findByTransactionDateTimeBetween(start, end);
        assertSame(payments, result);
    }

    @Test
    void findByDateBetween_shouldReturnEmptyIfNoneInRange() {
        LocalDateTime start = LocalDateTime.now().minusDays(10);
        LocalDateTime end = LocalDateTime.now().minusDays(5);
        when(jpaRepository.findByTransactionDateTimeBetween(start, end)).thenReturn(Collections.emptyList());

        List<Payment> result = paymentRepository.findByDateBetween(start, end);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}