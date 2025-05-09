package id.cs.ui.advprog.inthecost.repository;

import id.cs.ui.advprog.inthecost.enums.StatusPenyewaan;
import id.cs.ui.advprog.inthecost.model.PenyewaanKos;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class PenyewaanKosRepository {

    private final Map<Long, PenyewaanKos> storage = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    // Create
    public PenyewaanKos save(PenyewaanKos penyewaan) {
        Long id = idCounter.getAndIncrement();
        penyewaan.setId(id);
        penyewaan.setStatus(StatusPenyewaan.DIAJUKAN);  // default status
        storage.put(id, penyewaan);
        return penyewaan;
    }

    // Read All
    public List<PenyewaanKos> findAll() {
        return new ArrayList<>(storage.values());
    }

    // Read by ID
    public Optional<PenyewaanKos> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    // Update
    public PenyewaanKos update(PenyewaanKos penyewaan) {
        if (penyewaan.getId() == null || !storage.containsKey(penyewaan.getId())) {
            throw new NoSuchElementException("Penyewaan dengan ID ini tidak ditemukan.");
        }
        if (penyewaan.getStatus() != StatusPenyewaan.DIAJUKAN) {
            throw new IllegalStateException("Penyewaan hanya bisa diubah jika masih berstatus DIAJUKAN.");
        }

        storage.put(penyewaan.getId(), penyewaan);
        return penyewaan;
    }

    // Delete
    public void delete(Long id) {
        storage.remove(id);
    }
}
