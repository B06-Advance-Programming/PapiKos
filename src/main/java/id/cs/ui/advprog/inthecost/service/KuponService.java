package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.model.Kupon;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface KuponService {
    Kupon createKupon(Kupon kupon);

    Kupon updateKupon(
            UUID idKupon,
            List<UUID> kostIdList,
            int persentase,
            String namaKupon,
            LocalDate masaBerlaku,
            String deskripsi,
            int quantity
    );

    void deleteKupon(UUID id);

    CompletableFuture<Kupon> getKuponById(UUID id);
    CompletableFuture<Kupon> getKuponByKodeUnik(String kodeUnik);
    CompletableFuture<List<Kupon>> getAllKupon();
    CompletableFuture<List<Kupon>> findByKostId(UUID kostId);
}