package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.model.Kupon;
import id.cs.ui.advprog.inthecost.repository.KuponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class KuponServiceImpl implements KuponService {
    @Autowired
    private KuponRepository kuponRepository;

    @Override
    public Kupon createKupon(Kupon kupon) {
        if (kuponRepository.findByKodeUnik(kupon.getKodeUnik()) != null) {
            throw new IllegalArgumentException("Kode unik kupon sudah digunakan.");
        }
        return kuponRepository.save(kupon);
    }

    @Override
    public Kupon updateKupon(Kupon kupon){
        if (kuponRepository.findById(kupon.getIdKupon()) == null) {
            throw new NoSuchElementException("Kupon tidak ditemukan.");
        }
        return kuponRepository.save(kupon);
    }

    @Override
    public Kupon getKuponById(String id) {
        Kupon kupon = kuponRepository.findById(id);
        if (kupon == null) throw new NoSuchElementException("Kupon tidak ditemukan.");
        return kupon;
    }

    @Override
    public Kupon getKuponByKodeUnik(String kodeUnik) {
        Kupon kupon = kuponRepository.findByKodeUnik(kodeUnik);
        if (kupon == null) throw new NoSuchElementException("Kupon tidak ditemukan.");
        return kupon;
    }

    @Override
    public void deleteKupon(String id) {
        boolean success = kuponRepository.deleteById(id);
        if (!success) {
            throw new NoSuchElementException("Kupon tidak ditemukan.");
        }
    }

    @Override
    public List<Kupon> getAllKupon() {
        return kuponRepository.findAll();
    }
}