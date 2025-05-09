package id.cs.ui.advprog.inthecost.repository;

import id.cs.ui.advprog.inthecost.enums.PaymentTypeEnum;
import id.cs.ui.advprog.inthecost.model.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("prod")  // Active only in production
public class PaymentRepositoryImplDb implements PaymentRepository {

    private final SpringDataPaymentJpaRepository jpaRepository;

    @Autowired
    public PaymentRepositoryImplDb(SpringDataPaymentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Payment save(Payment payment) {
        return jpaRepository.save(payment);
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Payment> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void delete(Payment payment) {
        jpaRepository.delete(payment);
    }

    @Override
    public List<Payment> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId);
    }

    @Override
    public List<Payment> findByOwnerId(UUID ownerId) {
        return jpaRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<Payment> findByPaymentType(PaymentTypeEnum paymentType) {
        return jpaRepository.findByPaymentType(paymentType);
    }

    @Override
    public List<Payment> findByDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return jpaRepository.findByTransactionDateTimeBetween(startDateTime, endDateTime);
    }
}