package id.cs.ui.advprog.inthecost.model;

import id.cs.ui.advprog.inthecost.enums.KuponStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class KuponTest {
    Kupon kupon;
    @BeforeEach
    void setUp(){
        Kost kos1 = mock(Kost.class);
        when(kos1.getNama()).thenReturn("Kos Alamanda");

        Kost kos2 = mock(Kost.class);
        when(kos2.getNama()).thenReturn("Kos Griya Asri");

        String nama = "Kupon Idul Fitri";
        int persentase = 10;
        LocalDate masaBerlaku = LocalDate.of(2026, 10, 10);
        String deskripsi = "Kupon Lebaran Idul Fitri 2026 Diskon 10%";
        List<Kost> kosPemilik = List.of(kos1, kos2);
        int quantity = 2;

        this.kupon = new Kupon(kosPemilik, nama, masaBerlaku, persentase, deskripsi, quantity);
    }


    @Test
    void testKuponNotNull() {assertNotNull(kupon);}

    @Test
    void testGetKosPemilikKupon(){assertEquals(List.of("Kos Alamanda", "Kos Griya Asri"), this.kupon.getKosPemilik().stream().map(Kost::getNama).toList());}

    @Test
    void testGetPersentase(){assertEquals(10, this.kupon.getPersentase());}

    @Test
    void testGetNamaKupon(){assertEquals("Kupon Idul Fitri", this.kupon.getNamaKupon());}

    @Test
    void testGetMasaBerlakuKupon(){assertEquals(LocalDate.of(2026, 10, 10), this.kupon.getMasaBerlaku());}

    @Test
    void testGetDeskripsiKupon(){assertEquals("Kupon Lebaran Idul Fitri 2026 Diskon 10%", this.kupon.getDeskripsi());}

    @Test
    void testGetQuantityKupon(){assertEquals(2, this.kupon.getQuantity());}

    @Test
    void testKodeUnikKuponNotNull(){assertNotNull(this.kupon.getKodeUnik());}

    @Test
    void testUpdateKosPemilik(){
        Kost kos1 = mock(Kost.class);
        when(kos1.getNama()).thenReturn("KOS1");

        Kost kos3 = mock(Kost.class);
        when(kos3.getNama()).thenReturn("KOS3");

        Kost kos4 = mock(Kost.class);
        when(kos4.getNama()).thenReturn("KOS4");

        List<Kost> updatedKosList = List.of(kos1, kos3, kos4);
        this.kupon.setKosPemilik(updatedKosList);

        List<String> actualNames = this.kupon.getKosPemilik().stream()
                .map(Kost::getNama)
                .collect(Collectors.toList());

        assertEquals(List.of("KOS1", "KOS3", "KOS4"), actualNames);
    }

    @Test
    void testMasaBerlakuNull(){
        assertThrows(IllegalArgumentException.class, ()->{
            Kupon newKupon = new Kupon(List.of(mock(Kost.class), mock(Kost.class)), "Test Kupon" ,null, 10, "Kupon Lebaran Idul Fitri 2026", 6);
        });
    }

    @Test
    void testPersentaseInvalid(){
        assertThrows(IllegalArgumentException.class, () -> {
            Kupon newKupon = new Kupon(List.of(mock(Kost.class), mock(Kost.class)), "Test Kupon",LocalDate.of(2026, 4, 10), -30, "Kupon Lebaran Idul Fitri 2026", 6);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Kupon newKupon = new Kupon(List.of(mock(Kost.class), mock(Kost.class)), "Test Kupon",LocalDate.of(2026, 4, 10), 0, "Kupon Lebaran Idul Fitri 2026", 6);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Kupon newKupon = new Kupon(List.of(mock(Kost.class), mock(Kost.class)), "Test Kupon",LocalDate.of(2026, 4, 10), 129, "Kupon Lebaran Idul Fitri 2026", 6);
        });
    }

    @Test
    void testDeskripsiNull(){
        assertThrows(IllegalArgumentException.class, ()->{
            Kupon newKupon = new Kupon(List.of(mock(Kost.class), mock(Kost.class)), "Test Kupon",LocalDate.of(2026, 4, 10), 10, null, 6);
        });
        assertThrows(IllegalArgumentException.class, ()->{
            Kupon newKupon = new Kupon(List.of(mock(Kost.class), mock(Kost.class)), "Test Kupon",LocalDate.of(2026, 4, 10), 10, "", 6);
        });
    }

    @Test
    void testSetPersentaseInvalid() {
        assertThrows(IllegalArgumentException.class, () -> kupon.setPersentase(0));
        assertThrows(IllegalArgumentException.class, () -> kupon.setPersentase(200));
    }

    @Test
    void testSetPersentaseValid() {
        kupon.setPersentase(20);
        assertEquals(20, kupon.getPersentase());
    }

    @Test
    void testSetDeskripsiInvalid() {
        assertThrows(IllegalArgumentException.class, () -> kupon.setDeskripsi(""));
        assertThrows(IllegalArgumentException.class, () -> kupon.setDeskripsi(null));
    }

    @Test
    void testSetDeskripsiValid() {
        kupon.setDeskripsi("Updated Description");
        assertEquals("Updated Description", kupon.getDeskripsi());
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
        kupon.refreshStatus();
        assertEquals(String.format(
                        "Kupon[Nama Kupon: Kupon Idul Fitri, %s, 10%%, Hingga: 10 October 2026, Status: VALID, Quantity: 2, Kost: [Kos Alamanda, Kos Griya Asri]]",
                        kodeUnik),
                kupon.toString());
    }
}
