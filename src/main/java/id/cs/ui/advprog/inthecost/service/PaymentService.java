package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.enums.PaymentTypeEnum;
import id.cs.ui.advprog.inthecost.model.Payment;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PaymentService {

    Payment recordTopUpPayment(UUID userId, Double amount, String description);

    Payment recordKostPayment(UUID userId, UUID ownerId, UUID kostId, Double amount, String description);

    List<Payment> getTransactionHistory(UUID userId);

    List<Payment> getFilteredTransactionHistory(UUID userId, PaymentTypeEnum paymentType,
                                                LocalDate startDate, LocalDate endDate);

    List<Payment> getOwnerTransactionHistory(UUID ownerId);

    List<Payment> getFilteredOwnerTransactionHistory(UUID ownerId, PaymentTypeEnum paymentType,
                                                     LocalDate startDate, LocalDate endDate);
}