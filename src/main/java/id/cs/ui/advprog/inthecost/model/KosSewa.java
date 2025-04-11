package id.cs.ui.advprog.inthecost.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KosSewa {
    // Ini dummy model untuk Kos, nanti model ini akan diganti menunggu
    // PengelolaanKos selesai
    private Long id;
    private String nama;
    private String alamat;
    private String deskripsi;
    private int jumlahKamar;
    private int hargaSewaBulanan;
}
