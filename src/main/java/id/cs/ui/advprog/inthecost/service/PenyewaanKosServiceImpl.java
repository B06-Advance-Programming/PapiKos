package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.enums.StatusPenyewaan;
import id.cs.ui.advprog.inthecost.model.PenyewaanKos;
import id.cs.ui.advprog.inthecost.repository.PenyewaanKosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class PenyewaanKosServiceImpl implements PenyewaanKosService {

    private final PenyewaanKosRepository repository;

    @Autowired
    public PenyewaanKosServiceImpl(PenyewaanKosRepository repository) {
        this.repository = repository;
    }

    @Override
    public PenyewaanKos create(PenyewaanKos penyewaan) {
        if (penyewaan.getId() == null) {
            penyewaan.setId(UUID.randomUUID());
        }

        // Ambil semua penyewaan lain dengan userId + kostId + status diajukan
        List<PenyewaanKos> existingList = repository.findByKos_KostIDAndUserIdAndStatus(penyewaan.getKos().getKostID(), penyewaan.getUserId(), StatusPenyewaan.DIAJUKAN);

        for (PenyewaanKos existing : existingList) {
            existing.setStatus(StatusPenyewaan.DIBATALKAN);
            repository.save(existing);
        }

        penyewaan.setStatus(StatusPenyewaan.DIAJUKAN);
        return repository.save(penyewaan);
    }

    @Override
    public PenyewaanKos update(PenyewaanKos penyewaan) {
        PenyewaanKos existing = findById(penyewaan.getId());

        if (existing.getStatus() != StatusPenyewaan.DIAJUKAN) {
            throw new IllegalStateException(String.format("Penyewaan hanya bisa diubah jika masih berstatus DIAJUKAN. status sekarang '%s'", penyewaan.getStatus()));
        }

        return repository.save(penyewaan);
    }

    @Async
    @Override
    public CompletableFuture<Void> delete(UUID id) {
        repository.deleteById(id);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public PenyewaanKos findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Penyewaan tidak ditemukan dengan ID: " + id));
    }

    @Override
    public List<PenyewaanKos> findAll() {
        return repository.findAll();
    }

    @Override
    public boolean hasPendingPenyewaan(UUID userId, UUID kostId) {
        List<PenyewaanKos> pendingList = repository.findByKos_KostIDAndUserIdAndStatus(kostId, userId, StatusPenyewaan.DIAJUKAN);
        return !pendingList.isEmpty();
    }

    @Override
    public List<PenyewaanKos> getAllByUserIdAndStatus(UUID userId, StatusPenyewaan status) {
        return repository.findByUserIdAndStatus(userId, status);
    }
}