package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.enums.StatusPenyewaan;
import id.cs.ui.advprog.inthecost.model.PenyewaanKos;
import id.cs.ui.advprog.inthecost.repository.PenyewaanKosRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class PenyewaanKosServiceTest {

    private PenyewaanKosServiceImpl service;
    private PenyewaanKosRepository repository;

    @BeforeEach
    public void setUp() {
        repository = new PenyewaanKosRepository();
        service = new PenyewaanKosServiceImpl(repository);
    }

    @Test
    public void testCreatePenyewaan() {
        PenyewaanKos p = new PenyewaanKos();
        p.setNamaLengkap("Ayu");
        p.setNomorTelepon("08123456789");
        p.setTanggalCheckIn(LocalDate.of(2025, 6, 1));
        p.setDurasiBulan(3);

        PenyewaanKos result = service.create(p);

        assertNotNull(result.getId());
        assertEquals(StatusPenyewaan.DIAJUKAN, result.getStatus());
    }

    @Test
    public void testUpdatePenyewaanBerhasilJikaDiajukan() {
        PenyewaanKos p = new PenyewaanKos();
        p.setNamaLengkap("Rina");
        PenyewaanKos saved = service.create(p);

        saved.setNamaLengkap("Rina Update");
        PenyewaanKos updated = service.update(saved);

        assertEquals("Rina Update", updated.getNamaLengkap());
    }

    @Test
    public void testUpdateGagalJikaStatusDisetujui() {
        PenyewaanKos p = new PenyewaanKos();
        p.setNamaLengkap("Andi");
        p.setStatus(StatusPenyewaan.DISETUJUI);

        PenyewaanKos saved = repository.save(p);
        saved.setNamaLengkap("Tidak Boleh Diubah");

        assertThrows(IllegalStateException.class, () -> {
            service.update(saved);
        });
    }

    @Test
    public void testDeletePenyewaan() {
        PenyewaanKos p = new PenyewaanKos();
        p.setNamaLengkap("Bayu");
        PenyewaanKos saved = service.create(p);

        service.delete(saved.getId());

        Optional<PenyewaanKos> found = repository.findById(saved.getId());
        assertTrue(found.isEmpty());
    }

    @Test
    public void testFindById() {
        PenyewaanKos p = new PenyewaanKos();
        p.setNamaLengkap("Dina");
        PenyewaanKos saved = service.create(p);

        PenyewaanKos found = service.findById(saved.getId());
        assertEquals("Dina", found.getNamaLengkap());
    }

    @Test
    public void testFindAll() {
        service.create(new PenyewaanKos());
        service.create(new PenyewaanKos());

        List<PenyewaanKos> all = service.findAll();
        assertEquals(2, all.size());
    }
}
