package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.enums.PaymentStatusEnum;
import id.cs.ui.advprog.inthecost.enums.PaymentTypeEnum;
import id.cs.ui.advprog.inthecost.model.Payment;
import id.cs.ui.advprog.inthecost.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Payment recordTopUpPayment(UUID userId, Double amount, String description) {
        // Create payment record for top-up
        Payment payment = Payment.builder()
                .amount(amount)
                .date(LocalDate.now())
                .paymentType(PaymentTypeEnum.TOP_UP)
                .description(description)
                .paymentStatus(PaymentStatusEnum.SUCCESS)
                .userId(userId)
                .build();

        return paymentRepository.save(payment);
    }

    @Override
    public Payment recordKostPayment(UUID userId, UUID ownerId, UUID kostId, Double amount, String description) {
        // Create payment record for kost payment
        Payment payment = Payment.builder()
                .amount(amount)
                .date(LocalDate.now())
                .paymentType(PaymentTypeEnum.KOST_PAYMENT)
                .description(description)
                .paymentStatus(PaymentStatusEnum.SUCCESS)
                .userId(userId)
                .ownerId(ownerId)
                .kostId(kostId)
                .build();

        return paymentRepository.save(payment);
    }

    @Override
    public List<Payment> getTransactionHistory(UUID userId) {
        return paymentRepository.findByUserId(userId);
    }

    @Override
    public List<Payment> getFilteredTransactionHistory(UUID userId, PaymentTypeEnum paymentType,
                                                       LocalDate startDate, LocalDate endDate) {
        // Get all user transactions first
        List<Payment> userTransactions = paymentRepository.findByUserId(userId);

        // Apply filters using extracted filter methods
        return filterPayments(userTransactions, paymentType, startDate, endDate);
    }

    @Override
    public List<Payment> getOwnerTransactionHistory(UUID ownerId) {
        return paymentRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<Payment> getFilteredOwnerTransactionHistory(UUID ownerId, PaymentTypeEnum paymentType,
                                                            LocalDate startDate, LocalDate endDate) {
        // Get all owner transactions first
        List<Payment> ownerTransactions = paymentRepository.findByOwnerId(ownerId);

        // Apply filters using extracted filter methods
        return filterPayments(ownerTransactions, paymentType, startDate, endDate);
    }

    private List<Payment> filterPayments(List<Payment> payments,
                                         PaymentTypeEnum paymentType,
                                         LocalDate startDate,
                                         LocalDate endDate) {
        Predicate<Payment> typeFilter = payment ->
                paymentType == null || payment.getPaymentType() == paymentType;

        Predicate<Payment> dateFilter = payment ->
                (startDate == null && endDate == null) ||
                        (payment.getDate() != null &&
                                !payment.getDate().isBefore(startDate) &&
                                !payment.getDate().isAfter(endDate));

        return payments.stream()
                .filter(typeFilter)
                .filter(dateFilter)
                .collect(Collectors.toList());
    }
}