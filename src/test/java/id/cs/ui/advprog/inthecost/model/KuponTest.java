package id.cs.ui.advprog.inthecost.model;

import id.cs.ui.advprog.inthecost.enums.KuponStatus;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class KuponTest {
    Kupon kupon;
    @BeforeEach
    void setUp(){
        String pemilik = "Admin";
        int persentase = 10;
        LocalDate masaBerlaku = LocalDate.of(2026, 10, 10);
        String deskripsi = "Kupon Lebaran Idul Fitri 2026";
        boolean kuponGlobal = true;

        this.kupon = new Kupon(pemilik, masaBerlaku, persentase, deskripsi,kuponGlobal);
    }

    @Test
    void testKuponNotNull() {assertNotNull(kupon);}

    @Test
    void testGetPemilikKupon(){assertEquals("Admin", this.kupon.getPemilik());}

    @Test
    void testGetPersentase(){assertEquals(10, this.kupon.getPersentase());}

    @Test
    void testGetMasaBerlakuKupon(){assertEquals(LocalDate.of(2026, 10, 10), this.kupon.getMasaBerlaku());}

    @Test
    void testGetDeskripsiKupon(){assertEquals("Kupon Lebaran Idul Fitri 2026", this.kupon.getDeskripsi());}

    @Test
    void testGetStatusKuponGlobal(){assertTrue(this.kupon.isKuponGlobal());}

    @Test
    void testIdKuponNotNull(){assertNotNull(this.kupon.getIdKupon());}

    @Test
    void testKodeUnikKuponNotNull(){assertNotNull(this.kupon.getKodeUnik());}

    @Test
    void testPemilikNull(){
        assertThrows(IllegalArgumentException.class, ()->{
            Kupon newKupon = new Kupon(null, LocalDate.of(2026, 4, 10), 10, "Kupon Lebaran Idul Fitri 2026", true);
        });
    }

    @Test
    void testMasaBerlakuNull(){
        assertThrows(IllegalArgumentException.class, ()->{
            Kupon newKupon = new Kupon("Admin", null, 10, "Kupon Lebaran Idul Fitri 2026", true);
        });
    }

    @Test
    void testPersentaseInvalid(){
        assertThrows(IllegalArgumentException.class, () -> {
            Kupon newKupon = new Kupon("Admin", LocalDate.of(2026, 4, 10), -30, "Kupon Lebaran Idul Fitri 2026", true);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Kupon newKupon = new Kupon("Admin", LocalDate.of(2026, 4, 10), 0, "Kupon Lebaran Idul Fitri 2026", true);
        });
    }

    @Test
    void testDeskripsiNull(){
        assertThrows(IllegalArgumentException.class, ()->{
            Kupon newKupon = new Kupon("Admin", LocalDate.of(2026, 4, 10), 10, null, true);
        });
        assertThrows(IllegalArgumentException.class, ()->{
            Kupon newKupon = new Kupon("Admin", LocalDate.of(2026, 4, 10), 10, "", true);
        });
    }

    @Test
    void testSetPersentaseInvalid() {
        assertThrows(IllegalArgumentException.class, () -> kupon.setPersentase(0));
        assertThrows(IllegalArgumentException.class, () -> kupon.setPersentase(200));
    }

    @Test
    void testSetDeskripsiInvalid() {
        assertThrows(IllegalArgumentException.class, () -> kupon.setDeskripsi(""));
        assertThrows(IllegalArgumentException.class, () -> kupon.setDeskripsi(null));
    }

    @Test
    void testSetMasaBerlakuInvalid() {
        assertThrows(IllegalArgumentException.class, () -> kupon.setMasaBerlaku(null));
    }

    @Test
    void testStatusKuponValid(){
        System.out.println(kupon.getStatusKupon());
        kupon.refreshStatus();
        assertEquals(KuponStatus.VALID, kupon.getStatusKupon());
    }

    @Test
    void testStatusKuponInvalid(){
        kupon.setMasaBerlaku(LocalDate.of(2020, 1, 1));
        assertEquals(KuponStatus.INVALID, kupon.getStatusKupon());
    }

    @Test
    void testKuponToString(){
        String kodeUnik = kupon.getKodeUnik();
        assertEquals(String.format("Kupon[%s, Admin, 10%%, Hingga: 10 October 2026, Status: VALID]", kodeUnik), kupon.toString());
    }
}
