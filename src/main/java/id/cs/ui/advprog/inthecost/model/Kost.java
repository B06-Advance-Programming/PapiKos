package id.cs.ui.advprog.inthecost.model;

import id.cs.ui.advprog.inthecost.exception.*;
import id.cs.ui.advprog.inthecost.observer.Observer;
import id.cs.ui.advprog.inthecost.observer.Subject;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Entity
@Table(name = "kost")
public class Kost implements Subject {
    @Id
    @Column(name = "kost_id")
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

    @Transient // Mark as non-persistent
    private final Set<Observer> observers = new HashSet<>();

    @Transient
    private boolean enableObservers = true; // Flag to enable/disable observer notifications

    public Kost() {
        kostID = UUID.randomUUID();
    }

    public Kost(String nama, String alamat, String deskripsi, int jumlahKamar, int hargaPerBulan) {
        if (nama == null || nama.trim().isEmpty()) {
            throw new ValidationException(ValidationErrorCode.NULL_OR_EMPTY_VALUE, "Nama kosan tidak boleh kosong");
        }
        if (alamat == null || alamat.trim().isEmpty()) {
            throw new ValidationException(ValidationErrorCode.NULL_OR_EMPTY_VALUE, "Alamat kosan tidak boleh kosong");
        }
        if (deskripsi == null || deskripsi.trim().isEmpty()) {
            throw new ValidationException(ValidationErrorCode.NULL_OR_EMPTY_VALUE, "Deskripsi kosan tidak boleh kosong");
        }
        if (jumlahKamar < 0) {
            throw new ValidationException(ValidationErrorCode.NEGATIVE_VALUE, "Jumlah kamar tidak boleh negatif");
        }
        if (hargaPerBulan <= 0) {
            throw new ValidationException(ValidationErrorCode.ZERO_OR_NEGATIVE_VALUE, "Harga per bulan harus lebih besar dari 0");
        }

        this.kostID = UUID.randomUUID();
        this.nama = nama;
        this.alamat = alamat;
        this.deskripsi = deskripsi;
        this.jumlahKamar = jumlahKamar;
        this.hargaPerBulan = hargaPerBulan;
    }

    public void setNama(String nama) {
        if (nama == null || nama.trim().isEmpty()) {
            throw new ValidationException(ValidationErrorCode.NULL_OR_EMPTY_VALUE, "Nama kosan tidak boleh kosong");
        }
        this.nama = nama;
    }

    public void setAlamat(String alamat) {
        if (alamat == null || alamat.trim().isEmpty()) {
            throw new ValidationException(ValidationErrorCode.NULL_OR_EMPTY_VALUE, "Alamat kosan tidak boleh kosong");
        }
        this.alamat = alamat;
    }

    public void setDeskripsi(String deskripsi) {
        if (deskripsi == null || deskripsi.trim().isEmpty()) {
            throw new ValidationException(ValidationErrorCode.NULL_OR_EMPTY_VALUE, "Deskripsi kosan tidak boleh kosong");
        }
        this.deskripsi = deskripsi;
    }

    public void setJumlahKamar(int jumlahKamar) {
        if (jumlahKamar < 0) {
            throw new ValidationException(ValidationErrorCode.NEGATIVE_VALUE, "Jumlah kamar tidak boleh negatif");
        }
    
        int oldValue = this.jumlahKamar;
        this.jumlahKamar = jumlahKamar;
    
        if (enableObservers && oldValue == 0 && jumlahKamar > 0) {
            notifyObservers();
        }
    }

    public void setHargaPerBulan(int hargaPerBulan) {
        if (hargaPerBulan <= 0) {
            throw new ValidationException(ValidationErrorCode.ZERO_OR_NEGATIVE_VALUE, "Harga per bulan harus lebih besar dari 0");
        }
        this.hargaPerBulan = hargaPerBulan;
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
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
