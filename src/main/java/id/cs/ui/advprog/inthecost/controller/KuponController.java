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
    @GetMapping
    public ResponseEntity<List<KuponResponse>> getAllKupon() {
        return new ResponseEntity<>(null);
    }


    @GetMapping("/{id}")
    public ResponseEntity<KuponResponse> getKuponById(@PathVariable UUID id) {
        return new ResponseEntity<>(null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKupon(@PathVariable UUID id) {
        return new ResponseEntity<>(null);
    }


    @PutMapping("/{id}")
    public ResponseEntity<KuponResponse> updateKupon(@PathVariable UUID id, @RequestBody KuponRequest request) {
        return new ResponseEntity<>(null);
    }

    @PostMapping
    public ResponseEntity<KuponResponse> createKupon(@RequestBody KuponRequest request) {
        return new ResponseEntity<>(null);
    }

    //Kupon Response DTO
    public static class KuponResponse{
    }


    //Kupon Request DTO
    public static class KuponRequest{
    }
}