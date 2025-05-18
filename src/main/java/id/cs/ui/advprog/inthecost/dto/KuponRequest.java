package id.cs.ui.advprog.inthecost.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class KuponRequest {
    private UUID pemilik;
    private List<UUID> kosPemilik;
    private String namaKupon;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate masaBerlaku;

    private int persentase;
    private String deskripsi;

    public UUID getPemilik() { return pemilik; }
    public void setPemilik(UUID pemilik) { this.pemilik = pemilik; }

    public List<UUID> getKosPemilik() { return kosPemilik; }
    public void setKosPemilik(List<UUID> kosPemilik) { this.kosPemilik = kosPemilik; }

    public String getNamaKupon() { return namaKupon; }
    public void setNamaKupon(String namaKupon) { this.namaKupon = namaKupon; }

    public LocalDate getMasaBerlaku() { return masaBerlaku; }
    public void setMasaBerlaku(LocalDate masaBerlaku) { this.masaBerlaku = masaBerlaku; }

    public int getPersentase() { return persentase; }
    public void setPersentase(int persentase) { this.persentase = persentase; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
}
