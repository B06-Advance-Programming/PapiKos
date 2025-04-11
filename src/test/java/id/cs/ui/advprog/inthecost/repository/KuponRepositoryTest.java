package id.cs.ui.advprog.inthecost.repository;

import id.cs.ui.advprog.inthecost.enums.KuponStatus;
import id.cs.ui.advprog.inthecost.model.Kupon;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class KuponRepositoryTest {
    KuponRepository kuponRepository;
    List<Kupon> kuponList;

    @BeforeEach
    void setUp(){
        kuponRepository = new KuponRepository();

        kuponList = new ArrayList<>();

        Kupon kupon1 = new Kupon("PemilikKos1", LocalDate.of(2025, 10, 20), 15, "Kupon Hari Pahlawan 2025", false);
        kuponList.add(kupon1);

        Kupon kupon2 = new Kupon("PemilikKos1", LocalDate.of(2026, 3, 1), 15, "Kupon Tahun Baru 2025", false);
        kuponList.add(kupon2);

        Kupon kupon3 = new Kupon("Admin", LocalDate.of(2025, 5, 22), 15, "Kupon Pendatang Baru", true);
        kuponList.add(kupon3);
    }

    @Test
    void testSaveCreateKupon(){
        Kupon kupon = kuponList.get(1);
        Kupon result = kuponRepository.save(kupon);

        Kupon findResult = kuponRepository.findById(kuponList.get(1).getIdKupon());
        assertEquals(kupon.getIdKupon(), result.getIdKupon());
        assertEquals(result.getIdKupon(), findResult.getIdKupon());
        assertEquals(result.getStatusKupon(), findResult.getStatusKupon());
        assertEquals(result.getDeskripsi(),findResult.getDeskripsi());
        assertEquals(result.getPemilik(), findResult.getPemilik());
        assertEquals(result.getPersentase(), findResult.getPersentase());
        assertEquals(result.getMasaBerlaku(), findResult.getMasaBerlaku());
        assertEquals(result.getKodeUnik(), findResult.getKodeUnik());
    }

    @Test
    void testSaveUpdateKupon(){
        Kupon kupon = new Kupon("user1", LocalDate.of(2025, 4, 10), 30, "Diskon awal", false);
        kuponRepository.save(kupon);

        kupon.setMasaBerlaku(LocalDate.now().plusDays(10));
        kupon.setDeskripsi("Diskon Update");
        kupon.setPersentase(25);
        kuponRepository.save(kupon);

        Kupon updated = kuponRepository.findById(kupon.getIdKupon());
        assertNotNull(updated);
        assertEquals(kupon.getPemilik(), updated.getPemilik());
        assertEquals(kupon.getKodeUnik(), updated.getKodeUnik());
        assertEquals("Diskon Update", updated.getDeskripsi());
        assertEquals(25, updated.getPersentase());
        assertEquals(KuponStatus.VALID, updated.getStatusKupon());
    }

    @Test
    void testFindByIdIfIdFound(){
        for(Kupon kupon: kuponList){
            kuponRepository.save(kupon);
        }

        Kupon target = kuponList.get(1);
        Kupon findResult = kuponRepository.findById(target.getIdKupon());

        assertEquals(target.getIdKupon(), findResult.getIdKupon());
        assertEquals(target.getPemilik(), findResult.getPemilik());
        assertEquals(target.getKodeUnik(), findResult.getKodeUnik());
        assertEquals(target.getDeskripsi(), findResult.getDeskripsi());
        assertEquals(target.getPersentase(), findResult.getPersentase());
        assertEquals(target.getStatusKupon(), findResult.getStatusKupon());
        assertEquals(target.getMasaBerlaku(), findResult.getMasaBerlaku());
        assertEquals(target.isKuponGlobal(), findResult.isKuponGlobal());
    }

    @Test
    void testFindByIdIfIdNotFound() {
        for(Kupon kupon: kuponList){
            kuponRepository.save(kupon);
        }
        assertNull(kuponRepository.findById("invalidId"));
    }

    @Test
    void testFindByKodeUnikIfFound(){
        for(Kupon kupon: kuponList){
            kuponRepository.save(kupon);
        }

        Kupon target = kuponList.get(1);
        Kupon findResult = kuponRepository.findByKodeUnik(target.getKodeUnik());

        assertEquals(target.getIdKupon(), findResult.getIdKupon());
        assertEquals(target.getPemilik(), findResult.getPemilik());
        assertEquals(target.getKodeUnik(), findResult.getKodeUnik());
        assertEquals(target.getDeskripsi(), findResult.getDeskripsi());
        assertEquals(target.getPersentase(), findResult.getPersentase());
        assertEquals(target.getStatusKupon(), findResult.getStatusKupon());
        assertEquals(target.getMasaBerlaku(), findResult.getMasaBerlaku());
        assertEquals(target.isKuponGlobal(), findResult.isKuponGlobal());
    }

    @Test
    void testFindByKodeUnikIfNotFound() {
        for(Kupon kupon: kuponList){
            kuponRepository.save(kupon);
        }
        assertNull(kuponRepository.findByKodeUnik("invalidKodeUnik"));
    }

    @Test
    void testDeleteByIdIfSuccess(){
        Kupon kupon = kuponRepository.save(kuponList.get(1));
        boolean success = kuponRepository.deleteById(kupon.getIdKupon());
        assertTrue(success);
        assertNull(kuponRepository.findById(kupon.getIdKupon()));
        assertNull(kuponRepository.findByKodeUnik(kupon.getKodeUnik()));
    }

    @Test
    void testDeleteByIdIfFailed(){
        Kupon kupon = kuponRepository.save(kuponList.get(1));
        boolean success = kuponRepository.deleteById("InvalidId");
        assertFalse(success);
        assertNotNull(kuponRepository.findById(kupon.getIdKupon()));
        assertNotNull(kuponRepository.findByKodeUnik(kupon.getKodeUnik()));
    }

    @Test
    void testFindAll() {
        for (Kupon kupon : kuponList) {
            kuponRepository.save(kupon);
        }
        List<Kupon> listKupon = kuponRepository.findAll();
        assertEquals(3, listKupon.size());
    }
}
