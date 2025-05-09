package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.enums.PaymentStatusEnum;
import id.cs.ui.advprog.inthecost.enums.PaymentTypeEnum;
import id.cs.ui.advprog.inthecost.model.Payment;
import id.cs.ui.advprog.inthecost.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        Payment payment = Payment.builder()
                .amount(amount)
                .transactionDateTime(LocalDateTime.now())
                .paymentType(PaymentTypeEnum.TOP_UP)
                .description(description)
                .paymentStatus(PaymentStatusEnum.SUCCESS)
                .userId(userId)
                .build();

        return paymentRepository.save(payment);
    }

    @Override
    public Payment recordKostPayment(UUID userId, UUID ownerId, UUID kostId, Double amount, String description) {
        Payment payment = Payment.builder()
                .amount(amount)
                .transactionDateTime(LocalDateTime.now())
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
                                                       LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Payment> userTransactions = paymentRepository.findByUserId(userId);
        return filterPayments(userTransactions, paymentType, startDateTime, endDateTime);
    }

    @Override
    public List<Payment> getOwnerTransactionHistory(UUID ownerId) {
        return paymentRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<Payment> getFilteredOwnerTransactionHistory(UUID ownerId, PaymentTypeEnum paymentType,
                                                            LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Payment> ownerTransactions = paymentRepository.findByOwnerId(ownerId);
        return filterPayments(ownerTransactions, paymentType, startDateTime, endDateTime);
    }

    private List<Payment> filterPayments(List<Payment> payments,
                                         PaymentTypeEnum paymentType,
                                         LocalDateTime startDateTime,
                                         LocalDateTime endDateTime) {
        Predicate<Payment> typeFilter = payment ->
                paymentType == null || payment.getPaymentType() == paymentType;

        Predicate<Payment> dateFilter = payment -> {
            LocalDateTime dt = payment.getTransactionDateTime();
            boolean afterOrEqualStart = (startDateTime == null) || !dt.isBefore(startDateTime);
            boolean beforeOrEqualEnd = (endDateTime == null) || !dt.isAfter(endDateTime);
            return afterOrEqualStart && beforeOrEqualEnd;
        };

        return payments.stream()
                .filter(typeFilter)
                .filter(dateFilter)
                .collect(Collectors.toList());
    }
}