package id.cs.ui.advprog.inthecost.repository;

import id.cs.ui.advprog.inthecost.enums.PaymentTypeEnum;
import id.cs.ui.advprog.inthecost.model.Payment;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class PaymentRepositoryImpl implements PaymentRepository {

    private final Map<Long, Payment> payments = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public Payment save(Payment payment) {
        if (payment.getId() == null) {
            // New payment, generate ID
            payment.setId(idCounter.getAndIncrement());
        }
        payments.put(payment.getId(), payment);
        return payment;
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return Optional.ofNullable(payments.get(id));
    }

    @Override
    public List<Payment> findAll() {
        return new ArrayList<>(payments.values());
    }

    @Override
    public void delete(Payment payment) {
        payments.remove(payment.getId());
    }

    @Override
    public List<Payment> findByUserId(Long userId) {
        return payments.values().stream()
                .filter(payment -> Objects.equals(payment.getUserId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByOwnerId(Long ownerId) {
        return payments.values().stream()
                .filter(payment -> Objects.equals(payment.getOwnerId(), ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByPaymentType(PaymentTypeEnum paymentType) {
        return payments.values().stream()
                .filter(payment -> payment.getPaymentType() == paymentType)
                .collect(Collectors.toList());
    }

    @Override
    public List<Payment> findByDateBetween(LocalDate startDate, LocalDate endDate) {
        return payments.values().stream()
                .filter(payment -> {
                    LocalDate paymentDate = payment.getDate();
                    return !paymentDate.isBefore(startDate) && !paymentDate.isAfter(endDate);
                })
                .collect(Collectors.toList());
    }
}