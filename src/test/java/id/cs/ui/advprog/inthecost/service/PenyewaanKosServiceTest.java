package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.builder.PenyewaanKosBuilder;
import id.cs.ui.advprog.inthecost.enums.StatusPenyewaan;
import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.PenyewaanKos;
import id.cs.ui.advprog.inthecost.repository.KostRepository;
import id.cs.ui.advprog.inthecost.repository.PenyewaanKosRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class PenyewaanKosServiceTest {

    @Autowired
    private PenyewaanKosService service;

    @Autowired
    private PenyewaanKosRepository repository;

    @Autowired
    private KostRepository kostRepository;


    private Kost kos;

    @BeforeEach
    public void setUp() {
        kos = new Kost("Kos Mawar", "Jl. Melati No. 2", "Kos nyaman", 5, 1200000);
        kostRepository.save(kos);
    }

    @Test
    public void testCreatePenyewaan() {
        PenyewaanKos p = PenyewaanKosBuilder.builder()
                .namaLengkap("Ayu")
                .nomorTelepon("08123456789")
                .tanggalCheckIn(LocalDate.of(2025, 6, 1))
                .durasiBulan(3)
                .kos(kos)
                .build();

        PenyewaanKos result = service.create(p);

        assertNotNull(result.getId());
        assertEquals(StatusPenyewaan.DIAJUKAN, result.getStatus());
    }

    @Test
    public void testUpdatePenyewaanBerhasilJikaDiajukan() {
        PenyewaanKos p = PenyewaanKosBuilder.builder()
                .namaLengkap("Rina")
                .kos(kos)
                .build();

        PenyewaanKos saved = service.create(p);
        saved.setNamaLengkap("Rina Update");

        PenyewaanKos updated = service.update(saved);
        assertEquals("Rina Update", updated.getNamaLengkap());
    }

    @Test
    public void testUpdateGagalJikaStatusDisetujui() {
        PenyewaanKos p = PenyewaanKosBuilder.builder()
                .namaLengkap("Andi")
                .kos(kos)
                .build();

        PenyewaanKos saved = service.create(p);
        saved.setStatus(StatusPenyewaan.DISETUJUI);  // update status dulu
        saved.setNamaLengkap("Tidak Boleh Diubah");

        assertThrows(IllegalStateException.class, () -> {
            service.update(saved);
        });
    }

    @Test
    public void testDeletePenyewaan() {
        PenyewaanKos p = PenyewaanKosBuilder.builder()
                .namaLengkap("Bayu")
                .kos(kos)
                .build();

        PenyewaanKos saved = service.create(p);
        UUID id = saved.getId();

        service.delete(id);
        assertTrue(repository.findById(id).isEmpty());
    }

    @Test
    public void testFindById() {
        PenyewaanKos p = PenyewaanKosBuilder.builder()
                .namaLengkap("Dina")
                .kos(kos)
                .build();

        PenyewaanKos saved = service.create(p);
        PenyewaanKos found = service.findById(saved.getId());

        assertEquals("Dina", found.getNamaLengkap());
    }

    @Test
    public void testFindAll() {
        service.create(PenyewaanKosBuilder.builder().kos(kos).build());
        service.create(PenyewaanKosBuilder.builder().kos(kos).build());

        List<PenyewaanKos> all = service.findAll();
        assertTrue(all.size() >= 2);
    }
}
