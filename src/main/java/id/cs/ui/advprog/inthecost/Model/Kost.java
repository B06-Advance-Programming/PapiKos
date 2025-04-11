package id.cs.ui.advprog.inthecost.Model;
import id.cs.ui.advprog.inthecost.Exception.*;

import lombok.Getter;

import java.util.UUID;

@Getter
public class Kost {
    private UUID kostID;
    private String nama;
    private String alamat;
    private String deskripsi;
    private int jumlahKamar;
    private int hargaPerBulan;

    // manual set each times
    public Kost() {
        kostID = UUID.randomUUID();
    }

    // constructor based
    public Kost(String nama, String alamat, String deskripsi, int jumlahKamar, int hargaPerBulan) {
        // handle null atau kosong
        if (nama == null || nama.trim().isEmpty()) {
            throw new ValidationException(ValidationErrorCode.NULL_OR_EMPTY_VALUE, "Nama kosan tidak boleh kosong");
        }
        if (alamat == null || alamat.trim().isEmpty()) {
            throw new ValidationException(ValidationErrorCode.NULL_OR_EMPTY_VALUE, "Alamat kosan tidak boleh kosong");
        }
        if (deskripsi == null || deskripsi.trim().isEmpty()) {
            throw new ValidationException(ValidationErrorCode.NULL_OR_EMPTY_VALUE, "Deskripsi kosan tidak boleh kosong");
        }

        // handle integer value yang tidak diperbolehkan
        if (jumlahKamar < 0) {
            throw new ValidationException(ValidationErrorCode.NEGATIVE_VALUE, "Jumlah kamar tidak boleh negatif");
        }
        if (hargaPerBulan <= 0) {
            throw new ValidationException(ValidationErrorCode.ZERO_OR_NEGATIVE_VALUE, "Harga per bulan harus lebih besar dari 0");
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
        this.jumlahKamar = jumlahKamar;
    }
    public void setHargaPerBulan(int hargaPerBulan) {
        if (hargaPerBulan <= 0) {
            throw new ValidationException(ValidationErrorCode.ZERO_OR_NEGATIVE_VALUE, "Harga per bulan harus lebih besar dari 0");
        }
        this.hargaPerBulan = hargaPerBulan;
    }
}
