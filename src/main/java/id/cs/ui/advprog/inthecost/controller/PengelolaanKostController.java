package id.cs.ui.advprog.inthecost.controller;

import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.service.PengelolaanKost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/pengelolaan_kost")
public class PengelolaanKostController {

    @Autowired
    private PengelolaanKost pengelolaanKost;

    // Create Kost
    @PostMapping
    public CompletableFuture<ResponseEntity<String>> addKost(@RequestBody Kost kost) {
        return pengelolaanKost.addKost(kost)
                .thenApply(v -> ResponseEntity.ok("Kost berhasil ditambahkan."));
    }

    // Read All Kost
    @GetMapping
    public CompletableFuture<ResponseEntity<List<Kost>>> getAllKost() {
        return pengelolaanKost.getAllKost()
                .thenApply(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public CompletableFuture<ResponseEntity<String>> updateKost(@PathVariable UUID id, @RequestBody Kost kost) {
        return pengelolaanKost.updateKostByID(id, kost)
                .thenApply(v -> ResponseEntity.ok("Kost berhasil diperbarui."));
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<String>> deleteKost(@PathVariable UUID id) {
        return pengelolaanKost.deleteKost(id)
                .thenApply(v -> ResponseEntity.ok("Kost berhasil dihapus."));
    }

}
