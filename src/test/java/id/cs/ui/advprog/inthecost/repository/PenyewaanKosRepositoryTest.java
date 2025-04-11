package id.cs.ui.advprog.inthecost.repository;

import id.cs.ui.advprog.inthecost.enums.StatusPenyewaan;
import id.cs.ui.advprog.inthecost.model.KosSewa;
import id.cs.ui.advprog.inthecost.model.PenyewaanKos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class PenyewaanKosRepositoryTest {

    private PenyewaanKosRepository repository;
    private KosSewa kos;

    @BeforeEach
    public void setUp() {
        repository = new PenyewaanKosRepository();
        kos = new KosSewa();
        kos.setId(1L);
        kos.setNama("Kos Mawar");
        kos.setAlamat("Jl. Melati No. 2");
        kos.setDeskripsi("Kos nyaman");
        kos.setJumlahKamar(5);
        kos.setHargaSewaBulanan(1200000);
    }

    @Test
    public void testCreatePenyewaan() {
        PenyewaanKos penyewaan = new PenyewaanKos();
        penyewaan.setNamaLengkap("Andi");
        penyewaan.setNomorTelepon("08123456789");
        penyewaan.setTanggalCheckIn(LocalDate.of(2025, 6, 1));
        penyewaan.setDurasiBulan(3);
        penyewaan.setKos(kos);

        PenyewaanKos saved = repository.save(penyewaan);
        assertNotNull(saved.getId());
        assertEquals("Andi", saved.getNamaLengkap());
    }

    @Test
    public void testGetAllPenyewaan() {
        PenyewaanKos p1 = repository.save(new PenyewaanKos());
        PenyewaanKos p2 = repository.save(new PenyewaanKos());
        List<PenyewaanKos> all = repository.findAll();
        assertEquals(2, all.size());
    }

    @Test
    public void testGetById() {
        PenyewaanKos p = new PenyewaanKos();
        p.setNamaLengkap("Dina");
        PenyewaanKos saved = repository.save(p);
        Optional<PenyewaanKos> found = repository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Dina", found.get().getNamaLengkap());
    }

    @Test
    public void testUpdateIfDiajukan() {
        PenyewaanKos p = new PenyewaanKos();
        p.setNamaLengkap("Ayu");
        PenyewaanKos saved = repository.save(p);
        saved.setNamaLengkap("Ayu Updated");

        PenyewaanKos updated = repository.update(saved);
        assertEquals("Ayu Updated", updated.getNamaLengkap());
    }

    @Test
    public void testUpdateFailsIfDisetujui() {
        PenyewaanKos p = new PenyewaanKos();
        p.setNamaLengkap("Bambang");
        PenyewaanKos saved = repository.save(p);
        saved.setStatus(StatusPenyewaan.DISETUJUI);
        saved.setNamaLengkap("Diubah");

        assertThrows(IllegalStateException.class, () -> {
            repository.update(saved);
        });
    }

    @Test
    public void testDeletePenyewaan() {
        PenyewaanKos p = new PenyewaanKos();
        p.setNamaLengkap("Bayu");
        PenyewaanKos saved = repository.save(p);

        repository.delete(saved.getId());
        assertTrue(repository.findById(saved.getId()).isEmpty());
    }
}
