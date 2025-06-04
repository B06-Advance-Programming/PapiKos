package id.cs.ui.advprog.inthecost.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

import id.cs.ui.advprog.inthecost.enums.KuponStatus;
import id.cs.ui.advprog.inthecost.strategy.KuponStatusStrategy;
import id.cs.ui.advprog.inthecost.strategy.KuponStatusStrategyManagement;

@Getter
@Setter
@Entity
@Table(name = "kupon")
public class Kupon {

    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue
    @Column(name = "id_kupon", nullable = false, columnDefinition = "uuid")
    private UUID idKupon;

    @Column(name = "nama_kupon", nullable=false)
    private String namaKupon;

    @Setter(AccessLevel.NONE)
    @Column(name = "kode_unik", nullable = false, length = 10, unique = true)
    private String kodeUnik;

    @Column(name = "persentase", nullable = false)
    private int persentase;

    @Column(name = "masa_berlaku", nullable = false)
    private LocalDate masaBerlaku;

    @Column(name = "deskripsi", nullable = false)
    private String deskripsi;

    @Setter(AccessLevel.NONE)
    @Column(name = "status_kupon", nullable = false, length = 50)

    @Enumerated(EnumType.STRING)
    private KuponStatus statusKupon;

    // Join dengan table kupon_kost untuk mendefinisikan hubungan many to many
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "kupon_kost",
            joinColumns = @JoinColumn(name = "id_kupon"),
            inverseJoinColumns = @JoinColumn(name = "id_kost")
    )
    private List<Kost> kosPemilik = new ArrayList<>();

    @Column(name = "quantity", nullable = false)
    private int quantity;

    public Kupon() {
        this.quantity = 1;
    }

    public Kupon(List<Kost> kosPemilik, String namaKupon,LocalDate masaBerlaku, int persentase, String deskripsi, int quantity) {
        validateInput(namaKupon, masaBerlaku, persentase, deskripsi, quantity);
        this.kodeUnik = generateKodeUnik();
        this.persentase = persentase;
        this.namaKupon = namaKupon;
        this.masaBerlaku = masaBerlaku;
        this.kosPemilik = kosPemilik;
        this.deskripsi = deskripsi;
        this.quantity = quantity;
        refreshStatus();
    }

    private void validateInput(String nama, LocalDate masaBerlaku, int persentase, String deskripsi, int quantity) {
        if (nama == null || nama.isBlank()) {
            throw new IllegalArgumentException("Nama kupon cannot be null");
        }
        if (masaBerlaku == null) {
            throw new IllegalArgumentException("Masa berlaku cannot be null");
        }
        if (persentase <= 0 || persentase > 100) {
            throw new IllegalArgumentException("Persentase must be between 1 and 100");
        }
        if (deskripsi == null || deskripsi.isBlank()) {
            throw new IllegalArgumentException("Deskripsi cannot be null or empty");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
    }

    //Kode unik random, hanya 6 huruf/angka. Seperti: ABCDE
    private String generateKodeUnik() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    public void refreshStatus() {
        this.statusKupon = KuponStatus.VALID;
        for (KuponStatusStrategy strategy : KuponStatusStrategyManagement.getStrategies()) {
            if (strategy.evaluate(this) == KuponStatus.INVALID) {
                this.statusKupon = KuponStatus.INVALID;
                return;
            }
        }
    }

    public void decreaseQuantityByOne() {
        if(this.quantity > 0)
            this.quantity = this.quantity - 1;
        refreshStatus();
    }

    public void setPersentase(int persentase) {
        if (persentase <= 0 || persentase > 100) {
            throw new IllegalArgumentException("Persentase must be between 1 and 100");
        }
        this.persentase = persentase;
    }

    public void setDeskripsi(String deskripsi) {
        if (deskripsi == null || deskripsi.isBlank()) {
            throw new IllegalArgumentException("Deskripsi cannot be null or empty");
        }
        this.deskripsi = deskripsi;
    }

    public void setQuantity(int quantity){
        this.quantity = quantity;
        refreshStatus();
    }

    public void setMasaBerlaku(LocalDate masaBerlaku) {
        if (masaBerlaku == null) {
            throw new IllegalArgumentException("Masa berlaku cannot be null");
        }
        this.masaBerlaku = masaBerlaku;
        refreshStatus();
    }

    @Override
    public String toString() {
        String namaKos = kosPemilik.stream()
                .map(Kost::getNama)
                .collect(Collectors.joining(", "));
        String status = statusKupon != null ? statusKupon.toString() : "UNKNOWN";

        return String.format(
                "Kupon[NamaKupon: %s, %s, %s%%, Hingga: %s, Status: %s, Quantity: %d, Kost: [%s]]",
                namaKupon,
                kodeUnik,
                persentase,
                masaBerlaku != null ? masaBerlaku.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")) : "N/A",
                status,
                quantity,
                namaKos.isEmpty() ? "Tidak ada" : namaKos
        );
    }

    @PrePersist
    private void prePersist() {
        if (kodeUnik == null) {
            kodeUnik = generateKodeUnik();
        }
        if (statusKupon == null) {
            refreshStatus();
        }
        if (quantity < 0) {
            quantity = 1;
        }
    }

    @PreUpdate
    private void preUpdate() {
        refreshStatus();
    }
}