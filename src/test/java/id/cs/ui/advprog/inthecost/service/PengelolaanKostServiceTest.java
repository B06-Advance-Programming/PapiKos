package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.exception.ValidationErrorCode;
import id.cs.ui.advprog.inthecost.exception.ValidationException;
import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.repository.KostRepository;
import id.cs.ui.advprog.inthecost.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
public class PengelolaanKostServiceTest {

    @Autowired
    private PengelolaanKost pengelolaanKost;

    @Autowired
    private KostRepository kostRepository;

    @Autowired
    private UserRepository userRepository;

    User user;
    Kost kost;

    @BeforeEach
    public void createSampleUserAndKost() {
        user = new User();
        String unique = UUID.randomUUID().toString();
        user.setUsername("owner_" + unique);
        user.setPassword("password");
        user.setEmail("owner_" + unique + "@example.com");
        user.setBalance(100000);
        user.setRoles(new HashSet<>());
        user = userRepository.saveAndFlush(user);  // flush biar langsung commit dan dapet id

        kost = new Kost();
        kost.setNama("Kost Asri");
        kost.setAlamat("Jl. Mawar No.10");
        kost.setDeskripsi("Dekat kampus, nyaman");
        kost.setJumlahKamar(15);
        kost.setHargaPerBulan(750000);
        kost.setOwnerId(user.getId());  // pakai id dari user yang sudah disimpan
    }

    @Test
    public void testAddKost() {
        List<Kost> allKostBefore = kostRepository.findAll();
        int currentSize = allKostBefore.size();

        pengelolaanKost.addKost(kost).join();

        List<Kost> allKostAfter = kostRepository.findAll();
        assertEquals(currentSize + 1, allKostAfter.size());

        // cari Kost berdasarkan ID yang baru ditambahkan
        Optional<Kost> foundKost = kostRepository.findById(kost.getKostID());
        assertTrue(foundKost.isPresent());

        assertEquals("Kost Asri", foundKost.get().getNama());
    }

    @Test
    public void testGetAllKost() {
        List<Kost> kostList = pengelolaanKost.getAllKost().join();
        int currentSize = kostList.size();

        pengelolaanKost.addKost(kost).join(); // + 1

        createSampleUserAndKost();
        pengelolaanKost.addKost(kost).join(); // + 2

        kostList = pengelolaanKost.getAllKost().join();
        assertEquals(currentSize + 2, kostList.size());
    }

    @Test
    public void testUpdateKostByID() {
        // Simpan Kost awal ke database
        Kost original = kostRepository.save(kost);

        // Ubah beberapa field
        Kost updated = kostRepository.findById(original.getKostID()).get();
        updated.setNama("Kost Update");
        updated.setAlamat("Jl. Baru");
        updated.setDeskripsi("Deskripsi Baru");
        updated.setJumlahKamar(8);
        updated.setHargaPerBulan(900000);

        // Lakukan update
        System.out.println();
        pengelolaanKost.updateKostByID(original.getKostID(), updated).join();

        // Ambil kembali dari DB dan pastikan nilai-nya berubah
        Optional<Kost> result = kostRepository.findById(original.getKostID());
        assertTrue(result.isPresent());

        Kost updatedResult = result.get();
        assertEquals("Kost Update", updatedResult.getNama());
        assertEquals("Jl. Baru", updatedResult.getAlamat());
        assertEquals("Deskripsi Baru", updatedResult.getDeskripsi());
        assertEquals(8, updatedResult.getJumlahKamar());
        assertEquals(900000, updatedResult.getHargaPerBulan());
    }

    @Test
    public void testDeleteKost() {
        pengelolaanKost.deleteKost(kost.getKostID());

        Optional<Kost> deleted = kostRepository.findById(kost.getKostID());
        assertFalse(deleted.isPresent());
    }

    @Test
    public void testUpdateKostByID_InvalidId_ThrowsException() {
        Kost original = kostRepository.save(kost);
        UUID invalidId = UUID.randomUUID();

        ExecutionException thrown = assertThrows(
                ExecutionException.class,
                () -> pengelolaanKost.updateKostByID(invalidId, original).get()
        );

        // Unwrap cause-nya dan pastikan itu ValidationException
        Throwable cause = thrown.getCause();
        assertTrue(cause instanceof ValidationException);
        ValidationException exception = (ValidationException) cause;

        assertEquals(ValidationErrorCode.INVALID_ID.getCode(), exception.getErrorCode());
    }
}