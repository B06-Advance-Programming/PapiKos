package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.exception.ValidationErrorCode;
import id.cs.ui.advprog.inthecost.exception.ValidationException;
import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.repository.KostRepository;
import id.cs.ui.advprog.inthecost.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class PengelolaanKostServiceTest {

    @Autowired
    private PengelolaanKost pengelolaanKost;

    @Autowired
    private KostRepository kostRepository;

    @Autowired
    private UserRepository userRepository;

    private User createSampleUser() {
        User user = new User();
        // Generate unique username/email using UUID to avoid duplicates
        String unique = UUID.randomUUID().toString();
        user.setUsername("owner_" + unique);
        user.setPassword("password");
        user.setEmail("owner_" + unique + "@example.com");
        user.setBalance(100000);
        user.setRoles(new HashSet<>()); // Set roles as needed
        return userRepository.save(user);
    }

    private Kost createSampleKost(UUID ownerId) {
        Kost kost = new Kost();
        kost.setNama("Kost Asri");
        kost.setAlamat("Jl. Mawar No.10");
        kost.setDeskripsi("Dekat kampus, nyaman");
        kost.setJumlahKamar(15);
        kost.setHargaPerBulan(750000);
        kost.setOwnerId(ownerId); // This must not be null
        return kost;
    }

    @Test
    public void testAddKost() {
        User owner = createSampleUser();

        List<Kost> allKostBefore = kostRepository.findAll();
        int currentSize = allKostBefore.size();

        Kost kost = createSampleKost(owner.getId());
        pengelolaanKost.addKost(kost);

        List<Kost> allKostAfter = kostRepository.findAll();
        assertEquals(currentSize + 1, allKostAfter.size());

        // cari Kost berdasarkan ID yang baru ditambahkan
        Optional<Kost> foundKost = kostRepository.findById(kost.getKostID());
        assertTrue(foundKost.isPresent());

        assertEquals("Kost Asri", foundKost.get().getNama());
    }

    @Test
    public void testGetAllKost() {
        User owner1 = createSampleUser();
        User owner2 = createSampleUser();

        List<Kost> kostList = pengelolaanKost.getAllKost();
        int currentSize = kostList.size();

        Kost kost1 = createSampleKost(owner1.getId());
        Kost kost2 = createSampleKost(owner2.getId());
        kost2.setNama("Kost Harmoni");

        kostRepository.save(kost1);
        kostRepository.save(kost2);

        kostList = pengelolaanKost.getAllKost();
        assertEquals(currentSize + 2, kostList.size());
    }

    @Test
    public void testUpdateKostByID() {
        User owner = createSampleUser();
        Kost original = kostRepository.save(createSampleKost(owner.getId()));
        Kost updated = createSampleKost(owner.getId());
        updated.setNama("Kost Update");
        updated.setHargaPerBulan(850000);

        pengelolaanKost.updateKostByID(original.getKostID(), updated);

        Optional<Kost> result = kostRepository.findById(original.getKostID());
        assertTrue(result.isPresent());
        assertEquals("Kost Update", result.get().getNama());
        assertEquals(850000, result.get().getHargaPerBulan());
    }

    @Test
    public void testDeleteKost() {
        User owner = createSampleUser();
        Kost kost = kostRepository.save(createSampleKost(owner.getId()));
        pengelolaanKost.deleteKost(kost.getKostID());

        Optional<Kost> deleted = kostRepository.findById(kost.getKostID());
        assertFalse(deleted.isPresent());
    }

    @Test
    public void testUpdateKostByID_InvalidId_ThrowsException() {
        User owner = createSampleUser();
        Kost updated = createSampleKost(owner.getId());
        UUID invalidId = UUID.randomUUID();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> pengelolaanKost.updateKostByID(invalidId, updated)
        );
        assertEquals(ValidationErrorCode.INVALID_ID.getCode(), exception.getErrorCode());
    }
}