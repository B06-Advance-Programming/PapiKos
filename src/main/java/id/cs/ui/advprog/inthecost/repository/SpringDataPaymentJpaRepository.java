package id.cs.ui.advprog.inthecost.repository;

import id.cs.ui.advprog.inthecost.enums.PaymentTypeEnum;
import id.cs.ui.advprog.inthecost.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SpringDataPaymentJpaRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserId(UUID userId);
    List<Payment> findByOwnerId(UUID ownerId);
    List<Payment> findByPaymentType(PaymentTypeEnum paymentType);
    List<Payment> findByTransactionDateTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
}