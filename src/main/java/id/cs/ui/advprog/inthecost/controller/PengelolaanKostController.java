package id.cs.ui.advprog.inthecost.controller;

import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.service.PengelolaanKost;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/pengelolaan_kost")
public class PengelolaanKostController {

    private final PengelolaanKost pengelolaanKost;

    public PengelolaanKostController(PengelolaanKost pengelolaanKost) {
        this.pengelolaanKost = pengelolaanKost;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('PEMILIK', 'ADMIN')")
    public CompletableFuture<ResponseEntity<String>> addKost(@RequestBody Kost kost) {
        return pengelolaanKost.addKost(kost)
                .thenApply(v -> ResponseEntity.ok("Kost berhasil ditambahkan."));
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<List<Kost>>> getAllKost() {
        return pengelolaanKost.getAllKost()
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/{ownerId}")
    public CompletableFuture<ResponseEntity<List<Kost>>> getKostByOwnerId(@PathVariable UUID ownerId) {
        return pengelolaanKost.getKostByOwnerId(ownerId)
                .thenApply(ResponseEntity::ok);
    }

    @PreAuthorize("hasAnyRole('PEMILIK', 'ADMIN')")
    @PutMapping("/{id}")
    public CompletableFuture<ResponseEntity<String>> updateKost(@PathVariable UUID id, @RequestBody Kost kost) {
        return pengelolaanKost.updateKostByID(id, kost)
                .thenApply(v -> ResponseEntity.ok("Kost berhasil diperbarui."));
    }

    @PreAuthorize("hasAnyRole('PEMILIK', 'ADMIN')")
    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<String>> deleteKost(@PathVariable UUID id) {
        return pengelolaanKost.deleteKost(id)
                .thenApply(v -> ResponseEntity.ok("Kost berhasil dihapus."));
    }
}