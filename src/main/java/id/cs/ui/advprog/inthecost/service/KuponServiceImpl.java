package id.cs.ui.advprog.inthecost.service;
import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.Kupon;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.repository.KostRepository;
import id.cs.ui.advprog.inthecost.repository.KuponRepository;
import id.cs.ui.advprog.inthecost.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class KuponServiceImpl implements KuponService {

    @Autowired
    KuponRepository kuponRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    KostRepository kostRepository;

    @Override
    public Kupon createKupon(Kupon kupon){
        return kuponRepository.save(kupon);
    }

    public Kupon updateKupon(UUID idKupon, UUID pemilikId, List<UUID> kostIds, int persentase, String namaKupon,
                             LocalDate masaBerlaku, String deskripsi) {
        if (idKupon == null) {
            throw new IllegalArgumentException("Kupon ID cannot be null");
        }
        if (pemilikId == null) {
            throw new IllegalArgumentException("Pemilik ID cannot be null");
        }
        if (kostIds == null || kostIds.isEmpty()) {
            throw new IllegalArgumentException("Kost IDs cannot be null or empty");
        }
        if (persentase <= 0 || persentase > 100) {
            throw new IllegalArgumentException("Persentase must be between 1 and 100");
        }
        if (masaBerlaku == null || masaBerlaku.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Masa berlaku cannot be in the past");
        }
        if (deskripsi == null || deskripsi.trim().isEmpty()) {
            throw new IllegalArgumentException("Deskripsi cannot be empty");
        }

        Kupon kupon = kuponRepository.findById(idKupon)
                .orElseThrow(() -> new EntityNotFoundException("Kupon not found with ID: " + idKupon));

        User pemilik = userRepository.findById(pemilikId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + pemilikId));

        List<Kost> kosList = kostRepository.findAllById(kostIds);
        if (kosList.size() != kostIds.size()) {
            throw new EntityNotFoundException("Some Kost not found with");
        }

        kupon.setPemilik(pemilik);
        kupon.setKosPemilik(kosList);
        kupon.setPersentase(persentase);
        kupon.setNamaKupon(namaKupon);
        kupon.setMasaBerlaku(masaBerlaku);
        kupon.setDeskripsi(deskripsi.trim());

        return kuponRepository.save(kupon);
    }

    @Override
    public Kupon getKuponById(UUID id) {
        return kuponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kupon dengan ID " + id + " tidak ditemukan."));
    }

    @Override
    public Kupon getKuponByKodeUnik(String kodeUnik){
        return kuponRepository.findByKodeUnik(kodeUnik)
                .orElseThrow(() -> new RuntimeException("Kupon dengan Kode unik " + kodeUnik + " tidak ditemukan."));
    }

    @Override
    public void deleteKupon(UUID id) {
        if (kuponRepository.existsById(id)) {
            kuponRepository.deleteById(id);
        } else {
            throw new RuntimeException("Kupon dengan ID " + id + " tidak ditemukan.");
        }
    }

    @Override
    public List<Kupon> getAllKupon() {
        return kuponRepository.findAll();
    }
}