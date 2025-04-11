package id.cs.ui.advprog.inthecost.repository;

import id.cs.ui.advprog.inthecost.enums.PaymentTypeEnum;
import id.cs.ui.advprog.inthecost.model.Payment;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findById(Long id);

    List<Payment> findAll();

    void delete(Payment payment);

    List<Payment> findByUserId(Long userId);

    List<Payment> findByOwnerId(Long ownerId);

    List<Payment> findByPaymentType(PaymentTypeEnum paymentType);

    List<Payment> findByDateBetween(LocalDate startDate, LocalDate endDate);
}