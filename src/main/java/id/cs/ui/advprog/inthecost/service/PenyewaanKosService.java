package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.model.PenyewaanKos;

import java.util.List;

public interface PenyewaanKosService {
    PenyewaanKos create(PenyewaanKos penyewaan);
    PenyewaanKos update(PenyewaanKos penyewaan);
    void delete(Long id);
    PenyewaanKos findById(Long id);
    List<PenyewaanKos> findAll();
}
