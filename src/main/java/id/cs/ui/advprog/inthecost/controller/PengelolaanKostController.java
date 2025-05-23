package id.cs.ui.advprog.inthecost.controller;

import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.service.PengelolaanKost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pengelolaan_kost")
public class PengelolaanKostController {

    @Autowired
    private PengelolaanKost pengelolaanKost;

    // Create Kost
    @PostMapping
    public ResponseEntity<String> addKost(@RequestBody Kost kost) {
        pengelolaanKost.addKost(kost);
        return ResponseEntity.ok("Kost berhasil ditambahkan.");
    }

    // Read All Kost
    @GetMapping
    public ResponseEntity<List<Kost>> getAllKost() {
        List<Kost> kostList = pengelolaanKost.getAllKost();
        return ResponseEntity.ok(kostList);
    }

    // Update Kost by ID
    @PutMapping("/{id}")
    public ResponseEntity<String> updateKost(@PathVariable UUID id, @RequestBody Kost kost) {
        pengelolaanKost.updateKostByID(id, kost);
        return ResponseEntity.ok("Kost berhasil diperbarui.");
    }

    // Delete Kost by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteKost(@PathVariable UUID id) {
        pengelolaanKost.deleteKost(id);
        return ResponseEntity.ok("Kost berhasil dihapus.");
    }
}
