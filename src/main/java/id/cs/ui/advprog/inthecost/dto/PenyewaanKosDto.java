package id.cs.ui.advprog.inthecost.dto;

import id.cs.ui.advprog.inthecost.enums.StatusPenyewaan;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class PenyewaanKosDto {

    private UUID id;
    private String namaLengkap;
    private String nomorTelepon;
    private LocalDate tanggalCheckIn;
    private int durasiBulan;
    private StatusPenyewaan status;
    private UUID userId;
    private UUID kostId;
    private String namaKos;

    public PenyewaanKosDto(UUID id, String namaLengkap, String nomorTelepon, LocalDate tanggalCheckIn,
                           int durasiBulan, StatusPenyewaan status, UUID userId, UUID kostId, String namaKos) {
        this.id = id;
        this.namaLengkap = namaLengkap;
        this.nomorTelepon = nomorTelepon;
        this.tanggalCheckIn = tanggalCheckIn;
        this.durasiBulan = durasiBulan;
        this.status = status;
        this.userId = userId;
        this.kostId = kostId;
        this.namaKos = namaKos;
    }
}
