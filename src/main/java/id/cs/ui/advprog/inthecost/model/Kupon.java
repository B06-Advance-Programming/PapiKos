package id.cs.ui.advprog.inthecost.model;

import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class Kupon{
    private String idKupon;
    private String pemilik; //To be updated
    private String kodeUnik;
    private int persentase;
    private LocalDate masaBerlaku;
    private String deskripsi;
    private boolean kuponGlobal;

    public Kupon(String pemilik, LocalDate masaBerlaku, int persentase, String deskripsi, boolean kuponGlobal) {
        this.idKupon = UUID.randomUUID().toString();
        this.pemilik = pemilik;
        this.kodeUnik = generateKodeUnik();
        this.persentase = persentase;
        this.masaBerlaku = masaBerlaku;
        this.deskripsi = deskripsi;
        this.kuponGlobal = kuponGlobal;
    }

    private String generateKodeUnik(){
        return UUID.randomUUID().toString().substring(0,6).toUpperCase();
    }

}