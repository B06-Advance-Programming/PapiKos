package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.enums.StatusPenyewaan;
import id.cs.ui.advprog.inthecost.model.PenyewaanKos;
import id.cs.ui.advprog.inthecost.repository.PenyewaanKosRepository;

import java.util.List;
import java.util.NoSuchElementException;

public class PenyewaanKosServiceImpl implements PenyewaanKosService {

    private final PenyewaanKosRepository repository;

    public PenyewaanKosServiceImpl(PenyewaanKosRepository repository) {
        this.repository = repository;
    }

    @Override
    public PenyewaanKos create(PenyewaanKos penyewaan) {
        return repository.save(penyewaan);
    }

    @Override
    public PenyewaanKos update(PenyewaanKos penyewaan) {
        return repository.update(penyewaan);
    }

    @Override
    public void delete(Long id) {
        repository.delete(id);
    }

    @Override
    public PenyewaanKos findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Penyewaan tidak ditemukan dengan ID: " + id));
    }

    @Override
    public List<PenyewaanKos> findAll() {
        return repository.findAll();
    }
}
