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
     * API untuk top up balance user
     *
     * HTTP Method: POST
     * URL: /api/payments/topup
     *
     * Contoh Payload:
     * {
     *     "userId": 1,
     *     "amount": 150000.0,
     *     "description": "Top Up via bank transfer"
     * }
     *
     * Header for test detection:
     * X-Testing-Mode: true
     *
     * Response: Returns the created Payment record with status 200 OK.
     */
    @PostMapping("/topup")
    public ResponseEntity<Payment> topUp(
            @RequestBody TopUpRequest req,
            @RequestHeader(value = "X-Testing-Mode", required = false) String testingMode) {

        boolean isTestRequest = "true".equalsIgnoreCase(testingMode);

        if (isTestRequest) {
            // test request
            System.out.println("topUp called from test context");
        } else {
            // real request
            System.out.println("topUp called from normal runtime");
        }

        Payment payment = paymentService.recordTopUpPayment(req.getUserId(), req.getAmount(), req.getDescription());
        return ResponseEntity.ok(payment);
    }


    /**
     * API untuk pembayaran kos
     *
     * HTTP Method: POST
     * URL: /api/payments/kost
     *
     * Contoh Payload:
     * {
     *     "userId": 2,
     *     "ownerId": 20,
     *     "kostId": 5,
     *     "amount": 500000.0,
     *     "description": "Monthly Kost Payment for May"
     * }
     *
     * Header for test detection:
     * X-Testing-Mode: true
     *
     * Response: Returns the created kost Payment record with status 200 OK.
     */
    @PostMapping("/kost")
    public ResponseEntity<Payment> kostPayment(
            @RequestBody KostPaymentRequest req,
            @RequestHeader(value = "X-Testing-Mode", required = false) String testingMode) {

        boolean isTestRequest = "true".equalsIgnoreCase(testingMode);

        if (isTestRequest) {
            System.out.println("kostPayment called from test context");
        } else {
            System.out.println("kostPayment called from normal runtime");
        }

        Payment payment = paymentService.recordKostPayment(
                req.getUserId(),
                req.getOwnerId(),
                req.getKostId(),
                req.getAmount(),
                req.getDescription());
        return ResponseEntity.ok(payment);
    }


    /**
     * API untuk transaksi pengguna lengkap
     *
     * HTTP Method: GET
     * URL: /api/payments/history/{userId}
     *
     * Path Variable:
     *  - userId: The ID of the user whose transaction history to retrieve
     *
     * Response: Returns a list of all Payment records of that user.
     */
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<Payment>> getTransactionHistory(@PathVariable Long userId) {
        List<Payment> history = paymentService.getTransactionHistory(userId);
        return ResponseEntity.ok(history);
    }


    /**
     * API untuk filtered transaksi pengguna berdasarkan tanggal atau payment type
     *
     * HTTP Method: GET
     * URL: /api/payments/history/{userId}/filter
     *
     * Path Variable:
     *  - userId: The ID of the user whose filtered transaction history to retrieve
     *
     * Query Parameters (all optional):
     *  - paymentType: Filter by payment type (e.g., TOP_UP, KOST_PAYMENT)
     *  - startDate: Filter transactions from this date (format: yyyy-MM-dd)
     *  - endDate: Filter transactions up to this date (format: yyyy-MM-dd)
     *
     * Example URL:
     *   /api/payments/history/1/filter?paymentType=TOP_UP&startDate=2024-05-01&endDate=2024-05-31
     *
     * Response: Returns a list of Payment records matching filters.
     */
    @GetMapping("/history/{userId}/filter")
    public ResponseEntity<List<Payment>> getFilteredTransactionHistory(
            @PathVariable Long userId,
            @RequestParam(required = false) PaymentTypeEnum paymentType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
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