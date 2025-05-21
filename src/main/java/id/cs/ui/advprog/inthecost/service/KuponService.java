package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.model.Kupon;
import id.cs.ui.advprog.inthecost.model.Kost;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface KuponService {
    Kupon createKupon(Kupon kupon);
    Kupon updateKupon(UUID idKupon, List<UUID> kostIds, int persentase, String namaKupon,LocalDate masaBerlaku, String deskripsi, int quantity);
    Kupon getKuponById(UUID id);
    Kupon getKuponByKodeUnik(String kodeUnik);
    List<Kupon> getKuponByKost(Kost kos);
    void deleteKupon(UUID id);
    List<Kupon> getAllKupon();
}
