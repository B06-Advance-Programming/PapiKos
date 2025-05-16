package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.enums.StatusPenyewaan;
import id.cs.ui.advprog.inthecost.model.PenyewaanKos;
import id.cs.ui.advprog.inthecost.repository.PenyewaanKosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

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
        penyewaan.setStatus(StatusPenyewaan.DIAJUKAN);
        return repository.save(penyewaan);
    }

    @Override
    public PenyewaanKos update(PenyewaanKos penyewaan) {
        PenyewaanKos existing = findById(penyewaan.getId());

        if (existing.getStatus() != StatusPenyewaan.DIAJUKAN) {
            throw new IllegalStateException("Penyewaan hanya bisa diubah jika masih berstatus DIAJUKAN.");
        }

        return repository.save(penyewaan);
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
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
}
