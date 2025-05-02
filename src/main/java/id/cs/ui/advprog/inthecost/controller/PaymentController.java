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

    /**
     * buat logging aja
     */
    private void logRequestContext(String testingMode, String methodName) {
        boolean isTestRequest = "true".equalsIgnoreCase(testingMode);
        if (isTestRequest) {
            System.out.println(methodName + " called from test context");
        } else {
            System.out.println(methodName + " called from normal runtime");
        }
    }

    /**
     * Validate if user session is active and valid
     * // Mockup: replace with real integration logic
     */
    private boolean validateUserSession(Long userId) {
        // TODO: implement real session validation with auth service
        System.out.println("Validate session for userId=" + userId + " (mockup always true)");
        return true;
    }

    /**
     * Check if a kost payment is already made by user for that kost and period
     * // Mockup: replace with real persistence check
     */
    private boolean isKostAlreadyPaid(Long userId, Long kostId) {
        // TODO: check from DB or payment service if kost already paid for user and kostId
        System.out.println("Checking if kost is already paid userId=" + userId + ", kostId=" + kostId + " (mockup false)");
        return false;
    }

    /**
     * Check if the kostId belongs to user or is allowed for user to pay
     * // Mockup: replace with real ownership check
     */
    private boolean isUserAllowedToPayKost(Long userId, Long kostId) {
        // TODO: verify kost ownership or permission
        System.out.println("Validating kost ownership userId=" + userId + ", kostId=" + kostId + " (mockup true)");
        return true;
    }

    /**
     * Validate discount coupon: check coupon code validity and remaining quantity
     * // Mockup: replace with real coupon validation
     */
    private boolean isCouponValid(String couponCode, int quantity) {
        // TODO: validate coupon code and quantity left for that coupon
        System.out.println("Validating couponCode=" + couponCode + ", quantity=" + quantity + " (mockup true)");
        return true;
    }

    /**
     * API untuk top up balance user
     */
    @PostMapping("/topup")
    public ResponseEntity<?> topUp(
            @RequestBody TopUpRequest req,
            @RequestHeader(value = "X-Testing-Mode", required = false) String testingMode) {

        logRequestContext(testingMode, "topUp");

        if (!validateUserSession(req.getUserId())) {
            return ResponseEntity.status(401).body("Invalid user session");
        }

        // Assume top-up does not accept coupon/discount currently
        Payment payment = paymentService.recordTopUpPayment(req.getUserId(), req.getAmount(), req.getDescription());
        return ResponseEntity.ok(payment);
    }


    /**
     * API untuk pembayaran kos
     */
    @PostMapping("/kost")
    public ResponseEntity<?> kostPayment(
            @RequestBody KostPaymentRequest req,
            @RequestHeader(value = "X-Testing-Mode", required = false) String testingMode) {

        logRequestContext(testingMode, "kostPayment");

        if (!validateUserSession(req.getUserId())) {
            return ResponseEntity.status(401).body("Invalid user session");
        }

        // Validate kost ownership
        if (!isUserAllowedToPayKost(req.getUserId(), req.getKostId())) {
            return ResponseEntity.status(403).body("User not allowed to pay for this kost");
        }

        // Check if already paid
        if (isKostAlreadyPaid(req.getUserId(), req.getKostId())) {
            return ResponseEntity.badRequest().body("Kost already paid for this period");
        }

        // Validate coupon if provided
        if (req.getCouponCode() != null && !req.getCouponCode().isEmpty()) {
            if (!isCouponValid(req.getCouponCode(), req.getCouponQuantity())) {
                return ResponseEntity.badRequest().body("Invalid or exhausted coupon");
            }
        }

        // Calculate discounted amount if coupon applied
        double finalAmount = req.getAmount();
        if (req.getDiscountPrice() != null && req.getDiscountPrice() > 0) {
            finalAmount = req.getAmount() - req.getDiscountPrice();
            if (finalAmount < 0) finalAmount = 0;
        }

        Payment payment = paymentService.recordKostPayment(
                req.getUserId(),
                req.getOwnerId(),
                req.getKostId(),
                finalAmount,
                req.getDescription());

        return ResponseEntity.ok(payment);
    }


    /**
     * API untuk transaksi pengguna lengkap
     */
    @GetMapping("/history/{userId}")
    public ResponseEntity<?> getTransactionHistory(
            @PathVariable Long userId,
            @RequestHeader(value = "X-Session-Token", required = false) String sessionToken) {

        if (!validateUserSession(userId)) {
            return ResponseEntity.status(401).body("Invalid or expired session");
        }

        List<Payment> history = paymentService.getTransactionHistory(userId);
        return ResponseEntity.ok(history);
    }


    /**
     * API untuk filtered transaksi pengguna berdasarkan tanggal atau payment type
     */
    @GetMapping("/history/{userId}/filter")
    public ResponseEntity<?> getFilteredTransactionHistory(
            @PathVariable Long userId,
            @RequestParam(required = false) PaymentTypeEnum paymentType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestHeader(value = "X-Session-Token", required = false) String sessionToken) {

        if (!validateUserSession(userId)) {
            return ResponseEntity.status(401).body("Invalid or expired session");
        }

        List<Payment> filtered = paymentService.getFilteredTransactionHistory(userId, paymentType, startDate, endDate);
        return ResponseEntity.ok(filtered);
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

        // Adding coupon fields
        private String couponCode;        // nullable coupon code
        private Integer couponQuantity;   // nullable quantity for coupon
        private Double discountPrice;     // nullable discount price

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

        public String getCouponCode() { return couponCode; }
        public void setCouponCode(String couponCode) { this.couponCode = couponCode; }

        public Integer getCouponQuantity() { return couponQuantity; }
        public void setCouponQuantity(Integer couponQuantity) { this.couponQuantity = couponQuantity; }

        public Double getDiscountPrice() { return discountPrice; }
        public void setDiscountPrice(Double discountPrice) { this.discountPrice = discountPrice; }
    }
}