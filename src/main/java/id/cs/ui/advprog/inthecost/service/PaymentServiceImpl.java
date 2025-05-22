package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.builder.PaymentBuilder;
import id.cs.ui.advprog.inthecost.enums.KuponStatus;
import id.cs.ui.advprog.inthecost.enums.PaymentStatusEnum;
import id.cs.ui.advprog.inthecost.enums.PaymentTypeEnum;
import id.cs.ui.advprog.inthecost.enums.StatusPenyewaan;
import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.Kupon;
import id.cs.ui.advprog.inthecost.model.PenyewaanKos;
import id.cs.ui.advprog.inthecost.model.Payment;
import id.cs.ui.advprog.inthecost.model.User;

import id.cs.ui.advprog.inthecost.repository.KostRepository;
import id.cs.ui.advprog.inthecost.repository.KuponRepository;
import id.cs.ui.advprog.inthecost.repository.PenyewaanKosRepository;
import id.cs.ui.advprog.inthecost.repository.PaymentRepository;
import id.cs.ui.advprog.inthecost.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final KuponRepository kuponRepository;
    private final PenyewaanKosRepository penyewaanKosRepository;
    private final KostRepository kostRepository;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              UserRepository userRepository,
                              KuponRepository kuponRepository,
                              PenyewaanKosRepository penyewaanKosRepository,
                              KostRepository kostRepository) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.kuponRepository = kuponRepository;
        this.penyewaanKosRepository = penyewaanKosRepository;
        this.kostRepository = kostRepository;
    }

    @Override
    @Transactional
    public Payment recordTopUpPayment(UUID userId, Double amount, String description) {
        Payment payment = new PaymentBuilder()
                .amount(amount)
                .transactionDateTime(LocalDateTime.now())
                .paymentType(PaymentTypeEnum.TOP_UP)
                .description(description)
                .paymentStatus(PaymentStatusEnum.SUCCESS)
                .userId(userId)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        if (savedPayment.getPaymentStatus() == PaymentStatusEnum.SUCCESS) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

            user.setBalance(user.getBalance() + amount);
            userRepository.save(user);
        }

        return savedPayment;
    }

    private boolean hasPendingPenyewaan(UUID userId, UUID kostId) {
        List<PenyewaanKos> pending = penyewaanKosRepository.findByKos_KostIDAndUserIdAndStatus(kostId, userId, StatusPenyewaan.DIAJUKAN);
        return !pending.isEmpty();
    }

    @Override
    @Transactional
    public Payment recordKostPayment(UUID userId, UUID ownerId, UUID kostId, Double amount, String description) {
        if (!hasPendingPenyewaan(userId, kostId)) {
            throw new RuntimeException("Tidak ada penyewaan kos dengan status DIAJUKAN untuk user ini");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        if (user.getBalance() < amount) {
            Payment payment = new PaymentBuilder()
                    .amount(amount)
                    .transactionDateTime(LocalDateTime.now())
                    .paymentType(PaymentTypeEnum.KOST_PAYMENT)
                    .description(description)
                    .paymentStatus(PaymentStatusEnum.FAILED)
                    .userId(userId)
                    .ownerId(ownerId)
                    .kostId(kostId)
                    .build();

            return paymentRepository.save(payment);
        }

        Payment payment = new PaymentBuilder()
                .amount(amount)
                .transactionDateTime(LocalDateTime.now())
                .paymentType(PaymentTypeEnum.KOST_PAYMENT)
                .description(description)
                .paymentStatus(PaymentStatusEnum.SUCCESS)
                .userId(userId)
                .ownerId(ownerId)
                .kostId(kostId)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        user.setBalance(user.getBalance() - amount);
        userRepository.save(user);

        // Increase owner's balance by payment amount
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found with ID: " + ownerId));
        owner.setBalance(owner.getBalance() + amount);
        userRepository.save(owner);

        // Update PenyewaanKos status to DISETUJUI
        List<PenyewaanKos> penyewaanList = penyewaanKosRepository.findByKos_KostIDAndUserIdAndStatus(kostId, userId, StatusPenyewaan.DIAJUKAN);
        if (!penyewaanList.isEmpty()) {
            PenyewaanKos penyewaan = penyewaanList.get(0);
            penyewaan.setStatus(StatusPenyewaan.DISETUJUI);
            penyewaanKosRepository.save(penyewaan);
        }

        return savedPayment;
    }

    @Override
    @Transactional
    public Payment processKostPaymentWithKupon(UUID userId, UUID ownerId, UUID kostId, Double originalAmount, String kuponCode) {
        if (!hasPendingPenyewaan(userId, kostId)) {
            throw new RuntimeException("Tidak ada penyewaan kos dengan status DIAJUKAN untuk user ini");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Kupon kupon = kuponRepository.findByKodeUnik(kuponCode)
                .orElseThrow(() -> new RuntimeException("Kupon dengan kode " + kuponCode + " tidak ditemukan"));

        kupon.refreshStatus();

        if (kupon.getStatusKupon() == KuponStatus.INVALID) {
            throw new RuntimeException("Kupon tidak aktif");
        }

        if (LocalDate.now().isAfter(kupon.getMasaBerlaku())) {
            throw new RuntimeException("Kupon sudah kadaluarsa");
        }

        if (kupon.getQuantity() <= 0) {
            throw new RuntimeException("Kupon habis");
        }

        double discount = (kupon.getPersentase() / 100.0) * originalAmount;
        double discountedAmount = originalAmount - discount;
        if (discountedAmount < 0) discountedAmount = 0;

        if (user.getBalance() < discountedAmount) {
            Payment failedPayment = new PaymentBuilder()
                    .amount(discountedAmount)
                    .transactionDateTime(LocalDateTime.now())
                    .paymentType(PaymentTypeEnum.KOST_PAYMENT)
                    .description("Pembayaran gagal: saldo tidak cukup setelah diskon")
                    .paymentStatus(PaymentStatusEnum.FAILED)
                    .userId(userId)
                    .ownerId(ownerId)
                    .kostId(kostId)
                    .build();

            return paymentRepository.save(failedPayment);
        }

        Payment payment = new PaymentBuilder()
                .amount(discountedAmount)
                .transactionDateTime(LocalDateTime.now())
                .paymentType(PaymentTypeEnum.KOST_PAYMENT)
                .description("Pembayaran dengan kupon kode: " + kuponCode)
                .paymentStatus(PaymentStatusEnum.SUCCESS)
                .userId(userId)
                .ownerId(ownerId)
                .kostId(kostId)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        user.setBalance(user.getBalance() - discountedAmount);
        userRepository.save(user);

        // Increase owner's balance by discounted amount
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found with ID: " + ownerId));
        owner.setBalance(owner.getBalance() + discountedAmount);
        userRepository.save(owner);

        kupon.decreaseQuantityByOne();
        kuponRepository.save(kupon);

        // Update PenyewaanKos status to DISETUJUI
        List<PenyewaanKos> penyewaanList = penyewaanKosRepository.findByKos_KostIDAndUserIdAndStatus(kostId, userId, StatusPenyewaan.DIAJUKAN);
        if (!penyewaanList.isEmpty()) {
            PenyewaanKos penyewaan = penyewaanList.get(0);
            penyewaan.setStatus(StatusPenyewaan.DISETUJUI);
            penyewaanKosRepository.save(penyewaan);
        }

        return savedPayment;
    }

    @Override
    public double getKostPrice(UUID kostId) {
        Kost kost = kostRepository.findById(kostId)
                .orElseThrow(() -> new RuntimeException("Kost with ID " + kostId + " not found"));
        return (double) kost.getHargaPerBulan();
    }

    @Override
    public List<Payment> getTransactionHistory(UUID userId) {
        return paymentRepository.findByUserId(userId);
    }

    @Override
    public List<Payment> getFilteredTransactionHistory(UUID userId, PaymentTypeEnum paymentType, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Payment> userTransactions = paymentRepository.findByUserId(userId);
        return filterPayments(userTransactions, paymentType, startDateTime, endDateTime);
    }

    @Override
    public List<Payment> getOwnerTransactionHistory(UUID ownerId) {
        return paymentRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<Payment> getFilteredOwnerTransactionHistory(UUID ownerId, PaymentTypeEnum paymentType, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Payment> ownerTransactions = paymentRepository.findByOwnerId(ownerId);
        return filterPayments(ownerTransactions, paymentType, startDateTime, endDateTime);
    }

    private List<Payment> filterPayments(List<Payment> payments, PaymentTypeEnum paymentType, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Predicate<Payment> typeFilter = payment -> paymentType == null || payment.getPaymentType() == paymentType;
        Predicate<Payment> dateFilter = payment -> {
            LocalDateTime dt = payment.getTransactionDateTime();
            boolean afterOrEqualStart = startDateTime == null || !dt.isBefore(startDateTime);
            boolean beforeOrEqualEnd = endDateTime == null || !dt.isAfter(endDateTime);
            return afterOrEqualStart && beforeOrEqualEnd;
        };
        return payments.stream()
                .filter(typeFilter)
                .filter(dateFilter)
                .collect(Collectors.toList());
    }
}