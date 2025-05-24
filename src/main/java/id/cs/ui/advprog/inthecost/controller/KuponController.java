package id.cs.ui.advprog.inthecost.controller;

import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.Kupon;
import id.cs.ui.advprog.inthecost.service.KuponService;
import id.cs.ui.advprog.inthecost.service.KuponServiceImpl;
import id.cs.ui.advprog.inthecost.repository.UserRepository;
import id.cs.ui.advprog.inthecost.repository.KostRepository;

import id.cs.ui.advprog.inthecost.dto.KuponRequest;
import id.cs.ui.advprog.inthecost.dto.KuponResponse;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/kupon")
public class KuponController{
    @Autowired
    private KuponService kuponService;

    @Autowired
    private KostRepository kostRepository;

    @GetMapping
    public ResponseEntity<List<KuponResponse>> getAllKupon() {
        logCurrentUser();

        List<Kupon> kupons = kuponService.getAllKupon().join();
        System.out.println("[DEBUG] kupons size: " + kupons.size());
        kupons.forEach(k -> System.out.println("[DEBUG] Kupon: " + k.getNamaKupon()));
        if (kupons.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<KuponResponse> responses = kupons.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }


    @GetMapping("/{id}")
    public ResponseEntity<KuponResponse> getKuponById(@PathVariable UUID id) {
        logCurrentUser();
        try{
            Kupon kupon = kuponService.getKuponById(id).join();
            return ResponseEntity.ok(mapToResponse(kupon));
        }catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PEMILIK') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteKupon(@PathVariable UUID id) {
        try {
            kuponService.getKuponById(id);
            kuponService.deleteKupon(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PEMILIK') or hasRole('ADMIN')")
    public ResponseEntity<KuponResponse> updateKupon(@PathVariable UUID id, @RequestBody KuponRequest request) {
        logCurrentUser();
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
        logCurrentUser();

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
        logCurrentUser();

        List<Kupon> kupons = kuponService.findByKostId(kostId).join();
        if (kupons.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<KuponResponse> responses = kupons.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
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
        response.setKosPemilik(
                kupon.getKosPemilik().stream()
                        .map(Kost::getKostID)
                        .toList()
        );
        return response;
    }

    private void logCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String username = auth.getName();
            String roles = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(", "));
            System.out.println("[DEBUG] User: " + username + ", Roles: " + roles);
        } else {
            System.out.println("[DEBUG] Anonymous or unauthenticated request");
        }
    }
}