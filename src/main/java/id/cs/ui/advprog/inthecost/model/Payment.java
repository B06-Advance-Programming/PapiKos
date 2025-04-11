package id.cs.ui.advprog.inthecost.model;

import id.cs.ui.advprog.inthecost.enums.PaymentStatusEnum;
import id.cs.ui.advprog.inthecost.enums.PaymentTypeEnum;
import java.time.LocalDate;

public class Payment {
    private Long id;
    private Double amount;
    private LocalDate date;
    private PaymentTypeEnum paymentType;
    private String description;
    private PaymentStatusEnum paymentStatus;
    private Long userId;
    private Long ownerId;
    private Long kostId;

    // No-args constructor
    public Payment() {
    }

    // Private constructor used by the builder
    private Payment(PaymentBuilder builder) {
        this.id = builder.id;
        this.amount = builder.amount;
        this.date = builder.date;
        this.paymentType = builder.paymentType;
        this.description = builder.description;
        this.paymentStatus = builder.paymentStatus;
        this.userId = builder.userId;
        this.ownerId = builder.ownerId;
        this.kostId = builder.kostId;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public PaymentTypeEnum getPaymentType() {
        return paymentType;
    }

    public String getDescription() {
        return description;
    }

    public PaymentStatusEnum getPaymentStatus() {
        return paymentStatus;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public Long getKostId() {
        return kostId;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setPaymentType(PaymentTypeEnum paymentType) {
        this.paymentType = paymentType;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPaymentStatus(PaymentStatusEnum paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public void setKostId(Long kostId) {
        this.kostId = kostId;
    }

    // Builder class
    public static class PaymentBuilder {
        private Long id;
        private Double amount;
        private LocalDate date;
        private PaymentTypeEnum paymentType;
        private String description;
        private PaymentStatusEnum paymentStatus;
        private Long userId;
        private Long ownerId;
        private Long kostId;

        public PaymentBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public PaymentBuilder amount(Double amount) {
            this.amount = amount;
            return this;
        }

        public PaymentBuilder date(LocalDate date) {
            this.date = date;
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

        public PaymentBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public PaymentBuilder ownerId(Long ownerId) {
            this.ownerId = ownerId;
            return this;
        }

        public PaymentBuilder kostId(Long kostId) {
            this.kostId = kostId;
            return this;
        }

        public Payment build() {
            return new Payment(this);
        }
    }
}