package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.model.Kupon;
import id.cs.ui.advprog.inthecost.repository.KuponRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class KuponServiceTest {
    @InjectMocks
    KuponServiceImpl kuponService;

    @Mock
    private KuponRepository kuponRepository;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateKupon(){
        Kupon kupon = new Kupon("user1", LocalDate.of(2025, 9, 12), 10, "Kupon Test", false);
        when(kuponRepository.save(any(Kupon.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Kupon addedKupon = kuponService.createKupon(kupon);
        assertEquals(kupon.getIdKupon(), addedKupon.getIdKupon());
        assertEquals(kupon.getPemilik(), addedKupon.getPemilik());
        assertEquals(kupon.getKodeUnik(), addedKupon.getKodeUnik());
        assertEquals(kupon.getDeskripsi(), addedKupon.getDeskripsi());
        assertEquals(kupon.getPersentase(), addedKupon.getPersentase());
        assertEquals(kupon.getStatusKupon(), addedKupon.getStatusKupon());
        assertEquals(kupon.getMasaBerlaku(), addedKupon.getMasaBerlaku());
        assertEquals(kupon.isKuponGlobal(), addedKupon.isKuponGlobal());
    }

    @Test
    public void testUpdateKupon(){
        Kupon kupon = new Kupon("user1", LocalDate.of(2025, 9, 12), 10, "Kupon Test", false);
        when(kuponRepository.save(any(Kupon.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Kupon addedKupon = kuponService.createKupon(kupon);
        addedKupon.setPersentase(15);
        addedKupon.setDeskripsi("New Kupon Edit");
        addedKupon.setMasaBerlaku(LocalDate.of(2025, 10, 10));
        Kupon editedKupon = kuponService.createKupon(addedKupon);
        assertEquals(addedKupon.getIdKupon(), editedKupon.getIdKupon());
        assertEquals(addedKupon.getPemilik(), editedKupon.getPemilik());
        assertEquals(addedKupon.getKodeUnik(), editedKupon.getKodeUnik());
        assertEquals(addedKupon.getDeskripsi(), editedKupon.getDeskripsi());
        assertEquals(addedKupon.getPersentase(), editedKupon.getPersentase());
        assertEquals(addedKupon.getStatusKupon(), editedKupon.getStatusKupon());
        assertEquals(addedKupon.getMasaBerlaku(), editedKupon.getMasaBerlaku());
        assertEquals(addedKupon.isKuponGlobal(), editedKupon.isKuponGlobal());
    }

    @Test
    public void testGetKuponByIdSuccess(){
        Kupon kupon = new Kupon("user1", LocalDate.of(2025, 9, 12), 10, "Kupon Test", false);
        when(kuponRepository.findById(kupon.getIdKupon())).thenReturn(kupon);
        Kupon result = kuponService.getKuponById(kupon.getIdKupon());
        assertEquals(kupon, result);
    }

    @Test
    public void testGetKuponByIdFail(){
        when(kuponRepository.findById("InvalidId")).thenReturn(null);
        assertThrows(NoSuchElementException.class, () -> kuponService.getKuponById("InvalidId"));
    }

    @Test
    public void testGetKuponByKodeUnikSuccess(){
        Kupon kupon = new Kupon("user1", LocalDate.of(2025, 9, 12), 10, "Kupon Test", false);
        when(kuponRepository.findById(kupon.getKodeUnik())).thenReturn(kupon);
        Kupon result = kuponService.getKuponById(kupon.getKodeUnik());
        assertEquals(kupon, result);
    }

    @Test
    public void testGetKuponByKodeUnikFail(){
        when(kuponRepository.findByKodeUnik("InvalidKodeUnik")).thenReturn(null);
        assertThrows(NoSuchElementException.class, () -> kuponService.getKuponByKodeUnik("InvalidKodeUnik"));
    }

    @Test
    void testDeleteKuponSuccess() {
        Kupon kupon = new Kupon("user1", LocalDate.of(2025, 9, 12), 10, "Kupon Test", false);
        when(kuponRepository.save(any(Kupon.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Kupon createdKupon = kuponService.createKupon(kupon);

        when(kuponRepository.deleteById(createdKupon.getIdKupon())).thenReturn(true);

        assertDoesNotThrow(() -> kuponService.deleteKupon(createdKupon.getIdKupon()));
    }

    @Test
    void testDeleteKuponNotFound() {
        when(kuponRepository.deleteById("invalidId")).thenReturn(false);

        assertThrows(NoSuchElementException.class, () -> kuponService.deleteKupon("invalidId"));
    }

    @Test
    void testGetAllKupon() {
        Kupon kupon1 = new Kupon("user1", LocalDate.of(2025, 9, 12), 10, "Kupon Test", false);
        Kupon kupon2 = new Kupon("Admin", LocalDate.of(2026, 10, 22), 20, "Admin Kupon Test", true);
        List<Kupon> list = List.of(kupon1, kupon2);

        when(kuponRepository.findAll()).thenReturn(list);

        List<Kupon> result = kuponService.getAllKupon();

        assertEquals(2, result.size());
    }

}