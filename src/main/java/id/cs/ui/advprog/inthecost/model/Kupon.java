package id.cs.ui.advprog.inthecost.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;
import java.time.format.DateTimeFormatter;

import id.cs.ui.advprog.inthecost.enums.KuponStatus;
import id.cs.ui.advprog.inthecost.strategy.KuponStatusStrategy;
import id.cs.ui.advprog.inthecost.strategy.DefaultKuponStatusStrategy;

@Getter
@Setter
public class Kupon{
    @Setter(AccessLevel.NONE)
    private String idKupon;
    @Setter(AccessLevel.NONE)
    private String pemilik; //To be updated
    @Setter(AccessLevel.NONE)
    private String kodeUnik;
    private int persentase;
    private LocalDate masaBerlaku;
    private String deskripsi;
    @Setter(AccessLevel.NONE)
    private KuponStatus statusKupon;
    private static final KuponStatusStrategy defaultStatusStrategy = new DefaultKuponStatusStrategy();
    private boolean kuponGlobal;

    public Kupon(String pemilik, LocalDate masaBerlaku, int persentase, String deskripsi, boolean kuponGlobal) {
        validateInput(pemilik, masaBerlaku, persentase, deskripsi);
        this.idKupon = generateIdKupon();
        this.pemilik = pemilik;
        this.kodeUnik = generateKodeUnik();
        this.persentase = persentase;
        this.masaBerlaku = masaBerlaku;
        refreshStatus();
        this.deskripsi = deskripsi;
        this.kuponGlobal = kuponGlobal;
    }

    private void validateInput(String pemilik, LocalDate masaBerlaku, int persentase, String deskripsi) {
        if (pemilik == null || pemilik.isBlank()) {
            throw new IllegalArgumentException("Pemilik cannot be null or empty");
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

    private String generateIdKupon(){
        return UUID.randomUUID().toString();
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
        return String.format("Kupon[%s, %s, %d%%, Hingga: %s, Status: %s]", kodeUnik, pemilik, persentase, masaBerlaku.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),statusKupon);
    }
}