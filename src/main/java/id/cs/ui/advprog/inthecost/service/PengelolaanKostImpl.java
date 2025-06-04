package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.exception.ValidationErrorCode;
import id.cs.ui.advprog.inthecost.exception.ValidationException;
import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.observer.WishlistObserver;
import id.cs.ui.advprog.inthecost.repository.KostRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class PengelolaanKostImpl implements PengelolaanKost {

    private final KostRepository kostRepository;
    private final NotificationService notificationService;

    @Autowired
    public PengelolaanKostImpl(KostRepository kostRepository, NotificationService notificationService) {
        this.kostRepository = kostRepository;
        this.notificationService = notificationService;
    }

    @Override @Async
    public CompletableFuture<Void> addKost(Kost kost) {
        try {
            kostRepository.save(kost);
            return CompletableFuture.completedFuture(null);
        } catch (ValidationException e) {
            CompletableFuture<Void> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(e);
            return failedFuture;
        }
    }

    // Mengambil daftar semua kost
    @Override @Async
    public CompletableFuture<List<Kost>> getAllKost() {
        return CompletableFuture.completedFuture(kostRepository.findAll());
    }

    @Override @Async
    public CompletableFuture<List<Kost>> getKostByOwnerId(UUID ownerId) {
        List<Kost> kostList = kostRepository.findByOwnerId(ownerId);
        return CompletableFuture.completedFuture(kostList);
    }

    @Override @Async
    public CompletableFuture<Void> updateKostByID(UUID kostId, Kost kost) {
        try {
            Optional<Kost> existingKost = kostRepository.findById(kostId);

            if (existingKost.isEmpty()) {
                throw new ValidationException(ValidationErrorCode.INVALID_ID, "ID Kost tidak ditemukan.");
            }            Kost kostToUpdate = existingKost.get();
            
            // Clear any existing observers and register fresh observer for each update
            kostToUpdate.clearObservers();
            kostToUpdate.addObserver(new WishlistObserver(notificationService));
            
            System.out.println("🔄 UPDATING KOST: '" + kostToUpdate.getNama() + "' from " + kostToUpdate.getJumlahKamar() + " to " + kost.getJumlahKamar() + " rooms");
            
            kostToUpdate.setNama(kost.getNama());
            kostToUpdate.setAlamat(kost.getAlamat());
            kostToUpdate.setDeskripsi(kost.getDeskripsi());
            kostToUpdate.setJumlahKamar(kost.getJumlahKamar());
            kostToUpdate.setHargaPerBulan(kost.getHargaPerBulan());

            kostRepository.save(kostToUpdate);

            return CompletableFuture.completedFuture(null);
        } catch (ValidationException e) {
            CompletableFuture<Void> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(e);
            return failedFuture;
        }
    }

    // Menghapus kost berdasarkan ID
    @Override @Async
    public CompletableFuture<Void> deleteKost(UUID kostId) {
        try {
            if (!kostRepository.existsById(kostId)) {
                throw new ValidationException(ValidationErrorCode.INVALID_ID, "ID Kost tidak ditemukan.");
            }
            kostRepository.deleteById(kostId);
            return CompletableFuture.completedFuture(null);
        } catch (ValidationException e) {
            CompletableFuture<Void> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(e);
            return failedFuture;
        }
        }
}
