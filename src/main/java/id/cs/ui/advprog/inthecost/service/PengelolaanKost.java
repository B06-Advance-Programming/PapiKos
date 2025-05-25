package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.model.Kost;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PengelolaanKost {
    CompletableFuture<Void> addKost(Kost kost);
    // Cek Kost
    CompletableFuture<List<Kost>> getAllKost();
    CompletableFuture<List<Kost>> getKostByOwnerId(UUID id);
    CompletableFuture<Void> updateKostByID(UUID kostId, Kost kost);
    CompletableFuture<Void> deleteKost(UUID kostId);
}
