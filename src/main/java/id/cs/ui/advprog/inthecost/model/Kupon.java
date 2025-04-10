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
    }

}