package id.cs.ui.advprog.inthecost.model;
import id.cs.ui.advprog.inthecost.exception.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class KostModelTest {
    // 1. Test Constructor tanpa parameter (kosID harus auto-generate)
    @Test
    public void testConstructorTanpaParameter() {
        Kost kost = new Kost();

        // Pastikan kosID auto-generated
        assertNotNull(kost.getKostID(), "kosID seharusnya otomatis ter-generate");
    }

    // 2. Test Constructor dengan parameter
    @Test
    public void testConstructorDenganParameter() {
        String nama = "Kos Mawar";
        String alamat = "Jl. Melati No. 5";
        String deskripsi = "Kos nyaman dekat kampus";
        int jumlahKamar = 10;
        int hargaPerBulan = 750000;

        Kost kost = new Kost(nama, alamat, deskripsi, jumlahKamar, hargaPerBulan);

        assertNotNull(kost.getKostID(), "kosID harus ter-generate");
        assertEquals(nama, kost.getNama());
        assertEquals(alamat, kost.getAlamat());
        assertEquals(deskripsi, kost.getDeskripsi());
        assertEquals(jumlahKamar, kost.getJumlahKamar());
        assertEquals(hargaPerBulan, kost.getHargaPerBulan());
    }

    // 3. Test Setter dan Getter
    @Test
    public void testSetterDanGetter() {
        Kost kost = new Kost();

        kost.setNama("Kos Anggrek");
        kost.setAlamat("Jl. Kenanga No. 12");
        kost.setDeskripsi("Kos sejuk dan bersih");
        kost.setJumlahKamar(5);
        kost.setHargaPerBulan(650000);

        assertEquals("Kos Anggrek", kost.getNama());
        assertEquals("Jl. Kenanga No. 12", kost.getAlamat());
        assertEquals("Kos sejuk dan bersih", kost.getDeskripsi());
        assertEquals(5, kost.getJumlahKamar());
        assertEquals(650000, kost.getHargaPerBulan());
    }

    // 4. Test Harga per bulan Harus Positif
    @Test
    public void testHargaPerBulanHarusPositif() {
        // Menguji setHargaPerBulan dengan nilai negatif
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            Kost kost = new Kost("Kos Melati", "Jl. Melati No. 10", "Kos nyaman", 10, 500000);
            kost.setHargaPerBulan(-1000);  // Set harga per bulan negatif
        });
        assertEquals(ValidationErrorCode.ZERO_OR_NEGATIVE_VALUE.getCode(), exception.getErrorCode());

        // Menguji setHargaPerBulan dengan nilai 0
        exception = assertThrows(ValidationException.class, () -> {
            Kost kost = new Kost("Kos Melati", "Jl. Melati No. 10", "Kos nyaman", 10, 500000);
            kost.setHargaPerBulan(0);  // Set harga per bulan 0
        });
        assertEquals(ValidationErrorCode.ZERO_OR_NEGATIVE_VALUE.getCode(), exception.getErrorCode());
    }

    // 5. Test Jumlah kamar tidak boleh negatif
    @Test
    public void testJumlahKamarTidakBolehNegatif() {
        // Menguji setJumlahKamar dengan nilai negatif
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            Kost kost = new Kost("Kos Cendana", "Jl. Cendana No. 20", "Kos bersih dan rapi", 10, 600000);
            kost.setJumlahKamar(-5);  // Set jumlah kamar negatif
        });
        assertEquals(ValidationErrorCode.NEGATIVE_VALUE.getCode(), exception.getErrorCode());
    }

    // 6. Test Deskripsi tidak boleh kosong
    @Test
    public void testDeskripsiTidakBolehKosong() {
        // Menguji setDeskripsi dengan deskripsi kosong
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            Kost kost = new Kost("Kos Merah", "Jl. Merah No. 15", "", 5, 450000);
            kost.setDeskripsi("");  // Set deskripsi kosong
        });
        assertEquals(ValidationErrorCode.NULL_OR_EMPTY_VALUE.getCode(), exception.getErrorCode());

        // Menguji setDeskripsi dengan deskripsi null
        exception = assertThrows(ValidationException.class, () -> {
            Kost kost = new Kost("Kos Merah", "Jl. Merah No. 15", null, 5, 450000);
            kost.setDeskripsi(null);  // Set deskripsi null
        });
        assertEquals(ValidationErrorCode.NULL_OR_EMPTY_VALUE.getCode(), exception.getErrorCode());
    }

    // 7. Test KosID auto-generate berbeda untuk setiap instance
    @Test
    public void testKosIDUnik() {
        Kost kost1 = new Kost("Kos A", "Jl. A", "Dekat kampus", 12, 850000);
        Kost kost2 = new Kost("Kos B", "Jl. B", "Dekat pusat perbelanjaan", 8, 900000);

        assertNotEquals(kost1.getKostID(), kost2.getKostID(), "KosID harus unik untuk setiap instance");
    }

    // 8. Test untuk nama tidak boleh kosong
    @Test
    public void testNamaTidakBolehKosong() {
        // Memeriksa jika nama kosong melempar exception
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            Kost kost = new Kost();
            kost.setNama("");  // Set nama kosong
        });
        assertEquals(ValidationErrorCode.NULL_OR_EMPTY_VALUE.getCode(), exception.getErrorCode());

        // Memeriksa jika nama null melempar exception
        exception = assertThrows(ValidationException.class, () -> {
            Kost kost = new Kost();
            kost.setNama(null);  // Set nama null
        });
        assertEquals(ValidationErrorCode.NULL_OR_EMPTY_VALUE.getCode(), exception.getErrorCode());
    }

    // Test untuk alamat tidak boleh kosong
    @Test
    public void testAlamatTidakBolehKosong() {
        // Memeriksa jika alamat kosong melempar exception
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            Kost kost = new Kost();
            kost.setAlamat("");  // Set alamat kosong
        });
        assertEquals(ValidationErrorCode.NULL_OR_EMPTY_VALUE.getCode(), exception.getErrorCode());

        // Memeriksa jika alamat null melempar exception
        exception = assertThrows(ValidationException.class, () -> {
            Kost kost = new Kost();
            kost.setAlamat(null);  // Set alamat null
        });
        assertEquals(ValidationErrorCode.NULL_OR_EMPTY_VALUE.getCode(), exception.getErrorCode());
    }
}
