package id.cs.ui.advprog.inthecost.controller;

import id.cs.ui.advprog.inthecost.model.PenyewaanKos;
import id.cs.ui.advprog.inthecost.service.PenyewaanKosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/penyewaan")
public class PenyewaanKosController {

    private final PenyewaanKosService service;

    @Autowired
    public PenyewaanKosController(PenyewaanKosService service) {
        this.service = service;
    }

    @PostMapping
    public PenyewaanKos create(@RequestBody PenyewaanKos penyewaan) {
        return service.create(penyewaan);
    }

    @GetMapping
    public List<PenyewaanKos> getAll() {
        return service.findAll();
    }

    @PutMapping
    public PenyewaanKos update(@RequestBody PenyewaanKos penyewaan) {
        return service.update(penyewaan);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
