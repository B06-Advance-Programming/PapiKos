package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.model.Kost;

import java.util.List;
import java.util.UUID;

public interface PengelolaanKost {
    void addKost(Kost kost);
    // Cek Kost
    List<Kost> getAllKost();
    void updateKostByID(UUID kostId, Kost kost);
    void deleteKost(UUID kostId);
}
