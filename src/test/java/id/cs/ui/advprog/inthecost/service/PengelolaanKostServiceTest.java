package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.exception.ValidationErrorCode;
import id.cs.ui.advprog.inthecost.exception.ValidationException;
import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.repository.KostRepository;
import id.cs.ui.advprog.inthecost.service.PengelolaanKostImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class PengelolaanKostServiceTest {

    @Autowired
    private PengelolaanKostImpl pengelolaanKost;

    @Autowired
    private KostRepository kostRepository;

    private Kost createSampleKost() {
        Kost kost = new Kost();
        kost.setNama("Kost Asri");
        kost.setAlamat("Jl. Mawar No.10");
        kost.setDeskripsi("Dekat kampus, nyaman");
        kost.setJumlahKamar(15);
        kost.setHargaPerBulan(750000);
        return kost;
    }

    @Test
    public void testAddKost() {
        Kost kost = createSampleKost();
        pengelolaanKost.addKost(kost);

        List<Kost> allKost = kostRepository.findAll();
        assertEquals(1, allKost.size());
        assertEquals("Kost Asri", allKost.getFirst().getNama());
    }

    @Test
    // alamatnya null
    public void testAddKostError() {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            Kost kost = createSampleKost();
            kost.setAlamat(null);
            pengelolaanKost.addKost(kost);
        });
        assertEquals(ValidationErrorCode.NULL_VALUE, exception.getError());
    }

    @Test
    public void testGetAllKost() {
        Kost kost1 = createSampleKost();
        Kost kost2 = createSampleKost();
        kost2.setNama("Kost Harmoni");

        kostRepository.save(kost1);
        kostRepository.save(kost2);

        List<Kost> kostList = pengelolaanKost.getAllKost();
        assertEquals(2, kostList.size());
    }

    @Test
    public void testUpdateKostByID() {
        Kost original = kostRepository.save(createSampleKost());
        Kost updated = createSampleKost();
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
        Kost kost = kostRepository.save(createSampleKost());
        pengelolaanKost.deleteKost(kost.getKostID());

        Optional<Kost> deleted = kostRepository.findById(kost.getKostID());
        assertFalse(deleted.isPresent());
    }

    @Test
    public void testUpdateKostByID_InvalidId_ThrowsException() {
        Kost updated = createSampleKost();
        UUID invalidId = UUID.randomUUID();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> pengelolaanKost.updateKostByID(invalidId, updated)
        );
        assertEquals(ValidationErrorCode.INVALID_ID, exception.getError());
    }
}
