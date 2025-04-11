package id.cs.ui.advprog.inthecost.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KosSewa {
    private Long id;
    private String nama;
    private String alamat;
    private String deskripsi;
    private int jumlahKamar;
    private int hargaSewaBulanan;
}
