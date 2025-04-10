package id.cs.ui.advprog.inthecost.model;

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
        LocalDate masaBerlaku = LocalDate.of(2025, 4, 10);
        String deskripsi = "Kupon Lebaran Idul Fitri 2025";
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
    void testGetMasaBerlakuKupon(){assertEquals(LocalDate.of(2025, 4, 10), this.kupon.getMasaBerlaku());}

    @Test
    void testGetDeskripsiKupon(){assertEquals("Kupon Lebaran Idul Fitri 2025", this.kupon.getDeskripsi());}

    @Test
    void testGetStatusKuponGlobal(){assertTrue(this.kupon.isKuponGlobal());}

    @Test
    void testIdKuponNotNull(){assertNotNull(this.kupon.getIdKupon());}

    @Test
    void testKodeUnikKuponNotNull(){assertNotNull(this.kupon.getKodeUnik());}
}
