package id.cs.ui.advprog.inthecost.Repository;

import id.cs.ui.advprog.inthecost.enums.StatusPenyewaan;
import id.cs.ui.advprog.inthecost.model.PenyewaanKos;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class PenyewaanKosRepository {

    private final Map<Long, PenyewaanKos> storage = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    // Create
    public PenyewaanKos save(PenyewaanKos penyewaan) {return null;}

    // Read All
    public List<PenyewaanKos> findAll() {return null;}

    // Read by ID
    public Optional<PenyewaanKos> findById(Long id) {
        return null;
    }

    // Update
    public PenyewaanKos update(PenyewaanKos penyewaan) {return null;}

    // Delete
    public void delete(Long id) {
        ;
    }
}
