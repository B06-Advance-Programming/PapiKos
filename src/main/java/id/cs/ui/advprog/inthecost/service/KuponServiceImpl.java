package id.cs.ui.advprog.inthecost.service;
import id.cs.ui.advprog.inthecost.model.Kupon;
import id.cs.ui.advprog.inthecost.repository.KuponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class KuponServiceImpl implements KuponService {

    @Autowired
    KuponRepository kuponRepository;

    @Override
    public Kupon createKupon(Kupon kupon){
        return kuponRepository.save(kupon);
    }

    @Override
    public Kupon updateKupon(Kupon kupon) {
        Optional<Kupon> existingKupon = kuponRepository.findById(kupon.getIdKupon());
        if (existingKupon.isPresent()) {
            Kupon updated = existingKupon.get();
            updated.setPemilik(kupon.getPemilik());
            updated.setPersentase(kupon.getPersentase());
            updated.setMasaBerlaku(kupon.getMasaBerlaku());
            updated.setDeskripsi(kupon.getDeskripsi());
            updated.setKosPemilik(kupon.getKosPemilik());
            return kuponRepository.save(updated);
        } else {
            throw new IllegalArgumentException("Kupon dengan ID " + kupon.getIdKupon() + " tidak ditemukan.");
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