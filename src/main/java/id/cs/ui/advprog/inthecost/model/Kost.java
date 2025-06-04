package id.cs.ui.advprog.inthecost.model;

import id.cs.ui.advprog.inthecost.exception.*;
import id.cs.ui.advprog.inthecost.observer.Observer;
import id.cs.ui.advprog.inthecost.observer.Subject;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Getter
@Entity
@Table(name = "kost")
public class Kost implements Subject {
    @Id
    @Column(name = "kost_id", nullable = false)
    private UUID kostID;

    @Column(name = "nama", nullable = false)
    private String nama;

    @Column(name = "alamat", nullable = false)
    private String alamat;

    @Column(name = "deskripsi", nullable = false)
    private String deskripsi;

    @Column(name = "jumlah_kamar", nullable = false)
    private int jumlahKamar;

    @Column(name = "harga_per_bulan", nullable = false)
    private int hargaPerBulan;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Transient // Mark as non-persistent
    private final Set<Observer> observers = new HashSet<>();

    @Transient
    private boolean enableObservers = true; // Flag to enable/disable observer notifications

    private static final String KOSAN_KOSONG = "Nama kosan tidak boleh kosong";
    private static final String ALAMAT_KOSONG = "Alamat kosan tidak boleh kosong";
    private static final String DESKRIPSI_KOSONG = "Deskripsi kosan tidak boleh kosong";
    private static final String KAMAR_NEGATIF = "Jumlah kamar tidak boleh negatif";
    private static final String HARGA_STRING = "Harga per bulan harus lebih besar dari 0";

    // manual set each times
    public Kost() {
        kostID = UUID.randomUUID();
    }

    // constructor based
    public Kost(String nama, String alamat, String deskripsi, int jumlahKamar, int hargaPerBulan) {
        // handle null atau kosong
        if (nama == null || nama.trim().isEmpty()) {
            throw new ValidationException(ValidationErrorCode.NULL_OR_EMPTY_VALUE, KOSAN_KOSONG);
        }
        if (alamat == null || alamat.trim().isEmpty()) {
            throw new ValidationException(ValidationErrorCode.NULL_OR_EMPTY_VALUE, ALAMAT_KOSONG);
        }
        if (deskripsi == null || deskripsi.trim().isEmpty()) {
            throw new ValidationException(ValidationErrorCode.NULL_OR_EMPTY_VALUE, DESKRIPSI_KOSONG);
        }

        // handle integer value yang tidak diperbolehkan
        if (jumlahKamar < 0) {
            throw new ValidationException(ValidationErrorCode.NEGATIVE_VALUE, KAMAR_NEGATIF);
        }
        if (hargaPerBulan <= 0) {
            throw new ValidationException(ValidationErrorCode.ZERO_OR_NEGATIVE_VALUE, HARGA_STRING);
        }

        this.kostID = UUID.randomUUID();
        this.nama = nama;
        this.alamat = alamat;
        this.deskripsi = deskripsi;
        this.jumlahKamar = jumlahKamar;
        this.hargaPerBulan = hargaPerBulan;
    }

    // constructor with owner_id
    public Kost(String nama, String alamat, String deskripsi, int jumlahKamar, int hargaPerBulan, UUID ownerId) {
        // handle null atau kosong
        if (nama == null || nama.trim().isEmpty()) {
            throw new ValidationException(ValidationErrorCode.NULL_OR_EMPTY_VALUE, KOSAN_KOSONG);
        }
        if (alamat == null || alamat.trim().isEmpty()) {
            throw new ValidationException(ValidationErrorCode.NULL_OR_EMPTY_VALUE, ALAMAT_KOSONG);
        }
        if (deskripsi == null || deskripsi.trim().isEmpty()) {
            throw new ValidationException(ValidationErrorCode.NULL_OR_EMPTY_VALUE, DESKRIPSI_KOSONG);
        }
        if (ownerId == null) {
            throw new ValidationException(ValidationErrorCode.NULL_OR_EMPTY_VALUE, "Owner ID tidak boleh kosong");
        }

        // handle integer value yang tidak diperbolehkan
        if (jumlahKamar < 0) {
            throw new ValidationException(ValidationErrorCode.NEGATIVE_VALUE, KAMAR_NEGATIF);
        }
        if (hargaPerBulan <= 0) {
            throw new ValidationException(ValidationErrorCode.ZERO_OR_NEGATIVE_VALUE, HARGA_STRING);
        }

        this.kostID = UUID.randomUUID();
        this.nama = nama;
        this.alamat = alamat;
        this.deskripsi = deskripsi;
        this.jumlahKamar = jumlahKamar;
        this.hargaPerBulan = hargaPerBulan;
        this.ownerId = ownerId;
    }

    // cek manual saat set
    public void setNama(String nama) {
        if (nama == null || nama.trim().isEmpty()) {
            throw new ValidationException(ValidationErrorCode.NULL_OR_EMPTY_VALUE, KOSAN_KOSONG);
        }
        this.nama = nama;
    }
    public void setAlamat(String alamat) {
        if (alamat == null || alamat.trim().isEmpty()) {
            throw new ValidationException(ValidationErrorCode.NULL_OR_EMPTY_VALUE, ALAMAT_KOSONG);
        }
        this.alamat = alamat;
    }
    public void setDeskripsi(String deskripsi) {
        if (deskripsi == null || deskripsi.trim().isEmpty()) {
            throw new ValidationException(ValidationErrorCode.NULL_OR_EMPTY_VALUE, DESKRIPSI_KOSONG);
        }
        this.deskripsi = deskripsi;
    }    public void setJumlahKamar(int jumlahKamar) {
        if (jumlahKamar < 0) {
            throw new ValidationException(ValidationErrorCode.NEGATIVE_VALUE, KAMAR_NEGATIF);
        }        int oldValue = this.jumlahKamar;
        this.jumlahKamar = jumlahKamar;

        if (enableObservers && oldValue == 0 && jumlahKamar > 0) {
            log.info("🔔 OBSERVER PATTERN TRIGGERED: Kost '{}' rooms changed from {} to {} - Notifying observers!", this.nama, oldValue, jumlahKamar);
            notifyObservers();
        }
    }
    public void setHargaPerBulan(int hargaPerBulan) {
        if (hargaPerBulan <= 0) {
            throw new ValidationException(ValidationErrorCode.ZERO_OR_NEGATIVE_VALUE, HARGA_STRING);
        }
        this.hargaPerBulan = hargaPerBulan;
    }

    public void setOwnerId(UUID ownerId) {
        if (ownerId == null) {
            throw new ValidationException(ValidationErrorCode.NULL_OR_EMPTY_VALUE, "Owner ID tidak boleh kosong");
        }
        this.ownerId = ownerId;
    }

    public void setKostID(UUID kostID) {
        this.kostID = kostID;
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    // Clear all observers
    public void clearObservers() {
        observers.clear();
    }    @Override
    public void notifyObservers() {
        log.debug("🔔 NOTIFY OBSERVERS: {} observers registered", observers.size());
        for (Observer observer : observers) {
            observer.update(this);
        }
    }

    // Disable observer notifications (useful for tests)
    public void disableObservers() {
        this.enableObservers = false;
    }

    // Enable observer notifications
    public void enableObservers() {
        this.enableObservers = true;
    }
}