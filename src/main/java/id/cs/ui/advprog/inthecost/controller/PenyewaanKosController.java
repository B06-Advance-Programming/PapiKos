package id.cs.ui.advprog.inthecost.controller;

import id.cs.ui.advprog.inthecost.builder.PenyewaanKosBuilder;
import id.cs.ui.advprog.inthecost.dto.PenyewaanKosDto;
import id.cs.ui.advprog.inthecost.dto.PenyewaanKosRequestDto;
import id.cs.ui.advprog.inthecost.enums.StatusPenyewaan;
import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.PenyewaanKos;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.repository.KostRepository;
import id.cs.ui.advprog.inthecost.service.PenyewaanKosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/penyewaan")
public class PenyewaanKosController {

    private final PenyewaanKosService service;
    private final KostRepository kosRepository;

    @Autowired
    public PenyewaanKosController(PenyewaanKosService service, KostRepository kosRepository) {
        this.service = service;
        this.kosRepository = kosRepository;
    }

    @PostMapping
    public PenyewaanKosDto create(@RequestBody PenyewaanKosRequestDto dto) throws ExecutionException, InterruptedException {
        Kost kost = kosRepository.findById(dto.getKostId())
                .orElseThrow(() -> new RuntimeException("Kost not found"));

        PenyewaanKos penyewaan = PenyewaanKosBuilder.builder()
                .namaLengkap(dto.getNamaLengkap())
                .nomorTelepon(dto.getNomorTelepon())
                .tanggalCheckIn(dto.getTanggalCheckIn())
                .durasiBulan(dto.getDurasiBulan())
                .kos(kost)
                .userId(dto.getUserId())
                .build();

        PenyewaanKos created = service.create(penyewaan).get();

        return new PenyewaanKosDto(
                created.getId(),
                created.getNamaLengkap(),
                created.getNomorTelepon(),
                created.getTanggalCheckIn(),
                created.getDurasiBulan(),
                created.getStatus(),
                created.getUserId(),
                created.getKos().getNama()
        );
    }


    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<PenyewaanKosDto> getAll() {
        return service.findAll().stream()
                .map(p -> new PenyewaanKosDto(
                        p.getId(),
                        p.getNamaLengkap(),
                        p.getNomorTelepon(),
                        p.getTanggalCheckIn(),
                        p.getDurasiBulan(),
                        p.getStatus(),
                        p.getUserId(),
                        p.getKos().getNama()
                ))
                .toList();
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'PENYEWA')")
    public List<PenyewaanKosDto> getAllByPenyewa() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        UUID userId = currentUser.getId();

        return service.findAll().stream()
                .filter(p -> p.getUserId().equals(userId))
                .map(p -> new PenyewaanKosDto(
                        p.getId(),
                        p.getNamaLengkap(),
                        p.getNomorTelepon(),
                        p.getTanggalCheckIn(),
                        p.getDurasiBulan(),
                        p.getStatus(),
                        p.getUserId(),
                        p.getKos().getNama()
                ))
                .toList();
    }

    @GetMapping("/pemilik")
    @PreAuthorize("hasAnyRole('ADMIN', 'PEMILIK')")
    public List<PenyewaanKosDto> getAllByPemilik() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        UUID ownerId = currentUser.getId();

        return service.findAll().stream()
                .filter(p -> p.getKos().getOwnerId().equals(ownerId))
                .map(p -> new PenyewaanKosDto(
                        p.getId(),
                        p.getNamaLengkap(),
                        p.getNomorTelepon(),
                        p.getTanggalCheckIn(),
                        p.getDurasiBulan(),
                        p.getStatus(),
                        p.getUserId(),
                        p.getKos().getNama()
                ))
                .toList();
    }

    @PutMapping
    public PenyewaanKosDto update(@RequestBody PenyewaanKosRequestDto dto) throws ExecutionException, InterruptedException {
        Kost kost = kosRepository.findById(dto.getKostId())
                .orElseThrow(() -> new RuntimeException("Kost not found"));

        PenyewaanKos penyewaan = PenyewaanKosBuilder.builder()
                .id(dto.getId())
                .namaLengkap(dto.getNamaLengkap())
                .nomorTelepon(dto.getNomorTelepon())
                .tanggalCheckIn(dto.getTanggalCheckIn())
                .durasiBulan(dto.getDurasiBulan())
                .kos(kost)
                .userId(dto.getUserId())
                .status(StatusPenyewaan.valueOf(dto.getStatus()))
                .build();

        PenyewaanKos updated = service.update(penyewaan).get();

        return new PenyewaanKosDto(
                updated.getId(),
                updated.getNamaLengkap(),
                updated.getNomorTelepon(),
                updated.getTanggalCheckIn(),
                updated.getDurasiBulan(),
                updated.getStatus(),
                updated.getUserId(),
                updated.getKos().getNama()
        );
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
