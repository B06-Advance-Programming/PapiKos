package id.cs.ui.advprog.inthecost.builder;

import id.cs.ui.advprog.inthecost.enums.PaymentStatusEnum;
import id.cs.ui.advprog.inthecost.enums.PaymentTypeEnum;
import id.cs.ui.advprog.inthecost.model.Payment;

import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentBuilder {

    private Long id;
    private Double amount;
    private LocalDateTime transactionDateTime;
    private PaymentTypeEnum paymentType;
    private String description;
    private PaymentStatusEnum paymentStatus;
    private UUID userId;
    private UUID ownerId;
    private UUID kostId;

    public static PaymentBuilder builder() {
        return new PaymentBuilder();
    }

    public PaymentBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public PaymentBuilder amount(Double amount) {
        this.amount = amount;
        return this;
    }

    public PaymentBuilder transactionDateTime(LocalDateTime transactionDateTime) {
        this.transactionDateTime = transactionDateTime;
        return this;
    }

    public PaymentBuilder paymentType(PaymentTypeEnum paymentType) {
        this.paymentType = paymentType;
        return this;
    }

    public PaymentBuilder description(String description) {
        this.description = description;
        return this;
    }

    public PaymentBuilder paymentStatus(PaymentStatusEnum paymentStatus) {
        this.paymentStatus = paymentStatus;
        return this;
    }

    public PaymentBuilder userId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public PaymentBuilder ownerId(UUID ownerId) {
        this.ownerId = ownerId;
        return this;
    }

    public PaymentBuilder kostId(UUID kostId) {
        this.kostId = kostId;
        return this;
    }

    public Payment build() {
        Payment payment = new Payment();
        payment.setId(id);
        payment.setAmount(amount);
        payment.setTransactionDateTime(transactionDateTime);
        payment.setPaymentType(paymentType);
        payment.setDescription(description);
        payment.setPaymentStatus(paymentStatus);
        payment.setUserId(userId);
        payment.setOwnerId(ownerId);
        payment.setKostId(kostId);
        return payment;
    }
}