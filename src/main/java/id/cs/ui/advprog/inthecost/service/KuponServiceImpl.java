package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.Kupon;
import id.cs.ui.advprog.inthecost.repository.KostRepository;
import id.cs.ui.advprog.inthecost.repository.KuponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class KuponServiceImpl implements KuponService {

    @Autowired
    KuponRepository kuponRepository;

    @Autowired
    KostRepository kostRepository;

    @Override
    public Kupon createKupon(Kupon kupon) {
        return kuponRepository.save(kupon);
    }

    @Override
    public Kupon updateKupon(Kupon kupon) {
        Optional<Kupon> existingKupon = kuponRepository.findById(kupon.getIdKupon());
        if (existingKupon.isPresent()) {
            Kupon updated = existingKupon.get();
            updated.setPersentase(kupon.getPersentase());
            updated.setMasaBerlaku(kupon.getMasaBerlaku());
            updated.setDeskripsi(kupon.getDeskripsi());
            updated.setKosPemilik(kupon.getKosPemilik());
            updated.setNamaKupon(kupon.getNamaKupon());
            updated.setQuantity(kupon.getQuantity());
            return kuponRepository.save(updated);
        } else {
            throw new IllegalArgumentException("Kupon dengan ID " + kupon.getIdKupon() + " tidak ditemukan.");
        }
    }

    @Override
    public Kupon updateKupon(
            UUID idKupon,
            List<UUID> kostIdList,
            int persentase,
            String namaKupon,
            LocalDate masaBerlaku,
            String deskripsi,
            int quantity
    ) {
        Optional<Kupon> existingKupon = kuponRepository.findById(idKupon);
        if (existingKupon.isPresent()) {
            Kupon updated = existingKupon.get();
            List<Kost> kosts = kostRepository.findAllById(kostIdList);
            updated.setKosPemilik(kosts);
            updated.setPersentase(persentase);
            updated.setNamaKupon(namaKupon);
            updated.setMasaBerlaku(masaBerlaku);
            updated.setDeskripsi(deskripsi);
            updated.setQuantity(quantity);
            return kuponRepository.save(updated);
        } else {
            throw new IllegalArgumentException("Kupon dengan ID " + idKupon + " tidak ditemukan.");
        }
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