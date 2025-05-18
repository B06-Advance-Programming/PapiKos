package id.cs.ui.advprog.inthecost.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class KuponResponse {
    private UUID idKupon;
    private String pemilik;
    private String kodeUnik;
    private int persentase;
    private String namaKupon;
    private LocalDate masaBerlaku;
    private String deskripsi;
    private String statusKupon;
    private List<String> kosPemilik;

    public UUID getIdKupon() { return idKupon; }
    public void setIdKupon(UUID idKupon) { this.idKupon = idKupon; }

    public String getPemilik() { return pemilik; }
    public void setPemilik(String pemilik) { this.pemilik = pemilik; }

    public String getKodeUnik() { return kodeUnik; }
    public void setKodeUnik(String kodeUnik) { this.kodeUnik = kodeUnik; }

    public int getPersentase() { return persentase; }
    public void setPersentase(int persentase) { this.persentase = persentase; }

    public String getNamaKupon() { return namaKupon; }
    public void setNamaKupon(String namaKupon) { this.namaKupon = namaKupon; }

    public LocalDate getMasaBerlaku() { return masaBerlaku; }
    public void setMasaBerlaku(LocalDate masaBerlaku) { this.masaBerlaku = masaBerlaku; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public String getStatusKupon() { return statusKupon; }
    public void setStatusKupon(String statusKupon) { this.statusKupon = statusKupon; }

    public List<String> getKosPemilik() { return kosPemilik; }
    public void setKosPemilik(List<String> kosPemilik) { this.kosPemilik = kosPemilik; }
}
