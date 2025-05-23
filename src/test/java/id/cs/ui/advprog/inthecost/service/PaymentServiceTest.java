package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.builder.PaymentBuilder;
import id.cs.ui.advprog.inthecost.enums.KuponStatus;
import id.cs.ui.advprog.inthecost.enums.PaymentStatusEnum;
import id.cs.ui.advprog.inthecost.enums.PaymentTypeEnum;
import id.cs.ui.advprog.inthecost.enums.StatusPenyewaan;
import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.Kupon;
import id.cs.ui.advprog.inthecost.model.Payment;
import id.cs.ui.advprog.inthecost.model.PenyewaanKos;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.repository.KostRepository;
import id.cs.ui.advprog.inthecost.repository.KuponRepository;
import id.cs.ui.advprog.inthecost.repository.PaymentRepository;
import id.cs.ui.advprog.inthecost.repository.PenyewaanKosRepository;
import id.cs.ui.advprog.inthecost.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PenyewaanKosRepository penyewaanKosRepository;

    @Mock
    private KostRepository kostRepository;

    @Mock
    private KuponRepository kuponRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private UUID userId;
    private UUID ownerId;
    private UUID kostId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        kostId = UUID.randomUUID();
    }

    @Test
    void recordTopUpPayment_shouldCreateTopUpPaymentRecord() throws Exception {
        User user = new User();
        user.setId(userId);
        user.setBalance(0.0);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        Double amount = 100000.0;
        String description = "Top up via bank transfer";

        Payment result = paymentService.recordTopUpPayment(userId, amount, description).get();

        assertEquals(PaymentTypeEnum.TOP_UP, result.getPaymentType());
        assertEquals(PaymentStatusEnum.SUCCESS, result.getPaymentStatus());
        assertEquals(amount, result.getAmount());
        assertEquals(description, result.getDescription());
        assertEquals(userId, result.getUserId());
        assertNull(result.getOwnerId());
        assertNull(result.getKostId());
        verify(paymentRepository).save(any(Payment.class));
        verify(userRepository).save(user);
        assertEquals(amount, user.getBalance());
    }

    @Test
    void recordKostPayment_shouldCreateKostPaymentRecord() throws Exception {
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setBalance(500000.0);

        User mockOwner = new User();
        mockOwner.setId(ownerId);
        mockOwner.setBalance(100000.0);

        Kost mockKost = new Kost();
        mockKost.setKostID(kostId);

        PenyewaanKos penyewaanKos = new PenyewaanKos();
        penyewaanKos.setId(UUID.randomUUID());
        penyewaanKos.setKos(mockKost);
        penyewaanKos.setUserId(userId);
        penyewaanKos.setStatus(StatusPenyewaan.DIAJUKAN);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(mockOwner));
        when(penyewaanKosRepository.findByKos_KostIDAndUserIdAndStatus(kostId, userId, StatusPenyewaan.DIAJUKAN))
                .thenReturn(List.of(penyewaanKos));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));
        when(penyewaanKosRepository.save(any(PenyewaanKos.class))).thenAnswer(i -> i.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        Double amount = 300000.0;
        String description = "Kost payment for May";

        Payment payment = paymentService.recordKostPayment(userId, ownerId, kostId, amount, description).get();

        assertNotNull(payment);
        assertEquals(PaymentTypeEnum.KOST_PAYMENT, payment.getPaymentType());
        assertEquals(PaymentStatusEnum.SUCCESS, payment.getPaymentStatus());
        assertEquals(amount, payment.getAmount());
        assertEquals(userId, payment.getUserId());
        assertEquals(ownerId, payment.getOwnerId());
        assertEquals(kostId, payment.getKostId());

        // Verify balance deductions and additions
        assertEquals(200000.0, mockUser.getBalance());
        assertEquals(400000.0, mockOwner.getBalance());

        // Verify penyewaan status updated
        assertEquals(StatusPenyewaan.DISETUJUI, penyewaanKos.getStatus());

        verify(paymentRepository).save(any(Payment.class));
        verify(userRepository, times(2)).save(any(User.class));
        verify(penyewaanKosRepository).save(penyewaanKos);
    }

    @Test
    void recordKostPayment_shouldThrowRuntimeExceptionIfNoPendingPenyewaan() {
        when(penyewaanKosRepository.findByKos_KostIDAndUserIdAndStatus(kostId, userId, StatusPenyewaan.DIAJUKAN))
                .thenReturn(List.of());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            try {
                paymentService.recordKostPayment(userId, ownerId, kostId, 100000.0, "desc").get();
            } catch (ExecutionException e) {
                if (e.getCause() instanceof RuntimeException) {
                    throw (RuntimeException) e.getCause();
                }
                throw e;
            }
        });

        String expectedMessage = "Tidak ada penyewaan kos dengan status DIAJUKAN untuk user ini";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void recordKostPayment_shouldCreateFailedPaymentWhenInsufficientBalance() throws Exception {
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setBalance(100000.0);

        Kost mockKost = new Kost();
        mockKost.setKostID(kostId);

        PenyewaanKos penyewaanKos = new PenyewaanKos();
        penyewaanKos.setId(UUID.randomUUID());
        penyewaanKos.setKos(mockKost);
        penyewaanKos.setUserId(userId);
        penyewaanKos.setStatus(StatusPenyewaan.DIAJUKAN);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(penyewaanKosRepository.findByKos_KostIDAndUserIdAndStatus(kostId, userId, StatusPenyewaan.DIAJUKAN))
                .thenReturn(List.of(penyewaanKos));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        Double amount = 300000.0;
        String description = "Kost payment for May";

        Payment payment = paymentService.recordKostPayment(userId, ownerId, kostId, amount, description).get();

        assertEquals(PaymentStatusEnum.FAILED, payment.getPaymentStatus());
        assertEquals(amount, payment.getAmount());
        assertEquals(userId, payment.getUserId());
        assertEquals(ownerId, payment.getOwnerId());
        assertEquals(kostId, payment.getKostId());

        verify(paymentRepository).save(any(Payment.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void processKostPaymentWithKupon_shouldSucceedWhenValidKuponAndBalance() throws Exception {
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setBalance(1000000.0);

        User mockOwner = new User();
        mockOwner.setId(ownerId);
        mockOwner.setBalance(500000.0);

        Kupon kupon = mock(Kupon.class);
        when(kuponRepository.findByKodeUnik("KU123")).thenReturn(Optional.of(kupon));
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(mockOwner));
        when(penyewaanKosRepository.findByKos_KostIDAndUserIdAndStatus(kostId, userId, StatusPenyewaan.DIAJUKAN))
                .thenReturn(List.of(createPenyewaanKosWithRequestedStatus()));

        doNothing().when(kupon).refreshStatus();
        when(kupon.getStatusKupon()).thenReturn(KuponStatus.VALID);
        when(kupon.getMasaBerlaku()).thenReturn(LocalDate.now().plusDays(1));
        when(kupon.getQuantity()).thenReturn(10);
        when(kupon.getPersentase()).thenReturn(20);

        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));
        when(kuponRepository.save(any(Kupon.class))).thenAnswer(i -> i.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(penyewaanKosRepository.save(any(PenyewaanKos.class))).thenAnswer(i -> i.getArgument(0));

        double originalAmount = 500000.0;
        Payment payment = paymentService.processKostPaymentWithKupon(userId, ownerId, kostId, originalAmount, "KU123").get();

        double expectedDiscountedAmount = originalAmount * 0.8;

        assertEquals(PaymentStatusEnum.SUCCESS, payment.getPaymentStatus());
        assertEquals(expectedDiscountedAmount, payment.getAmount(), 0.001);
        assertEquals(userId, payment.getUserId());
        assertEquals(ownerId, payment.getOwnerId());
        assertEquals(kostId, payment.getKostId());

        assertEquals(1000000.0 - expectedDiscountedAmount, mockUser.getBalance(), 0.001);
        assertEquals(500000.0 + expectedDiscountedAmount, mockOwner.getBalance(), 0.001);

        verify(kupon).decreaseQuantityByOne();
    }

    @Test
    void processKostPaymentWithKupon_shouldFailIfNoPendingPenyewaan() {
        when(penyewaanKosRepository.findByKos_KostIDAndUserIdAndStatus(kostId, userId, StatusPenyewaan.DIAJUKAN))
                .thenReturn(List.of());

        RuntimeException e = assertThrows(RuntimeException.class, () -> {
            try {
                paymentService.processKostPaymentWithKupon(userId, ownerId, kostId, 100000.0, "KU123").get();
            } catch (ExecutionException ex) {
                if (ex.getCause() instanceof RuntimeException) {
                    throw (RuntimeException) ex.getCause();
                }
                throw ex;
            }
        });

        assertEquals("Tidak ada penyewaan kos dengan status DIAJUKAN untuk user ini", e.getMessage());
    }

    @Test
    void processKostPaymentWithKupon_shouldFailIfKuponInvalid() {
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setBalance(1000000.0);

        Kupon kupon = mock(Kupon.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(kuponRepository.findByKodeUnik("KU123")).thenReturn(Optional.of(kupon));
        when(penyewaanKosRepository.findByKos_KostIDAndUserIdAndStatus(kostId,userId, StatusPenyewaan.DIAJUKAN))
                .thenReturn(List.of(createPenyewaanKosWithRequestedStatus()));

        doNothing().when(kupon).refreshStatus();
        when(kupon.getStatusKupon()).thenReturn(KuponStatus.INVALID);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            try {
                paymentService.processKostPaymentWithKupon(userId, ownerId, kostId, 100000.0, "KU123").get();
            } catch (ExecutionException e) {
                if (e.getCause() instanceof RuntimeException) {
                    throw (RuntimeException) e.getCause();
                }
                throw e;
            }
        });

        assertEquals("Kupon tidak aktif", exception.getMessage());
    }

    @Test
    void processKostPaymentWithKupon_shouldFailIfKuponExpired() {
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setBalance(1000000.0);

        Kupon kupon = mock(Kupon.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(kuponRepository.findByKodeUnik("KU123")).thenReturn(Optional.of(kupon));
        when(penyewaanKosRepository.findByKos_KostIDAndUserIdAndStatus(kostId,userId, StatusPenyewaan.DIAJUKAN))
                .thenReturn(List.of(createPenyewaanKosWithRequestedStatus()));

        doNothing().when(kupon).refreshStatus();
        when(kupon.getStatusKupon()).thenReturn(KuponStatus.VALID);
        when(kupon.getMasaBerlaku()).thenReturn(LocalDate.now().minusDays(1));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            try {
                paymentService.processKostPaymentWithKupon(userId, ownerId, kostId, 100000.0, "KU123").get();
            } catch (ExecutionException e) {
                if (e.getCause() instanceof RuntimeException) {
                    throw (RuntimeException) e.getCause();
                }
                throw e;
            }
        });

        assertEquals("Kupon sudah kadaluarsa", exception.getMessage());
    }

    @Test
    void processKostPaymentWithKupon_shouldFailIfKuponQuantityZero() {
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setBalance(1000000.0);

        Kupon kupon = mock(Kupon.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(kuponRepository.findByKodeUnik("KU123")).thenReturn(Optional.of(kupon));
        when(penyewaanKosRepository.findByKos_KostIDAndUserIdAndStatus(kostId,userId, StatusPenyewaan.DIAJUKAN))
                .thenReturn(List.of(createPenyewaanKosWithRequestedStatus()));

        doNothing().when(kupon).refreshStatus();
        when(kupon.getStatusKupon()).thenReturn(KuponStatus.VALID);
        when(kupon.getMasaBerlaku()).thenReturn(LocalDate.now().plusDays(1));
        when(kupon.getQuantity()).thenReturn(0);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            try {
                paymentService.processKostPaymentWithKupon(userId, ownerId, kostId, 100000.0, "KU123").get();
            } catch (ExecutionException e) {
                if (e.getCause() instanceof RuntimeException) {
                    throw (RuntimeException) e.getCause();
                }
                throw e;
            }
        });

        assertEquals("Kupon habis", exception.getMessage());
    }

    @Test
    void processKostPaymentWithKupon_shouldFailIfInsufficientBalanceAfterDiscount() throws Exception {
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setBalance(10000.0);

        Kupon kupon = mock(Kupon.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(kuponRepository.findByKodeUnik("KU123")).thenReturn(Optional.of(kupon));
        when(penyewaanKosRepository.findByKos_KostIDAndUserIdAndStatus(kostId,userId, StatusPenyewaan.DIAJUKAN))
                .thenReturn(List.of(createPenyewaanKosWithRequestedStatus()));

        doNothing().when(kupon).refreshStatus();
        when(kupon.getStatusKupon()).thenReturn(KuponStatus.VALID);
        when(kupon.getMasaBerlaku()).thenReturn(LocalDate.now().plusDays(1));
        when(kupon.getQuantity()).thenReturn(5);
        when(kupon.getPersentase()).thenReturn(50);

        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        double originalAmount = 50000.0;
        Payment failedPayment = paymentService.processKostPaymentWithKupon(userId, ownerId, kostId, originalAmount, "KU123").get();

        assertEquals(PaymentStatusEnum.FAILED, failedPayment.getPaymentStatus());

        verify(userRepository, never()).save(any());
    }

    @Test
    void getKostPrice_shouldReturnKostPrice() {
        Kost kost = new Kost();
        kost.setKostID(kostId);
        kost.setHargaPerBulan(1200000);

        when(kostRepository.findById(kostId)).thenReturn(Optional.of(kost));

        double price = paymentService.getKostPrice(kostId);
        assertEquals(1200000.0, price);
        verify(kostRepository).findById(kostId);
    }

    @Test
    void getKostPrice_shouldThrowExceptionIfKostNotFound() {
        when(kostRepository.findById(kostId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> paymentService.getKostPrice(kostId));
        assertTrue(exception.getMessage().contains("Kost with ID " + kostId + " not found"));
    }

    // Helper method
    private PenyewaanKos createPenyewaanKosWithRequestedStatus() {
        PenyewaanKos penyewaan = new PenyewaanKos();
        penyewaan.setId(UUID.randomUUID());
        penyewaan.setKos(new Kost());
        penyewaan.setUserId(userId);
        penyewaan.setStatus(StatusPenyewaan.DIAJUKAN);
        return penyewaan;
    }
}