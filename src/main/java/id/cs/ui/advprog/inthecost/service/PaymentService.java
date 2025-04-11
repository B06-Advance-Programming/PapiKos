package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.enums.PaymentTypeEnum;
import id.cs.ui.advprog.inthecost.model.Payment;

import java.time.LocalDate;
import java.util.List;

public interface PaymentService {

    Payment recordTopUpPayment(Long userId, Double amount, String description);

    Payment recordKostPayment(Long userId, Long ownerId, Long kostId, Double amount, String description);

    List<Payment> getTransactionHistory(Long userId);

    List<Payment> getFilteredTransactionHistory(Long userId, PaymentTypeEnum paymentType,
                                                LocalDate startDate, LocalDate endDate);

    List<Payment> getOwnerTransactionHistory(Long ownerId);
    
    List<Payment> getFilteredOwnerTransactionHistory(Long ownerId, PaymentTypeEnum paymentType,
                                                     LocalDate startDate, LocalDate endDate);
}