package id.cs.ui.advprog.inthecost.model;
import id.cs.ui.advprog.inthecost.exception.*;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import jakarta.persistence.Column;

import java.util.UUID;

@Getter
@Entity
@Table(name = "kost")
public class Kost {
    @Id
    @Column(name = "kost_id")
    private UUID kostID;

    @Column(name = "nama")
    private String nama;

    @Column(name = "alamat")
    private String alamat;

    @Column(name = "deskripsi")
    private String deskripsi;

    @Column(name = "jumlah_kamar")
    private int jumlahKamar;

    @Column(name = "harga_per_bulan")
    private int hargaPerBulan;

    // manual set each times
    public Kost() {
        kostID = UUID.randomUUID();
    }

    // constructor based
    public Kost(String nama, String alamat, String deskripsi, int jumlahKamar, int hargaPerBulan) {
        // handle null atau kosong
        if (nama == null) {
            throw new ValidationException(ValidationErrorCode.NULL_VALUE, "Nama kosan tidak boleh null");
        }
        if (nama.trim().isEmpty()) {
            throw new ValidationException(ValidationErrorCode.EMPTY_VALUE, "Nama kosan tidak boleh kosong");
        }
        if (alamat == null) {
            throw new ValidationException(ValidationErrorCode.NULL_VALUE, "Alamat kosan tidak boleh null");
        }
        if (alamat.trim().isEmpty()) {
            throw new ValidationException(ValidationErrorCode.EMPTY_VALUE, "Alamat kosan tidak boleh kosong");
        }
        if (deskripsi == null) {
            throw new ValidationException(ValidationErrorCode.NULL_VALUE, "Deskripsi kosan tidak boleh null");
        }
        if (deskripsi.trim().isEmpty()) {
            throw new ValidationException(ValidationErrorCode.EMPTY_VALUE, "Deskripsi kosan tidak boleh kosong");
        }

        // handle integer value yang tidak diperbolehkan
        if (jumlahKamar < 0) {
            throw new ValidationException(ValidationErrorCode.NEGATIVE_VALUE, "Jumlah kamar tidak boleh negatif");
        }
        if (jumlahKamar == 0) {
            throw new ValidationException(ValidationErrorCode.ZERO_VALUE, "Jumlah kamar tidak boleh nol");
        }
        if (hargaPerBulan == 0) {
            throw new ValidationException(ValidationErrorCode.ZERO_VALUE, "Harga per bulan tidak boleh nol");
        }
        if (hargaPerBulan < 0) {
            throw new ValidationException(ValidationErrorCode.NEGATIVE_VALUE, "Harga per bulan tidak boleh negatif");
        }

        kostID = UUID.randomUUID();
        this.nama = nama;
        this.alamat = alamat;
        this.deskripsi = deskripsi;
        this.jumlahKamar = jumlahKamar;
        this.hargaPerBulan = hargaPerBulan;
    }

    // cek manual saat set
    public void setNama(String nama) {
        if (nama == null) {
            throw new ValidationException(ValidationErrorCode.NULL_VALUE, "Nama kosan tidak boleh null");
        }
        if (nama.trim().isEmpty()) {
            throw new ValidationException(ValidationErrorCode.EMPTY_VALUE, "Nama kosan tidak boleh kosong");
        }
        this.nama = nama;
    }
    public void setAlamat(String alamat) {
        if (alamat == null) {
            throw new ValidationException(ValidationErrorCode.NULL_VALUE, "Alamat kosan tidak boleh null");
        }
        if (alamat.trim().isEmpty()) {
            throw new ValidationException(ValidationErrorCode.EMPTY_VALUE, "Alamat kosan tidak boleh kosong");
        }
        this.alamat = alamat;
    }
    public void setDeskripsi(String deskripsi) {
        if (deskripsi == null) {
            throw new ValidationException(ValidationErrorCode.NULL_VALUE, "Deskripsi kosan tidak boleh null");
        }
        if (deskripsi.trim().isEmpty()) {
            throw new ValidationException(ValidationErrorCode.EMPTY_VALUE, "Deskripsi kosan tidak boleh kosong");
        }
        this.deskripsi = deskripsi;
    }
    public void setJumlahKamar(int jumlahKamar) {
        if (jumlahKamar == 0) {
            throw new ValidationException(ValidationErrorCode.ZERO_VALUE, "jumlah kamar tidak boleh nol");
        }
        if (jumlahKamar < 0) {
            throw new ValidationException(ValidationErrorCode.NEGATIVE_VALUE, "jumlah kamar tidak boleh negatif");
        }
        this.jumlahKamar = jumlahKamar;
    }
    public void setHargaPerBulan(int hargaPerBulan) {
        if (hargaPerBulan == 0) {
            throw new ValidationException(ValidationErrorCode.ZERO_VALUE, "Harga per bulan tidak boleh nol");
        }
        if (hargaPerBulan < 0) {
            throw new ValidationException(ValidationErrorCode.NEGATIVE_VALUE, "Harga per bulan tidak boleh negatif");
        }
        this.hargaPerBulan = hargaPerBulan;
    }
}
