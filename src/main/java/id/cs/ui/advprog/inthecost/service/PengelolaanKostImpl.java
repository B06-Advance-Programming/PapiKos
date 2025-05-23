package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.exception.ValidationErrorCode;
import id.cs.ui.advprog.inthecost.exception.ValidationException;
import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.repository.KostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PengelolaanKostImpl implements PengelolaanKost {

    @Autowired
    private KostRepository kostRepository;

    @Override
    public void addKost(Kost kost) {
        try {
            kostRepository.save(kost);
        } catch (ValidationException e) {
            throw e;
        }
    }

    // Mengambil daftar semua kost
    @Override
    public List<Kost> getAllKost() {
        return kostRepository.findAll();
    }

    @Override
    public void updateKostByID(UUID kostId, Kost kost) {
        Optional<Kost> existingKost = kostRepository.findById(kostId);

        if (existingKost.isEmpty()) {
            throw new ValidationException(ValidationErrorCode.INVALID_ID, "ID Kost tidak ditemukan.");
        }

        // Update data kost yang ada
        Kost kostToUpdate = existingKost.get();
        kostToUpdate.setNama(kost.getNama());
        kostToUpdate.setAlamat(kost.getAlamat());
        kostToUpdate.setDeskripsi(kost.getDeskripsi());
        kostToUpdate.setJumlahKamar(kost.getJumlahKamar());
        kostToUpdate.setHargaPerBulan(kost.getHargaPerBulan());

        kostRepository.save(kostToUpdate);
    }

    // Menghapus kost berdasarkan ID
    @Override
    public void deleteKost(UUID kostId) {
        if (!kostRepository.existsById(kostId)) {
            throw new ValidationException(ValidationErrorCode.INVALID_ID, "ID Kost tidak ditemukan.");
        }
        kostRepository.deleteById(kostId);
    }
}
