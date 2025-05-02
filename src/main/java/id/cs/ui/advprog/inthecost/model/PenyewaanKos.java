package id.cs.ui.advprog.inthecost.model;

import id.cs.ui.advprog.inthecost.enums.StatusPenyewaan;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PenyewaanKos {
    private Long id;
    private String namaLengkap;
    private String nomorTelepon;
    private LocalDate tanggalCheckIn;
    private int durasiBulan;
    private Kost kos;
    private StatusPenyewaan status = StatusPenyewaan.DIAJUKAN;

    public boolean isEditable() {
        return this.status == StatusPenyewaan.DIAJUKAN;
    }
}
