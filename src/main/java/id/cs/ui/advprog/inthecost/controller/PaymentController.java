package id.cs.ui.advprog.inthecost.controller;

import id.cs.ui.advprog.inthecost.enums.PaymentTypeEnum;
import id.cs.ui.advprog.inthecost.enums.StatusPenyewaan;
import id.cs.ui.advprog.inthecost.model.Payment;
import id.cs.ui.advprog.inthecost.service.PaymentService;
import id.cs.ui.advprog.inthecost.service.PenyewaanKosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import id.cs.ui.advprog.inthecost.model.PenyewaanKos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.List;

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

    @PreAuthorize("hasAnyRole('USER', 'PENYEWA', 'PEMILIK')")
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

    @PreAuthorize("hasAnyRole('USER', 'PENYEWA', 'PEMILIK')")
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

    @PreAuthorize("hasAnyRole('USER', 'PENYEWA', 'PEMILIK')")
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

    @PreAuthorize("hasAnyRole('USER', 'PENYEWA', 'PEMILIK')")
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

    @PreAuthorize("hasAnyRole('PENYEWA')")
    @GetMapping("/penyewaan/diajukan")
    public ResponseEntity<?> getDiajukanPenyewaanKosByUser(
            @RequestParam("userId") String userIdStr) {
        UUID userId;
        try {
            userId = UUID.fromString(userIdStr);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid UUID for userId");
        }

        List<PenyewaanKos> result = penyewaanKosService.getAllByUserIdAndStatus(userId, StatusPenyewaan.DIAJUKAN);
        List<PenyewaanKosDTO> dtos = result.stream()
                .map(pk -> new PenyewaanKosDTO(
                        pk.getId(),
                        pk.getNamaLengkap() != null ? pk.getNamaLengkap() : "",
                        pk.getNomorTelepon(),
                        pk.getTanggalCheckIn(),
                        pk.getDurasiBulan(),
                        pk.getKos() != null ? pk.getKos().getKostID() : null,
                        pk.getStatus(),
                        pk.getUserId()
                ))
                .toList();
        return ResponseEntity.ok(dtos);
    }

    public static class PenyewaanKosDTO {
        private UUID id;
        private String namaLengkap;
        private String nomorTelepon;
        private LocalDate tanggalCheckIn;
        private int durasiBulan;
        private UUID kostId;
        private StatusPenyewaan status;
        private UUID userId;

        public PenyewaanKosDTO(UUID id, String namaLengkap, String nomorTelepon, LocalDate tanggalCheckIn, int durasiBulan, UUID kostId, StatusPenyewaan status, UUID userId) {
            this.id = id;
            this.namaLengkap = namaLengkap;
            this.nomorTelepon = nomorTelepon;
            this.tanggalCheckIn = tanggalCheckIn;
            this.durasiBulan = durasiBulan;
            this.kostId = kostId;
            this.status = status;
            this.userId = userId;
        }

        public UUID getId() { return id; }
        public String getNamaLengkap() { return namaLengkap; }
        public String getNomorTelepon() { return nomorTelepon; }
        public LocalDate getTanggalCheckIn() { return tanggalCheckIn; }
        public int getDurasiBulan() { return durasiBulan; }
        public UUID getKostId() { return kostId; }
        public StatusPenyewaan getStatus() { return status; }
        public UUID getUserId() { return userId; }
    }

}