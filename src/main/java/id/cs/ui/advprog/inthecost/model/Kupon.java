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
import id.cs.ui.advprog.inthecost.strategy.DefaultKuponStatusStrategy;

@Getter
@Setter
@Entity
@Table(name = "kupon")
public class Kupon{

    @Setter(AccessLevel.NONE)
    @Id
    @Column(name = "id_kupon", nullable = false, columnDefinition = "uuid")
    private UUID idKupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pemilik", nullable = false)
    private User pemilik;

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

    @Column(name = "kupon_global", nullable = false)
    private boolean kuponGlobal;

    @ManyToMany
    @JoinTable(
            name = "kupon_kost",
            joinColumns = @JoinColumn(name = "id_kupon"),
            inverseJoinColumns = @JoinColumn(name = "id_kost")
    )
    private List<Kost> kosPemilik = new ArrayList<>();

    private static final KuponStatusStrategy defaultStatusStrategy = new DefaultKuponStatusStrategy();

    public Kupon() {
    }

    public Kupon(User pemilik, List<Kost> kosPemilik, LocalDate masaBerlaku, int persentase, String deskripsi, boolean kuponGlobal) {
        validateInput(pemilik, masaBerlaku, persentase, deskripsi);
        this.idKupon = UUID.randomUUID();
        this.pemilik = pemilik;
        this.kodeUnik = generateKodeUnik();
        this.persentase = persentase;
        this.masaBerlaku = masaBerlaku;
        this.kosPemilik = kosPemilik;
        this.deskripsi = deskripsi;
        this.kuponGlobal = kuponGlobal;
        refreshStatus();
    }

    private void validateInput(User pemilik, LocalDate masaBerlaku, int persentase, String deskripsi) {
        if (pemilik == null) {
            throw new IllegalArgumentException("Pemilik cannot be null");
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
    }

    private String generateKodeUnik() {
        return UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    public void refreshStatus() {
        this.statusKupon = defaultStatusStrategy.evaluate(this);
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

    public void setMasaBerlaku(LocalDate masaBerlaku) {
        if (masaBerlaku == null) {
            throw new IllegalArgumentException("Masa berlaku cannot be null");
        }
        this.masaBerlaku = masaBerlaku;
        refreshStatus();
    }

    @Override
    public String toString() {
        String namaPemilik = pemilik.getUsername();
        String namaKos = kosPemilik.stream()
                .map(Kost::getNama)
                .collect(Collectors.joining(", "));
        String status = statusKupon.toString();

        return String.format("Kupon[%s, %s, %d%%, Hingga: %s, Status: %s, Kost: [%s]]",
                kodeUnik,
                namaPemilik,
                persentase,
                masaBerlaku.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
                status,
                namaKos);
    }
}