package id.cs.ui.advprog.inthecost.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.Kupon;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.service.KuponServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import id.cs.ui.advprog.inthecost.repository.UserRepository;
import id.cs.ui.advprog.inthecost.repository.KostRepository;
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
    private KuponServiceImpl kuponService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KostRepository kostRepository;

    @GetMapping
    public ResponseEntity<List<KuponResponse>> getAllKupon() {
        logCurrentUser();

        List<Kupon> kupons = kuponService.getAllKupon();

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
            Kupon kupon = kuponService.getKuponById(id);
            return ResponseEntity.ok(mapToResponse(kupon));
        }catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKupon(@PathVariable UUID id) {
        try{
            Kupon kupon = kuponService.getKuponById(id);
            kuponService.deleteKupon(id);
            return ResponseEntity.noContent().build();
        }catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<KuponResponse> updateKupon(@PathVariable UUID id, @RequestBody KuponRequest request) {
        logCurrentUser();
        try {
            kuponService.updateKupon(
                    id,
                    request.getPemilik(),
                    request.getKosPemilik(),
                    request.getPersentase(),
                    request.getNamaKupon(),
                    request.getMasaBerlaku(),
                    request.getDeskripsi()
            );
            Kupon updatedKupon = kuponService.getKuponById(id);

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
    public ResponseEntity<KuponResponse> createKupon(@RequestBody KuponRequest request) {
        logCurrentUser();

        if (request.getPersentase() < 0 || request.getPersentase() > 100 || request.getPemilik() == null || request.getKosPemilik() == null || request.getKosPemilik().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        User user = null;
        try{
            User userTry = userRepository.findById(request.getPemilik())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user = userTry;
        }catch (RuntimeException e){
            return ResponseEntity.internalServerError().build();
        }

        List<Kost> kostList = kostRepository.findAllById(request.getKosPemilik());
        if (kostList.size() != request.getKosPemilik().size()) {
            return ResponseEntity.badRequest().build();
        }

        Kupon kupon = new Kupon(user, kostList, request.getNamaKupon(),request.getMasaBerlaku(), request.getPersentase(), request.getDeskripsi());
        Kupon savedKupon = kuponService.createKupon(kupon);

        return ResponseEntity.ok(mapToResponse(savedKupon));
    }

    //Kupon Response DTO
    public static class KuponResponse{
        private UUID idKupon;
        private String pemilik;
        private String kodeUnik;
        private int persentase;
        private String namaKupon;
        private LocalDate masaBerlaku;
        private String deskripsi;
        private String statusKupon;
        private List<String> kosPemilik;

        public UUID getIdKupon() {return idKupon;}
        public void setIdKupon(UUID idKupon) {this.idKupon = idKupon;}

        public String getKodeUnik() {return kodeUnik;}
        public void setKodeUnik(String kodeUnik) {this.kodeUnik = kodeUnik;}

        public int getPersentase() {return persentase;}
        public void setPersentase(int persentase) {this.persentase = persentase;}

        public String getNamaKupon() {return namaKupon;}
        public void setNamaKupon(String namaKupon) {this.namaKupon = namaKupon;}

        public LocalDate getMasaBerlaku() {return masaBerlaku;}
        public void setMasaBerlaku(LocalDate masaBerlaku) {this.masaBerlaku = masaBerlaku;}

        public String getDeskripsi() {return deskripsi;}
        public void setDeskripsi(String deskripsi) {this.deskripsi = deskripsi;}

        public String getStatusKupon() {return statusKupon;}
        public void setStatusKupon(String statusKupon) {this.statusKupon = statusKupon;}

        public String getPemilik() {return pemilik;}
        public void setPemilik(String pemilik) {this.pemilik = pemilik;}

        public List<String> getKosPemilik() {return kosPemilik;}
        public void setKosPemilik(List<String> kosPemilik) {this.kosPemilik = kosPemilik;}
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
        response.setPemilik(kupon.getPemilik().getUsername());
        response.setKosPemilik(kupon.getKosPemilik().stream()
                .map(Kost::getNama)
                .toList());
        return response;
    }

    //Kupon Request DTO
    public static class KuponRequest{
        private UUID pemilik;
        private List<UUID> kosPemilik;
        private String namaKupon;

        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate masaBerlaku;

        private int persentase;
        private String deskripsi;

        public UUID getPemilik() { return pemilik; }
        public void setPemilik(UUID pemilik) { this.pemilik = pemilik; }

        public List<UUID> getKosPemilik(){return kosPemilik;}
        public void setKosPemilik(List<UUID> kosPemilik){this.kosPemilik = kosPemilik;}

        public String getNamaKupon(){return namaKupon;}
        public void setNamaKupon(String namaKupon){this.namaKupon = namaKupon;}

        public LocalDate getMasaBerlaku(){return masaBerlaku;}
        public void setMasaBerlaku(LocalDate masaBerlaku){this.masaBerlaku = masaBerlaku;}

        public int getPersentase(){return persentase;}
        public void setPersentase(int persentase){this.persentase = persentase;}

        public String getDeskripsi(){return deskripsi;}
        public void setDeskripsi(String deskripsi){this.deskripsi = deskripsi;}
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