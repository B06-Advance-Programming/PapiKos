package id.cs.ui.advprog.inthecost.repository;

import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class KostRepositoryTest {

    @Autowired
    private KostRepository kostRepository;

    @Autowired
    private UserRepository userRepository;  // Your User repository

    private Kost kost;
    private User owner;      // User owning the Kost
    private UUID ownerId;

    @BeforeEach
    void setUp() {
        // Create Roles set (empty or with roles if needed)
        HashSet roles = new HashSet<>();

        // Create a User and save to DB to satisfy FK constraint
        owner = new User("testuser", "password", "testuser@example.com", roles);
        owner = userRepository.save(owner);
        ownerId = owner.getId();

        // Create Kost with valid ownerId
        kost = new Kost("Kos Putri", "Jl. Merdeka No. 10", "Kosan nyaman untuk putri",
                10, 1000000, ownerId);
    }

    @Test
    void testSaveKost() {
        Kost savedKost = kostRepository.save(kost);

        assertNotNull(savedKost.getKostID());
        assertEquals(kost.getNama(), savedKost.getNama());
        assertEquals(kost.getAlamat(), savedKost.getAlamat());
        assertEquals(kost.getDeskripsi(), savedKost.getDeskripsi());
        assertEquals(kost.getJumlahKamar(), savedKost.getJumlahKamar());
        assertEquals(kost.getHargaPerBulan(), savedKost.getHargaPerBulan());
        assertEquals(ownerId, savedKost.getOwnerId());
    }

    @Test
    void testFindByID() {
        Kost savedKost = kostRepository.save(kost);

        Kost foundKost = kostRepository.findById(savedKost.getKostID()).orElse(null);

        assertNotNull(foundKost);
        assertEquals(savedKost.getKostID(), foundKost.getKostID());
        assertEquals(ownerId, foundKost.getOwnerId());
    }

    @Test
    void testFindAll() {
        // Create a second unique User owner for the second Kost
        User owner2 = new User("testuser2", "password", "testuser2@example.com", new HashSet<>());
        owner2 = userRepository.save(owner2);
        UUID ownerId2 = owner2.getId();

        Kost kost2 = new Kost("Kos Cowok", "Jl. Kemerdekaan No. 12", "Kosan untuk cowok",
                8, 1200000, ownerId2);

        kostRepository.save(kost);
        kostRepository.save(kost2);

        List<Kost> kostList = kostRepository.findAll();

        assertNotNull(kostList);
        assertFalse(kostList.isEmpty());

        // Verify each Kost has a non-null and unique ownerId
        var ownerIds = new HashSet<UUID>();
        for (Kost k : kostList) {
            assertNotNull(k.getOwnerId());
            assertTrue(ownerIds.add(k.getOwnerId())); // ensures ownerId is unique across Kosts
        }
    }

    @Test
    void testDeleteByID() {
        Kost savedKost = kostRepository.save(kost);

        kostRepository.deleteById(savedKost.getKostID());

        Kost deletedKost = kostRepository.findById(savedKost.getKostID()).orElse(null);
        assertNull(deletedKost);
    }

    @Test
    void testUpdateKostByID() {
        Kost savedKost = kostRepository.save(kost);

        savedKost.setNama("Kos Putri Baru");
        savedKost.setHargaPerBulan(1500000);

        Kost updatedKost = kostRepository.save(savedKost);

        assertEquals("Kos Putri Baru", updatedKost.getNama());
        assertEquals(1500000, updatedKost.getHargaPerBulan());
        assertEquals(ownerId, updatedKost.getOwnerId());
    }
}