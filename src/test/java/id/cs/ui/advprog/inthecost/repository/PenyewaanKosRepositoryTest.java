package id.cs.ui.advprog.inthecost.repository;

import id.cs.ui.advprog.inthecost.builder.PenyewaanKosBuilder;
import id.cs.ui.advprog.inthecost.enums.StatusPenyewaan;
import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.model.PenyewaanKos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class PenyewaanKosRepositoryTest {

    @Autowired
    private PenyewaanKosRepository repository;

    @Autowired
    private KostRepository kostRepository;

    @Autowired
    private UserRepository userRepository; // Add UserRepository autowiring

    private Kost kos;

    @BeforeEach
    public void setUp() {
        // Create and save a User owner
        User owner = new User("owneruser", "password", "owneruser@example.com", new HashSet<>());
        owner = userRepository.save(owner);

        kos = new Kost("Kos Mawar", "Jl. Melati No. 2", "Kos nyaman", 5, 1200000);
        kos.setOwnerId(owner.getId());   // Set mandatory ownerId
        kostRepository.save(kos);
    }

    @Test
    public void testCreatePenyewaan() {
        PenyewaanKos penyewaan = PenyewaanKosBuilder.builder()
                .namaLengkap("Andi")
                .nomorTelepon("08123456789")
                .tanggalCheckIn(LocalDate.of(2025, 6, 1))
                .durasiBulan(3)
                .kos(kos)
                .build();

        PenyewaanKos saved = repository.save(penyewaan);
        assertNotNull(saved.getId());
        assertEquals("Andi", saved.getNamaLengkap());
    }

    @Test
    public void testGetAllPenyewaan() {
        PenyewaanKos p1 = PenyewaanKosBuilder.builder()
                .kos(kos).build();
        PenyewaanKos p2 = PenyewaanKosBuilder.builder()
                .kos(kos).build();

        repository.save(p1);
        repository.save(p2);

        List<PenyewaanKos> all = repository.findAll();
        assertTrue(all.size() >= 2);  // bisa lebih dari 2 karena data existing di DB
    }

    @Test
    public void testGetById() {
        PenyewaanKos p = PenyewaanKosBuilder.builder()
                .namaLengkap("Dina")
                .kos(kos)
                .build();

        PenyewaanKos saved = repository.save(p);
        Optional<PenyewaanKos> found = repository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Dina", found.get().getNamaLengkap());
    }

    @Test
    public void testUpdateIfDiajukan() {
        PenyewaanKos p = PenyewaanKosBuilder.builder()
                .namaLengkap("Ayu")
                .kos(kos)
                .build();

        PenyewaanKos saved = repository.save(p);
        saved.setNamaLengkap("Ayu Updated");

        PenyewaanKos updated = repository.save(saved);  // save acts as update
        assertEquals("Ayu Updated", updated.getNamaLengkap());
    }

    @Test
    public void testSaveWithDisetujuiStatus() {
        PenyewaanKos p = PenyewaanKosBuilder.builder()
                .namaLengkap("Bambang")
                .status(StatusPenyewaan.DISETUJUI)
                .kos(kos)
                .build();

        PenyewaanKos saved = repository.save(p);

        assertEquals(StatusPenyewaan.DISETUJUI, saved.getStatus());
    }

    @Test
    public void testDeletePenyewaan() {
        PenyewaanKos p = PenyewaanKosBuilder.builder()
                .namaLengkap("Bayu")
                .kos(kos)
                .build();

        PenyewaanKos saved = repository.save(p);
        UUID id = saved.getId();

        repository.deleteById(id);
        assertTrue(repository.findById(id).isEmpty());
    }
}