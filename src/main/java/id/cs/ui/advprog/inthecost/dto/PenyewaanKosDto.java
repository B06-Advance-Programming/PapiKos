package id.cs.ui.advprog.inthecost.dto;

import id.cs.ui.advprog.inthecost.enums.StatusPenyewaan;
import java.time.LocalDate;
import java.util.UUID;

public class PenyewaanKosDto {

    private UUID id;
    private String namaLengkap;
    private String nomorTelepon;
    private LocalDate tanggalCheckIn;
    private int durasiBulan;
    private StatusPenyewaan status;
    private UUID userId;
    private UUID kostId;

    public PenyewaanKosDto(UUID id, String namaLengkap, String nomorTelepon, LocalDate tanggalCheckIn,
                           int durasiBulan, StatusPenyewaan status, UUID userId, UUID kostId) {
        this.id = id;
        this.namaLengkap = namaLengkap;
        this.nomorTelepon = nomorTelepon;
        this.tanggalCheckIn = tanggalCheckIn;
        this.durasiBulan = durasiBulan;
        this.status = status;
        this.userId = userId;
        this.kostId = kostId;
    }

    // Getters and setters (bisa pakai Lombok @Getter @Setter kalau mau)
    public UUID getId() { return id; }
    public String getNamaLengkap() { return namaLengkap; }
    public String getNomorTelepon() { return nomorTelepon; }
    public LocalDate getTanggalCheckIn() { return tanggalCheckIn; }
    public int getDurasiBulan() { return durasiBulan; }
    public StatusPenyewaan getStatus() { return status; }
    public UUID getUserId() { return userId; }
    public UUID getKostId() { return kostId; }
}
