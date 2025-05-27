package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.enums.StatusPenyewaan;
import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.PenyewaanKos;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.repository.KostRepository;
import id.cs.ui.advprog.inthecost.repository.PenyewaanKosRepository;
import id.cs.ui.advprog.inthecost.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PenyewaanKosServiceTest {

    @Mock
    private PenyewaanKosRepository repository;
    @Mock
    private KostRepository kostRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PenyewaanKosServiceImpl service;

    private Kost kos;
    private User owner;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(UUID.randomUUID());
        owner.setUsername("owner_test");
        owner.setPassword("password");
        owner.setEmail("owner_test@example.com");
        owner.setBalance(100000);

        kos = new Kost("Kos Mawar", "Jl. Melati No. 2", "Kos nyaman", 5, 1200000, owner.getId());
        kos.setKostID(UUID.randomUUID());
    }

    @Test
    void testCreatePenyewaan() {
        PenyewaanKos penyewaan = new PenyewaanKos();
        penyewaan.setNamaLengkap("Ayu");
        penyewaan.setNomorTelepon("08123456789");
        penyewaan.setTanggalCheckIn(LocalDate.of(2025, 6, 1));
        penyewaan.setDurasiBulan(3);
        penyewaan.setKos(kos);

        when(repository.save(any(PenyewaanKos.class))).thenAnswer(i -> {
            PenyewaanKos arg = i.getArgument(0);
            arg.setId(UUID.randomUUID());
            return arg;
        });

        PenyewaanKos result = service.create(penyewaan);

        assertNotNull(result.getId());
        assertEquals(StatusPenyewaan.DIAJUKAN, result.getStatus());
        verify(repository).save(any(PenyewaanKos.class));
    }

    @Test
    void testUpdatePenyewaanBerhasilJikaDiajukan() {
        PenyewaanKos penyewaan = new PenyewaanKos();
        penyewaan.setId(UUID.randomUUID());
        penyewaan.setNamaLengkap("Rina");
        penyewaan.setKos(kos);
        penyewaan.setStatus(StatusPenyewaan.DIAJUKAN);

        when(repository.findById(penyewaan.getId())).thenReturn(Optional.of(penyewaan));
        when(repository.save(any(PenyewaanKos.class))).thenAnswer(i -> i.getArgument(0));

        penyewaan.setNamaLengkap("Rina Update");
        PenyewaanKos updated = service.update(penyewaan);

        assertEquals("Rina Update", updated.getNamaLengkap());
        verify(repository).save(penyewaan);
    }

    @Test
    void testUpdateGagalJikaStatusDisetujui() {
        PenyewaanKos penyewaan = new PenyewaanKos();
        penyewaan.setId(UUID.randomUUID());
        penyewaan.setNamaLengkap("Andi");
        penyewaan.setKos(kos);
        penyewaan.setStatus(StatusPenyewaan.DISETUJUI);

        when(repository.findById(penyewaan.getId())).thenReturn(Optional.of(penyewaan));

        penyewaan.setNamaLengkap("Tidak Boleh Diubah");

        assertThrows(IllegalStateException.class, () -> service.update(penyewaan));
    }

    @Test
    void testDeletePenyewaan() throws Exception {
        UUID id = kos.getKostID();

        doNothing().when(repository).deleteById(id);
        when(repository.findById(id)).thenReturn(Optional.empty());

        service.delete(id).get();

        assertTrue(repository.findById(id).isEmpty());
        verify(repository).deleteById(id);
    }

    @Test
    void testFindById() {
        PenyewaanKos penyewaan = new PenyewaanKos();
        penyewaan.setId(UUID.randomUUID());
        penyewaan.setNamaLengkap("Dina");
        penyewaan.setKos(kos);

        when(repository.findById(penyewaan.getId())).thenReturn(Optional.of(penyewaan));

        PenyewaanKos found = service.findById(penyewaan.getId());

        assertEquals("Dina", found.getNamaLengkap());
        verify(repository).findById(penyewaan.getId());
    }

    @Test
    void testFindAll() {
        PenyewaanKos p1 = new PenyewaanKos();
        PenyewaanKos p2 = new PenyewaanKos();
        when(repository.findAll()).thenReturn(List.of(p1, p2));

        List<PenyewaanKos> all = service.findAll();
        assertEquals(2, all.size());
        verify(repository).findAll();
    }

    @Test
    void testCreatePenyewaanWithExistingPending() {
        PenyewaanKos existing = new PenyewaanKos();
        existing.setId(UUID.randomUUID());
        existing.setNamaLengkap("Existing");
        existing.setKos(kos);
        existing.setUserId(UUID.randomUUID());
        existing.setStatus(StatusPenyewaan.DIAJUKAN);

        PenyewaanKos newPenyewaan = new PenyewaanKos();
        newPenyewaan.setNamaLengkap("New");
        newPenyewaan.setKos(kos);
        newPenyewaan.setUserId(existing.getUserId());

        when(repository.findByKos_KostIDAndUserIdAndStatus(
                kos.getKostID(), existing.getUserId(), StatusPenyewaan.DIAJUKAN))
                .thenReturn(List.of(existing));
        when(repository.save(any(PenyewaanKos.class))).thenAnswer(i -> {
            PenyewaanKos p = i.getArgument(0);
            if (p.getId() == null) p.setId(UUID.randomUUID());
            return p;
        });

        PenyewaanKos result = service.create(newPenyewaan);

        assertNotNull(result.getId());
        assertEquals(StatusPenyewaan.DIAJUKAN, result.getStatus());
        verify(repository, times(2)).save(any(PenyewaanKos.class)); // existing updated + new saved
    }

    @Test
    void testHasPendingPenyewaanWhenExists() {
        UUID userId = UUID.randomUUID();
        UUID kostId = UUID.randomUUID();
        PenyewaanKos pending = new PenyewaanKos();
        when(repository.findByKos_KostIDAndUserIdAndStatus(
                kostId, userId, StatusPenyewaan.DIAJUKAN))
                .thenReturn(List.of(pending));

        boolean hasPending = service.hasPendingPenyewaan(userId, kostId);

        assertTrue(hasPending);
        verify(repository).findByKos_KostIDAndUserIdAndStatus(kostId, userId, StatusPenyewaan.DIAJUKAN);
    }

    @Test
    void testHasPendingPenyewaanWhenNone() {
        UUID userId = UUID.randomUUID();
        UUID kostId = UUID.randomUUID();
        when(repository.findByKos_KostIDAndUserIdAndStatus(
                kostId, userId, StatusPenyewaan.DIAJUKAN))
                .thenReturn(Collections.emptyList());

        boolean hasPending = service.hasPendingPenyewaan(userId, kostId);

        assertFalse(hasPending);
        verify(repository).findByKos_KostIDAndUserIdAndStatus(kostId, userId, StatusPenyewaan.DIAJUKAN);
    }

    @Test
    void testGetAllByUserIdAndStatus() {
        UUID userId = UUID.randomUUID();
        StatusPenyewaan status = StatusPenyewaan.DIAJUKAN;
        PenyewaanKos p1 = new PenyewaanKos();
        PenyewaanKos p2 = new PenyewaanKos();

        when(repository.findByUserIdAndStatus(userId, status))
                .thenReturn(List.of(p1, p2));

        List<PenyewaanKos> result = service.getAllByUserIdAndStatus(userId, status);

        assertEquals(2, result.size());
        verify(repository).findByUserIdAndStatus(userId, status);
    }

}
