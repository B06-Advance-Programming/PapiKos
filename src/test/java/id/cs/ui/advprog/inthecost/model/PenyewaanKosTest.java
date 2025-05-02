package id.cs.ui.advprog.inthecost.model;

import id.cs.ui.advprog.inthecost.builder.PenyewaanKosBuilder;
import id.cs.ui.advprog.inthecost.enums.StatusPenyewaan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class PenyewaanKosTest {

    private PenyewaanKos penyewaan;
    private KosSewa kos;

    @BeforeEach
    public void setUp() {
        kos = new KosSewa();
        kos.setNama("Kos Mawar");
        kos.setAlamat("Jl. Melati No. 2");
        kos.setDeskripsi("Kos nyaman dekat kampus");
        kos.setJumlahKamar(10);
        kos.setHargaSewaBulanan(1500000);

        penyewaan = PenyewaanKosBuilder.builder()
                    .namaLengkap("Budi Santoso")
                    .nomorTelepon("08123456789")
                    .tanggalCheckIn(LocalDate.of(2025, 5, 1))
                    .durasiBulan(6)
                    .kos(kos)
                    .build();
    }

    @Test
    public void testGetNamaLengkap() {
        assertEquals("Budi Santoso", penyewaan.getNamaLengkap());
    }

    @Test
    public void testGetNomorTelepon() {
        assertEquals("08123456789", penyewaan.getNomorTelepon());
    }

    @Test
    public void testGetTanggalCheckIn() {
        assertEquals(LocalDate.of(2025, 5, 1), penyewaan.getTanggalCheckIn());
    }

    @Test
    public void testGetDurasiBulan() {
        assertEquals(6, penyewaan.getDurasiBulan());
    }

    @Test
    public void testGetKosNama() {
        assertEquals("Kos Mawar", penyewaan.getKos().getNama());
    }

    @Test
    public void testGetKosAlamat() {
        assertEquals("Jl. Melati No. 2", penyewaan.getKos().getAlamat());
    }

    @Test
    public void testGetKosDeskripsi() {
        assertEquals("Kos nyaman dekat kampus", penyewaan.getKos().getDeskripsi());
    }

    @Test
    public void testGetKosJumlahKamar() {
        assertEquals(10, penyewaan.getKos().getJumlahKamar());
    }

    @Test
    public void testGetKosHargaSewaBulanan() {
        assertEquals(1500000, kos.getHargaSewaBulanan());
    }

    @Test
    public void testStatusDefaultDiajukan() {
        assertEquals(StatusPenyewaan.DIAJUKAN, penyewaan.getStatus());
    }

    @Test
    public void testBisaEditJikaDiajukan() {
        assertTrue(penyewaan.isEditable());
    }

    @Test
    public void testTidakBisaEditJikaDisetujui() {
        penyewaan.setStatus(StatusPenyewaan.DISETUJUI);
        assertFalse(penyewaan.isEditable());
    }

    @Test
    public void testTidakBisaEditJikaDibatalkan() {
        penyewaan.setStatus(StatusPenyewaan.DIBATALKAN);
        assertFalse(penyewaan.isEditable());
    }
}
