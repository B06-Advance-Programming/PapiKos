package id.cs.ui.advprog.inthecost.builder;

import id.cs.ui.advprog.inthecost.enums.StatusPenyewaan;
import id.cs.ui.advprog.inthecost.model.KosSewa;
import id.cs.ui.advprog.inthecost.model.PenyewaanKos;

import java.time.LocalDate;

public class PenyewaanKosBuilder {
    private Long id;
    private String namaLengkap;
    private String nomorTelepon;
    private LocalDate tanggalCheckIn;
    private int durasiBulan;
    private KosSewa kos;
    private StatusPenyewaan status = StatusPenyewaan.DIAJUKAN;

    public static PenyewaanKosBuilder builder() {
        return new PenyewaanKosBuilder();
    }

    public PenyewaanKosBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public PenyewaanKosBuilder namaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
        return this;
    }

    public PenyewaanKosBuilder nomorTelepon(String nomorTelepon) {
        this.nomorTelepon = nomorTelepon;
        return this;
    }

    public PenyewaanKosBuilder tanggalCheckIn(LocalDate tanggalCheckIn) {
        this.tanggalCheckIn = tanggalCheckIn;
        return this;
    }

    public PenyewaanKosBuilder durasiBulan(int durasiBulan) {
        this.durasiBulan = durasiBulan;
        return this;
    }

    public PenyewaanKosBuilder kos(KosSewa kos) {
        this.kos = kos;
        return this;
    }

    public PenyewaanKosBuilder status(StatusPenyewaan status) {
        this.status = status;
        return this;
    }

    public PenyewaanKos build() {
        PenyewaanKos penyewaan = new PenyewaanKos();
        penyewaan.setId(this.id);
        penyewaan.setNamaLengkap(this.namaLengkap);
        penyewaan.setNomorTelepon(this.nomorTelepon);
        penyewaan.setTanggalCheckIn(this.tanggalCheckIn);
        penyewaan.setDurasiBulan(this.durasiBulan);
        penyewaan.setKos(this.kos);
        penyewaan.setStatus(this.status);
        return penyewaan;
    }
}
