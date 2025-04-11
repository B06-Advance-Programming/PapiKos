package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.enums.PaymentStatusEnum;
import id.cs.ui.advprog.inthecost.enums.PaymentTypeEnum;
import id.cs.ui.advprog.inthecost.model.Payment;
import id.cs.ui.advprog.inthecost.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
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
    public Payment recordTopUpPayment(Long userId, Double amount, String description) {
        // Create payment record for top-up
        Payment payment = new Payment.PaymentBuilder()
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
    public Payment recordKostPayment(Long userId, Long ownerId, Long kostId, Double amount, String description) {
        // Create payment record for kost payment
        Payment payment = new Payment.PaymentBuilder()
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
    public List<Payment> getTransactionHistory(Long userId) {
        return paymentRepository.findByUserId(userId);
    }

    @Override
    public List<Payment> getFilteredTransactionHistory(Long userId, PaymentTypeEnum paymentType,
                                                       LocalDate startDate, LocalDate endDate) {
        // Get all user transactions first
        List<Payment> userTransactions = paymentRepository.findByUserId(userId);

        // Apply filters using extracted filter methods
        return filterPayments(userTransactions, paymentType, startDate, endDate);
    }

    @Override
    public List<Payment> getOwnerTransactionHistory(Long ownerId) {
        return paymentRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<Payment> getFilteredOwnerTransactionHistory(Long ownerId, PaymentTypeEnum paymentType,
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
        // Create predicates for type and date filtering
        Predicate<Payment> typeFilter = payment ->
                paymentType == null || payment.getPaymentType() == paymentType;

        Predicate<Payment> dateFilter = payment ->
                (startDate == null && endDate == null) ||
                        (payment.getDate() != null &&
                                !payment.getDate().isBefore(startDate) &&
                                !payment.getDate().isAfter(endDate));

        // Apply all filters in a chain
        return payments.stream()
                .filter(typeFilter)
                .filter(dateFilter)
                .collect(Collectors.toList());
    }
}