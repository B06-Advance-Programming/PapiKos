package id.cs.ui.advprog.inthecost.repository;

import id.cs.ui.advprog.inthecost.enums.PaymentTypeEnum;
import id.cs.ui.advprog.inthecost.model.Payment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findById(Long id);

    List<Payment> findAll();

    void delete(Payment payment);

    List<Payment> findByUserId(UUID userId);

    List<Payment> findByOwnerId(UUID ownerId);

    List<Payment> findByPaymentType(PaymentTypeEnum paymentType);

    List<Payment> findByDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
}