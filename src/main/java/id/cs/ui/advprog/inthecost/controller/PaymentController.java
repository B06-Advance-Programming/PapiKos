package id.cs.ui.advprog.inthecost.controller;

import id.cs.ui.advprog.inthecost.enums.PaymentTypeEnum;
import id.cs.ui.advprog.inthecost.model.Payment;
import id.cs.ui.advprog.inthecost.service.PaymentService;
import id.cs.ui.advprog.inthecost.service.PenyewaanKosService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PenyewaanKosService penyewaanKosService;

    @Autowired
    public PaymentController(PaymentService paymentService, PenyewaanKosService penyewaanKosService) {
        this.paymentService = paymentService;
        this.penyewaanKosService = penyewaanKosService;
    }

    // Mock validation of user session
    private boolean validateUserSession(UUID userId) {
        System.out.println("Validate session for userId=" + userId + " (mockup always true)");
        return true;
    }

    // Mock check if kost already paid for user
    private boolean isKostAlreadyPaid(UUID userId, UUID kostId) {
        System.out.println("Checking if kost is already paid userId=" + userId + ", kostId=" + kostId + " (mockup false)");
        return false;
    }

    // Mock user permission to pay for kost
    private boolean isUserAllowedToPayKost(UUID userId, UUID kostId) {
        System.out.println("Validating kost ownership userId=" + userId + ", kostId=" + kostId + " (mockup true)");
        return true;
    }

    @PostMapping("/topup")
    public CompletableFuture<ResponseEntity<?>> topUp(
            @RequestBody TopUpRequest req,
            @RequestHeader(value = "X-Testing-Mode", required = false) String testingMode) {

        logRequestContext(testingMode, "topUp");

        UUID userId;
        try {
            userId = UUID.fromString(req.getUserId());
        } catch (IllegalArgumentException e) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body("Invalid UUID format for userId"));
        }

        if (!validateUserSession(userId)) {
            return CompletableFuture.completedFuture(ResponseEntity.status(401).body("Invalid user session"));
        }

        return paymentService.recordTopUpPayment(userId, req.getAmount(), req.getDescription())
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/kost")
    public CompletableFuture<ResponseEntity<?>> kostPayment(
            @RequestBody KostPaymentRequest req,
            @RequestHeader(value = "X-Testing-Mode", required = false) String testingMode) {

        logRequestContext(testingMode, "kostPayment");

        UUID userId, ownerId, kostId;
        try {
            userId = UUID.fromString(req.getUserId());
            ownerId = UUID.fromString(req.getOwnerId());
            kostId = UUID.fromString(req.getKostId());
        } catch (IllegalArgumentException e) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body("Invalid UUID format for userId, ownerId, or kostId"));
        }

        if (!validateUserSession(userId)) {
            return CompletableFuture.completedFuture(ResponseEntity.status(401).body("Invalid user session"));
        }

        if (!isUserAllowedToPayKost(userId, kostId)) {
            return CompletableFuture.completedFuture(ResponseEntity.status(403).body("User tidak diizinkan membayar kost ini"));
        }

        if (!penyewaanKosService.hasPendingPenyewaan(userId, kostId)) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body("Tidak ada penyewaan kos dengan status DIAJUKAN untuk user ini"));
        }

        if (isKostAlreadyPaid(userId, kostId)) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body("Kost sudah dibayar untuk periode ini"));
        }

        double amount;
        try {
            amount = paymentService.getKostPrice(kostId);
        } catch (RuntimeException ex) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(ex.getMessage()));
        }

        CompletableFuture<Payment> futurePayment;
        if (req.getCouponCode() != null && !req.getCouponCode().isBlank()) {
            futurePayment = paymentService.processKostPaymentWithKupon(userId, ownerId, kostId, amount, req.getCouponCode());
        } else {
            futurePayment = paymentService.recordKostPayment(userId, ownerId, kostId, amount, req.getDescription());
        }

        return futurePayment.handle((payment, ex) -> {
            if (ex != null) {
                return ResponseEntity.badRequest().body(ex.getMessage());
            } else {
                return ResponseEntity.ok(payment);
            }
        });
    }

    @GetMapping("/history/{userId}")
    public CompletableFuture<ResponseEntity<?>> getTransactionHistory(
            @PathVariable String userId,
            @RequestHeader(value = "X-Session-Token", required = false) String sessionToken) {

        UUID uuidUserId;
        try {
            uuidUserId = UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body("Invalid UUID format for userId"));
        }

        if (!validateUserSession(uuidUserId)) {
            return CompletableFuture.completedFuture(ResponseEntity.status(401).body("Session tidak valid atau kedaluwarsa"));
        }

        return paymentService.getTransactionHistory(uuidUserId)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/history/{userId}/filter")
    public CompletableFuture<ResponseEntity<?>> getFilteredTransactionHistory(
            @PathVariable String userId,
            @RequestParam(required = false) PaymentTypeEnum paymentType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime,
            @RequestHeader(value = "X-Session-Token", required = false) String sessionToken) {

        UUID uuidUserId;
        try {
            uuidUserId = UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body("Invalid UUID format for userId"));
        }

        if (!validateUserSession(uuidUserId)) {
            return CompletableFuture.completedFuture(ResponseEntity.status(401).body("Session tidak valid atau kedaluwarsa"));
        }

        return paymentService.getFilteredTransactionHistory(uuidUserId, paymentType, startDateTime, endDateTime)
                .thenApply(ResponseEntity::ok);
    }

    // DTOs for requests

    public static class TopUpRequest {
        private String userId;
        private Double amount;
        private String description;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class KostPaymentRequest {
        private String userId;
        private String ownerId;
        private String kostId;
        private String description;
        private String couponCode;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getOwnerId() { return ownerId; }
        public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

        public String getKostId() { return kostId; }
        public void setKostId(String kostId) { this.kostId = kostId; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getCouponCode() { return couponCode; }
        public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
    }

    private void logRequestContext(String testingMode, String methodName) {
        if ("true".equalsIgnoreCase(testingMode)) {
            System.out.println(methodName + " called from test context");
        } else {
            System.out.println(methodName + " called from normal runtime");
        }
    }
}