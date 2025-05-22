package id.cs.ui.advprog.inthecost.model;

import id.cs.ui.advprog.inthecost.enums.StatusPenyewaan;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "penyewaan_kos")
public class PenyewaanKos {

    public PenyewaanKos() {
        this.id = UUID.randomUUID();
    }

    @Id
    @Column(name = "penyewaan_id")
    private UUID id;

    @Column(name = "nama_lengkap")
    private String namaLengkap;

    @Column(name = "nomor_telepon")
    private String nomorTelepon;

    @Column(name = "tanggal_check_in")
    private LocalDate tanggalCheckIn;

    @Column(name = "durasi_bulan")
    private int durasiBulan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kost_id", nullable = false)
    private Kost kos;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusPenyewaan status = StatusPenyewaan.DIAJUKAN;

    // NEW added field userId, map to user_id column (UUID type)
    @Column(name = "user_id", columnDefinition = "uuid")
    private UUID userId;

    public boolean isEditable() {
        return this.status == StatusPenyewaan.DIAJUKAN;
    }
}