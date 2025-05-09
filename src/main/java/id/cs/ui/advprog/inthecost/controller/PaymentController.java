package id.cs.ui.advprog.inthecost.controller;

import id.cs.ui.advprog.inthecost.enums.PaymentTypeEnum;
import id.cs.ui.advprog.inthecost.model.Payment;
import id.cs.ui.advprog.inthecost.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Change validation to accept UUID
    private boolean validateUserSession(UUID userId) {
        System.out.println("Validate session for userId=" + userId + " (mockup always true)");
        return true;
    }

    private boolean isKostAlreadyPaid(UUID userId, UUID kostId) {
        System.out.println("Checking if kost is already paid userId=" + userId + ", kostId=" + kostId + " (mockup false)");
        return false;
    }

    private boolean isUserAllowedToPayKost(UUID userId, UUID kostId) {
        System.out.println("Validating kost ownership userId=" + userId + ", kostId=" + kostId + " (mockup true)");
        return true;
    }

    private boolean isCouponValid(String couponCode, int quantity) {
        System.out.println("Validating couponCode=" + couponCode + ", quantity=" + quantity + " (mockup true)");
        return true;
    }

    @PostMapping("/topup")
    public ResponseEntity<?> topUp(
            @RequestBody TopUpRequest req,
            @RequestHeader(value = "X-Testing-Mode", required = false) String testingMode) {

        logRequestContext(testingMode, "topUp");

        UUID userId;
        try {
            userId = UUID.fromString(req.getUserId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid UUID format for userId");
        }

        if (!validateUserSession(userId)) {
            return ResponseEntity.status(401).body("Invalid user session");
        }

        Payment payment = paymentService.recordTopUpPayment(userId, req.getAmount(), req.getDescription());

        return ResponseEntity.ok(payment);
    }

    @PostMapping("/kost")
    public ResponseEntity<?> kostPayment(
            @RequestBody KostPaymentRequest req,
            @RequestHeader(value = "X-Testing-Mode", required = false) String testingMode) {

        logRequestContext(testingMode, "kostPayment");

        UUID userId, ownerId, kostId;
        try {
            userId = UUID.fromString(req.getUserId());
            ownerId = UUID.fromString(req.getOwnerId());
            kostId = UUID.fromString(req.getKostId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid UUID format for userId, ownerId, or kostId");
        }

        if (!validateUserSession(userId)) {
            return ResponseEntity.status(401).body("Invalid user session");
        }

        if (!isUserAllowedToPayKost(userId, kostId)) {
            return ResponseEntity.status(403).body("User tidak diizinkan membayar kost ini");
        }

        if (isKostAlreadyPaid(userId, kostId)) {
            return ResponseEntity.badRequest().body("Kost sudah dibayar untuk periode ini");
        }

        if (req.getCouponCode() != null && !req.getCouponCode().isBlank()) {
            if (!isCouponValid(req.getCouponCode(), req.getCouponQuantity() != null ? req.getCouponQuantity() : 0)) {
                return ResponseEntity.badRequest().body("Kupon tidak valid atau kuantitas habis");
            }
        }

        double finalAmount = req.getAmount();
        if (req.getDiscountPrice() != null && req.getDiscountPrice() > 0) {
            finalAmount = req.getAmount() - req.getDiscountPrice();
            if (finalAmount < 0) finalAmount = 0;
        }

        Payment payment = paymentService.recordKostPayment(
                userId,
                ownerId,
                kostId,
                finalAmount,
                req.getDescription()
        );

        return ResponseEntity.ok(payment);
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<?> getTransactionHistory(
            @PathVariable String userId,
            @RequestHeader(value = "X-Session-Token", required = false) String sessionToken) {

        UUID uuidUserId;
        try {
            uuidUserId = UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid UUID format for userId");
        }

        if (!validateUserSession(uuidUserId)) {
            return ResponseEntity.status(401).body("Session tidak valid atau kedaluwarsa");
        }

        List<Payment> history = paymentService.getTransactionHistory(uuidUserId);

        return ResponseEntity.ok(history);
    }

    @GetMapping("/history/{userId}/filter")
    public ResponseEntity<?> getFilteredTransactionHistory(
            @PathVariable String userId,
            @RequestParam(required = false) PaymentTypeEnum paymentType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime,
            @RequestHeader(value = "X-Session-Token", required = false) String sessionToken) {

        UUID uuidUserId;
        try {
            uuidUserId = UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid UUID format for userId");
        }

        if (!validateUserSession(uuidUserId)) {
            return ResponseEntity.status(401).body("Session tidak valid atau kedaluwarsa");
        }

        List<Payment> filtered = paymentService.getFilteredTransactionHistory(uuidUserId, paymentType, startDateTime, endDateTime);

        return ResponseEntity.ok(filtered);
    }

    // DTOs changes:

    public static class TopUpRequest {
        private String userId;  // changed from Long to String
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
        private String userId;  // changed from Long to String
        private String ownerId; // changed from Long to String
        private String kostId;  // changed from Long to String
        private Double amount;
        private String description;

        private String couponCode;
        private Integer couponQuantity;
        private Double discountPrice;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getOwnerId() { return ownerId; }
        public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

        public String getKostId() { return kostId; }
        public void setKostId(String kostId) { this.kostId = kostId; }

        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getCouponCode() { return couponCode; }
        public void setCouponCode(String couponCode) { this.couponCode = couponCode; }

        public Integer getCouponQuantity() { return couponQuantity; }
        public void setCouponQuantity(Integer couponQuantity) { this.couponQuantity = couponQuantity; }

        public Double getDiscountPrice() { return discountPrice; }
        public void setDiscountPrice(Double discountPrice) { this.discountPrice = discountPrice; }
    }

    /**
     * buat logging aja
     */
    private void logRequestContext(String testingMode, String methodName) {
        if ("true".equalsIgnoreCase(testingMode)) {
            System.out.println(methodName + " called from test context");
        } else {
            System.out.println(methodName + " called from normal runtime");
        }
    }
}