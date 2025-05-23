package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.model.Kupon;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface KuponService {
    Kupon createKupon(Kupon kupon);

    Kupon updateKupon(Kupon kupon);

    Kupon updateKupon(
            UUID idKupon,
            List<UUID> kostIdList,
            int persentase,
            String namaKupon,
            LocalDate masaBerlaku,
            String deskripsi,
            int quantity
    );

    Kupon getKuponById(UUID id);

    Kupon getKuponByKodeUnik(String kodeUnik);

    void deleteKupon(UUID id);

    List<Kupon> getAllKupon();

    List<Kupon> findByKostId(UUID kostId);
}