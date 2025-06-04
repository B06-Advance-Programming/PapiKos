package id.cs.ui.advprog.inthecost.controller;

import id.cs.ui.advprog.inthecost.exception.ValidationErrorCode;
import id.cs.ui.advprog.inthecost.exception.ValidationException;
import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.Kupon;
import id.cs.ui.advprog.inthecost.service.KuponService;
import id.cs.ui.advprog.inthecost.repository.KostRepository;

import id.cs.ui.advprog.inthecost.dto.KuponRequest;
import id.cs.ui.advprog.inthecost.dto.KuponResponse;
import jakarta.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/kupon")
public class KuponController{
    private static final Logger logger = LoggerFactory.getLogger(KuponController.class);

    private final KuponService kuponService;
    private final KostRepository kostRepository;

    public KuponController(KuponService kuponService, KostRepository kostRepository){
        this.kuponService = kuponService;
        this.kostRepository = kostRepository;
    }

    @GetMapping
    public ResponseEntity<List<KuponResponse>> getAllKupon() {
        List<Kupon> kupons = kuponService.getAllKupon().join();
        logger.debug("kupons size: {}", kupons.size());
        kupons.forEach(k -> logger.debug("Kupon: {}", k.getNamaKupon()));
        if (kupons.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<KuponResponse> responses = kupons.stream()
                .map(this::mapToResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }


    @GetMapping("/{id}")
    public ResponseEntity<KuponResponse> getKuponById(@PathVariable UUID id) {
        try{
            Kupon kupon = kuponService.getKuponById(id).join();
            return ResponseEntity.ok(mapToResponse(kupon));
        }catch (ValidationException e) {
            return ResponseEntity.notFound().build();
        }catch (Exception ex){
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PEMILIK') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteKupon(@PathVariable UUID id) {
        try {
            kuponService.getKuponById(id);
            kuponService.deleteKupon(id);
            return ResponseEntity.noContent().build();
        }catch (ValidationException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PEMILIK') or hasRole('ADMIN')")
    public ResponseEntity<KuponResponse> updateKupon(@PathVariable UUID id, @RequestBody KuponRequest request) {
        try {
            kuponService.updateKupon(
                    id,
                    request.getKosPemilik(),
                    request.getPersentase(),
                    request.getNamaKupon(),
                    request.getMasaBerlaku(),
                    request.getDeskripsi(),
                    request.getQuantity()
            );
            Kupon updatedKupon = kuponService.getKuponById(id).join();
            return ResponseEntity.ok(mapToResponse(updatedKupon));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('PEMILIK') or hasRole('ADMIN')")
    public ResponseEntity<KuponResponse> createKupon(@RequestBody KuponRequest request) {
        if (request.getPersentase() < 0 || request.getPersentase() > 100
                || request.getKosPemilik() == null || request.getKosPemilik().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<Kost> kostList = kostRepository.findAllById(request.getKosPemilik());
        if (kostList.size() != request.getKosPemilik().size()) {
            return ResponseEntity.badRequest().build();
        }

        Kupon kupon = new Kupon(
                kostList,
                request.getNamaKupon(),
                request.getMasaBerlaku(),
                request.getPersentase(),
                request.getDeskripsi(),
                request.getQuantity()
        );

        Kupon savedKupon = kuponService.createKupon(kupon);
        return ResponseEntity.ok(mapToResponse(savedKupon));
    }

    @GetMapping("/kost/{kostId}")
    public ResponseEntity<List<KuponResponse>> getKuponsByKost(@PathVariable UUID kostId) {
        List<Kupon> kupons = kuponService.findByKostId(kostId).join();
        if (kupons.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<KuponResponse> responses = kupons.stream()
                .map(this::mapToResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/owner/{ownerId}")
    @PreAuthorize("hasRole('PEMILIK') or hasRole('ADMIN')")
    public ResponseEntity<List<KuponResponse>> getKuponsByOwnerId(@PathVariable UUID ownerId) {
        List<Kost> kosts = kostRepository.findByOwnerId(ownerId);
        List<UUID> kostIds = kosts.stream().map(Kost::getKostID).toList();

        List<Kupon> allKupons = kuponService.getAllKupon().join();

        List<Kupon> filteredKupons = allKupons.stream()
                .filter(kupon -> kupon.getKosPemilik().stream()
                        .anyMatch(k -> kostIds.contains(k.getKostID())))
                .toList();

        if (filteredKupons.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<KuponResponse> responses = filteredKupons.stream()
                .map(this::mapToResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/kode/{kodeUnik}")
    public ResponseEntity<KuponResponse> getKuponByKodeUnik(@PathVariable String kodeUnik) {
        try {
            Kupon kupon = kuponService.getKuponByKodeUnik(kodeUnik).join();
            return ResponseEntity.ok(mapToResponse(kupon));
        } catch (CompletionException e) {
            if (e.getCause() instanceof ValidationException) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.internalServerError().build();
        }
    }

    private KuponResponse mapToResponse(Kupon kupon) {
        KuponResponse response = new KuponResponse();
        response.setIdKupon(kupon.getIdKupon());
        response.setKodeUnik(kupon.getKodeUnik());
        response.setPersentase(kupon.getPersentase());
        response.setNamaKupon(kupon.getNamaKupon());
        response.setMasaBerlaku(kupon.getMasaBerlaku());
        response.setDeskripsi(kupon.getDeskripsi());
        response.setStatusKupon(kupon.getStatusKupon().getValue());
        response.setQuantity(kupon.getQuantity());
        List<KuponResponse.KostInfo> kostInfoList = kupon.getKosPemilik().stream()
                .map(kost -> new KuponResponse.KostInfo(kost.getKostID(), kost.getNama()))
                .toList();
        response.setKosPemilik(kostInfoList);
        return response;
    }
}