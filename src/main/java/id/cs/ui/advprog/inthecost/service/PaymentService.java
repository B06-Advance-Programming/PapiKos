package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.enums.PaymentTypeEnum;
import id.cs.ui.advprog.inthecost.model.Payment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PaymentService {

    CompletableFuture<Payment> recordTopUpPayment(UUID userId, Double amount, String description);

    CompletableFuture<Payment> recordKostPayment(UUID userId, UUID ownerId, UUID kostId, Double amount, String description);

    CompletableFuture<List<Payment>> getTransactionHistory(UUID userId);

    CompletableFuture<List<Payment>> getFilteredTransactionHistory(UUID userId, PaymentTypeEnum paymentType,
                                                                   LocalDateTime startDateTime, LocalDateTime endDateTime);

    CompletableFuture<List<Payment>> getOwnerTransactionHistory(UUID ownerId);

    CompletableFuture<List<Payment>> getFilteredOwnerTransactionHistory(UUID ownerId, PaymentTypeEnum paymentType,
                                                                        LocalDateTime startDateTime, LocalDateTime endDateTime);

    CompletableFuture<Payment> processKostPaymentWithKupon(UUID userId, UUID ownerId, UUID kostId, Double originalAmount, String kuponCode);

    double getKostPrice(UUID kostId);

}