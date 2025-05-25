package id.cs.ui.advprog.inthecost.controller;

import id.cs.ui.advprog.inthecost.dto.PenyewaanKosDto;
import id.cs.ui.advprog.inthecost.dto.PenyewaanKosRequestDto;
import id.cs.ui.advprog.inthecost.enums.StatusPenyewaan;
import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.PenyewaanKos;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.repository.KostRepository;
import id.cs.ui.advprog.inthecost.service.PenyewaanKosService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PenyewaanKosControllerTest {

    @InjectMocks
    private PenyewaanKosController controller;

    @Mock
    private PenyewaanKosService service;

    @Mock
    private KostRepository kostRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private UUID userId;
    private UUID kostId;
    private Kost kost;
    private PenyewaanKos penyewaan;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userId = UUID.randomUUID();
        kostId = UUID.randomUUID();
        kost = new Kost();
        kost.setKostID(kostId);
        kost.setOwnerId(userId);

        penyewaan = new PenyewaanKos();
        penyewaan.setId(UUID.randomUUID());
        penyewaan.setNamaLengkap("Test User");
        penyewaan.setNomorTelepon("08123456789");
        penyewaan.setTanggalCheckIn(LocalDate.now());
        penyewaan.setDurasiBulan(6);
        penyewaan.setUserId(userId);
        penyewaan.setKos(kost);
        penyewaan.setStatus(StatusPenyewaan.DIAJUKAN);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testCreate() {
        PenyewaanKosRequestDto dto = new PenyewaanKosRequestDto();
        dto.setNamaLengkap("Test");
        dto.setNomorTelepon("0812");
        dto.setTanggalCheckIn(LocalDate.now());
        dto.setDurasiBulan(3);
        dto.setUserId(userId);
        dto.setKostId(kostId);

        when(kostRepository.findById(kostId)).thenReturn(Optional.of(kost));
        when(service.create(any(PenyewaanKos.class))).thenReturn(penyewaan);

        PenyewaanKos result = controller.create(dto);

        assertNotNull(result);
        assertEquals("Test User", result.getNamaLengkap());
    }

    @Test
    void testGetAll() {
        when(service.findAll()).thenReturn(List.of(penyewaan));

        List<PenyewaanKosDto> result = controller.getAll();

        assertEquals(1, result.size());
        assertEquals(penyewaan.getId(), result.get(0).getId());
    }

    @Test
    void testGetAllByPenyewa() {
        User user = new User();
        user.setId(userId);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(service.findAll()).thenReturn(List.of(penyewaan));

        List<PenyewaanKosDto> result = controller.getAllByPenyewa();

        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getUserId());
    }

    @Test
    void testGetAllByPemilik() {
        User user = new User();
        user.setId(userId);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(service.findAll()).thenReturn(List.of(penyewaan));

        List<PenyewaanKosDto> result = controller.getAllByPemilik();

        assertEquals(1, result.size());
        assertEquals(kostId, result.get(0).getKostId());
    }

    @Test
    void testUpdate() {
        PenyewaanKosRequestDto dto = new PenyewaanKosRequestDto();
        dto.setId(penyewaan.getId());
        dto.setNamaLengkap("Update");
        dto.setNomorTelepon("0812");
        dto.setTanggalCheckIn(LocalDate.now());
        dto.setDurasiBulan(2);
        dto.setUserId(userId);
        dto.setStatus("DIAJUKAN");
        dto.setKostId(kostId);

        when(kostRepository.findById(kostId)).thenReturn(Optional.of(kost));
        when(service.update(any(PenyewaanKos.class))).thenReturn(penyewaan);

        PenyewaanKosDto result = controller.update(dto);

        assertNotNull(result);
        assertEquals(penyewaan.getId(), result.getId());
    }
}
