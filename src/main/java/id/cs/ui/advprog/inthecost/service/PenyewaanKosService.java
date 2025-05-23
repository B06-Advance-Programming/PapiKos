package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.model.PenyewaanKos;
import id.cs.ui.advprog.inthecost.enums.StatusPenyewaan;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PenyewaanKosService {
    PenyewaanKos create(PenyewaanKos penyewaan);
    PenyewaanKos update(PenyewaanKos penyewaan);
    CompletableFuture<Void> delete(UUID id);
    PenyewaanKos findById(UUID id);
    List<PenyewaanKos> findAll();
    boolean hasPendingPenyewaan(UUID userId, UUID kostId);
}