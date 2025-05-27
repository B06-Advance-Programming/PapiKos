package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.exception.ValidationErrorCode;
import id.cs.ui.advprog.inthecost.exception.ValidationException;
import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.Kupon;
import id.cs.ui.advprog.inthecost.repository.KostRepository;
import id.cs.ui.advprog.inthecost.repository.KuponRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class KuponServiceImpl implements KuponService {

    private final KuponRepository kuponRepository;
    private final KostRepository kostRepository;

    public KuponServiceImpl(KuponRepository kuponRepository, KostRepository kostRepository) {
        this.kuponRepository = kuponRepository;
        this.kostRepository = kostRepository;
    }

    @Override
    public Kupon createKupon(Kupon kupon) {
        return kuponRepository.save(kupon);
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

    @Async
    @Override
    public CompletableFuture<Kupon> getKuponById(UUID id) {
        Kupon kupon = kuponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kupon dengan ID " + id + " tidak ditemukan."));
        return CompletableFuture.completedFuture(kupon);
    }

    @Async
    @Override
    public CompletableFuture<Kupon> getKuponByKodeUnik(String kodeUnik){
        Kupon kupon = kuponRepository.findByKodeUnik(kodeUnik)
                .orElseThrow(() -> new RuntimeException("Kupon dengan Kode unik " + kodeUnik + " tidak ditemukan."));
        return CompletableFuture.completedFuture(kupon);
    }

    @Override
    public void deleteKupon(UUID id) {
        if (kuponRepository.existsById(id)) {
            kuponRepository.deleteById(id);
        } else {
            throw new ValidationException(ValidationErrorCode.INVALID_ID);
        }
    }

    @Async
    @Override
    public CompletableFuture<List<Kupon>> getAllKupon() {
        List<Kupon> kupons = kuponRepository.findAll();
        return CompletableFuture.completedFuture(kupons);
    }

    @Async
    @Override
    public CompletableFuture<List<Kupon>> findByKostId(UUID kostId) {
        List<Kupon> kupons = kuponRepository.findByKostId(kostId);
        return CompletableFuture.completedFuture(kupons);
    }
}