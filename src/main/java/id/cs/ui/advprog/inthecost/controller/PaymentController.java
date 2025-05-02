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
        if ("true".equalsIgnoreCase(testingMode)) {
            System.out.println(methodName + " called from test context");
        } else {
            System.out.println(methodName + " called from normal runtime");
        }
    }

    /**
     * Validate jika session user aktif dan valid
     * // Mockup: ganti dengan integrasi service sesungguhnya
     */
    private boolean validateUserSession(Long userId) {
        System.out.println("Validate session for userId=" + userId + " (mockup always true)");
        return true;
    }

    /**
     * Cek apakah user sudah bayar kost untuk periode tersebut
     * // Mockup: ganti dengan cek persistence data asli
     */
    private boolean isKostAlreadyPaid(Long userId, Long kostId) {
        System.out.println("Checking if kost is already paid userId=" + userId + ", kostId=" + kostId + " (mockup false)");
        return false;
    }

    /**
     * Cek apakah kost tersebut milik user atau user berhak bayar
     * // Mockup: ganti dengan cek kepemilikan yang sebenarnya
     */
    private boolean isUserAllowedToPayKost(Long userId, Long kostId) {
        System.out.println("Validating kost ownership userId=" + userId + ", kostId=" + kostId + " (mockup true)");
        return true;
    }

    /**
     * Validasi kupon diskon: validasi kode kupon dan sisa kuantitas
     * // Mockup: ganti dengan validasi kupon asli
     */
    private boolean isCouponValid(String couponCode, int quantity) {
        System.out.println("Validating couponCode=" + couponCode + ", quantity=" + quantity + " (mockup true)");
        return true;
    }

    /**
     * API untuk top up saldo user
     */
    @PostMapping("/topup")
    public ResponseEntity<?> topUp(
            @RequestBody TopUpRequest req,
            @RequestHeader(value = "X-Testing-Mode", required = false) String testingMode) {

        logRequestContext(testingMode, "topUp");

        if (!validateUserSession(req.getUserId())) {
            return ResponseEntity.status(401).body("Invalid user session");
        }

        // Top-up saat ini belum menerima kupon/diskon
        Payment payment = paymentService.recordTopUpPayment(req.getUserId(), req.getAmount(), req.getDescription());

        return ResponseEntity.ok(payment);
    }

    /**
     * API untuk pembayaran kos dengan validasi dan dukungan diskon
     */
    @PostMapping("/kost")
    public ResponseEntity<?> kostPayment(
            @RequestBody KostPaymentRequest req,
            @RequestHeader(value = "X-Testing-Mode", required = false) String testingMode) {

        logRequestContext(testingMode, "kostPayment");

        if (!validateUserSession(req.getUserId())) {
            return ResponseEntity.status(401).body("Invalid user session");
        }

        if (!isUserAllowedToPayKost(req.getUserId(), req.getKostId())) {
            return ResponseEntity.status(403).body("User tidak diizinkan membayar kost ini");
        }

        if (isKostAlreadyPaid(req.getUserId(), req.getKostId())) {
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
                req.getUserId(),
                req.getOwnerId(),
                req.getKostId(),
                finalAmount,
                req.getDescription()
        );

        return ResponseEntity.ok(payment);
    }

    /**
     * API untuk mendapatkan riwayat transaksi lengkap user, harus valid session
     */
    @GetMapping("/history/{userId}")
    public ResponseEntity<?> getTransactionHistory(
            @PathVariable Long userId,
            @RequestHeader(value = "X-Session-Token", required = false) String sessionToken) {

        if (!validateUserSession(userId)) {
            return ResponseEntity.status(401).body("Session tidak valid atau kedaluwarsa");
        }

        List<Payment> history = paymentService.getTransactionHistory(userId);

        return ResponseEntity.ok(history);
    }

    /**
     * API untuk mendapatkan riwayat transaksi user yang difilter berdasarkan tanggal atau tipe pembayaran,
     * harus valid session
     */
    @GetMapping("/history/{userId}/filter")
    public ResponseEntity<?> getFilteredTransactionHistory(
            @PathVariable Long userId,
            @RequestParam(required = false) PaymentTypeEnum paymentType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestHeader(value = "X-Session-Token", required = false) String sessionToken) {

        if (!validateUserSession(userId)) {
            return ResponseEntity.status(401).body("Session tidak valid atau kedaluwarsa");
        }

        List<Payment> filtered = paymentService.getFilteredTransactionHistory(userId, paymentType, startDate, endDate);

        return ResponseEntity.ok(filtered);
    }

    /** DTO request untuk top-up */
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

    /** DTO request untuk pembayaran kost dengan dukungan kupon/diskon */
    public static class KostPaymentRequest {
        private Long userId;
        private Long ownerId;
        private Long kostId;
        private Double amount;
        private String description;

        private String couponCode;        // kupon bisa null
        private Integer couponQuantity;   // jumlah kupon bisa null
        private Double discountPrice;     // harga diskon bisa null

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