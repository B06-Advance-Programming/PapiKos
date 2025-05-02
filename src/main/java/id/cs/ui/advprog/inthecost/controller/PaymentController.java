package id.cs.ui.advprog.inthecost.controller;

import id.cs.ui.advprog.inthecost.enums.PaymentTypeEnum;
import id.cs.ui.advprog.inthecost.model.Payment;
import id.cs.ui.advprog.inthecost.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/topup")
    public ResponseEntity<Payment> topUp(@RequestBody TopUpRequest req) {
        // masih template
        return null;
    }

    @PostMapping("/kost")
    public ResponseEntity<Payment> kostPayment(@RequestBody KostPaymentRequest req) {
        // masih template
        return null;
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<Payment>> getTransactionHistory(@PathVariable Long userId) {
        return null;
    }

    @GetMapping("/history/{userId}/filter")
    public ResponseEntity<List<Payment>> getFilteredTransactionHistory(
            @PathVariable Long userId,
            @RequestParam(required = false) PaymentTypeEnum paymentType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return null;
    }

    public static class TopUpRequest {
        private Long userId;
        private Double amount;
        private String description;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class KostPaymentRequest {
        private Long userId;
        private Long ownerId;
        private Long kostId;
        private Double amount;
        private String description;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public Long getOwnerId() { return ownerId; }
        public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

        public Long getKostId() { return kostId; }
        public void setKostId(Long kostId) { this.kostId = kostId; }

        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}