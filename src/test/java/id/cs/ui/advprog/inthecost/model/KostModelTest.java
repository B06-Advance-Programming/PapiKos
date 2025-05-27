package id.cs.ui.advprog.inthecost.model;

import id.cs.ui.advprog.inthecost.exception.*;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class KostModelTest {
    // 1. Test Constructor tanpa parameter (kostID harus auto-generate)
    @Test
    public void testConstructorTanpaParameter() {
        Kost kost = new Kost();
        assertNotNull(kost.getKostID(), "kostID seharusnya otomatis ter-generate");
    }

    // 2. Test Konstruktor dengan parameter (valid input)
    @Test
    public void testConstructorDenganParameter() {
        String nama = "Kos Mawar";
        String alamat = "Jl. Melati No. 5";
        String deskripsi = "Kos nyaman dekat kampus";
        int jumlahKamar = 10;
        int hargaPerBulan = 750000;

        Kost kost = new Kost(nama, alamat, deskripsi, jumlahKamar, hargaPerBulan);

        assertNotNull(kost.getKostID(), "kostID harus ter-generate");
        assertEquals(nama, kost.getNama());
        assertEquals(alamat, kost.getAlamat());
        assertEquals(deskripsi, kost.getDeskripsi());
        assertEquals(jumlahKamar, kost.getJumlahKamar());
        assertEquals(hargaPerBulan, kost.getHargaPerBulan());
    }

    // 2b. Test Konstruktor dengan ownerId (valid input)
    @Test
    public void testConstructorDenganOwnerId() {
        UUID ownerId = UUID.randomUUID();
        Kost kost = new Kost("Kos Bunga", "Jl. Anggrek No.7", "Kos nyaman", 8, 600000, ownerId);
        assertEquals(ownerId, kost.getOwnerId());
    }

    // 3. Test Setter dan Getter biasa (valid)
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

    // 4. Test Harga per bulan Harus Positif (setter)
    @Test
    public void testHargaPerBulanHarusPositif() {
        Kost kost1 = new Kost("Kos Melati", "Jl. Melati No. 10", "Kos nyaman", 10, 500000);
        ValidationException exception = assertThrows(ValidationException.class, () -> kost1.setHargaPerBulan(-1000));
        assertEquals(ValidationErrorCode.ZERO_OR_NEGATIVE_VALUE.getCode(), exception.getErrorCode());

        Kost kost2 = new Kost("Kos Melati", "Jl. Melati No. 10", "Kos nyaman", 10, 500000);
        exception = assertThrows(ValidationException.class, () -> kost2.setHargaPerBulan(0));
        assertEquals(ValidationErrorCode.ZERO_OR_NEGATIVE_VALUE.getCode(), exception.getErrorCode());
    }

    // 5. Test Jumlah kamar tidak boleh negatif (setter)
    @Test
    public void testJumlahKamarTidakBolehNegatif() {
        Kost kost = new Kost("Kos Cendana", "Jl. Cendana No. 20", "Kos bersih dan rapi", 10, 600000);
        ValidationException exception = assertThrows(ValidationException.class, () -> kost.setJumlahKamar(-5));
        assertEquals(ValidationErrorCode.NEGATIVE_VALUE.getCode(), exception.getErrorCode());
    }

    // 6. Test Deskripsi tidak boleh kosong (setter)
    @Test
    public void testDeskripsiTidakBolehKosong() {
        Kost kost1 = new Kost("Kos Merah", "Jl. Merah No. 15", "Valid desc", 5, 450000);
        ValidationException exception = assertThrows(ValidationException.class, () -> kost1.setDeskripsi(""));
        assertEquals(ValidationErrorCode.NULL_OR_EMPTY_VALUE.getCode(), exception.getErrorCode());

        Kost kost2 = new Kost("Kos Merah", "Jl. Merah No. 15", "Valid desc", 5, 450000);
        exception = assertThrows(ValidationException.class, () -> kost2.setDeskripsi(null));
        assertEquals(ValidationErrorCode.NULL_OR_EMPTY_VALUE.getCode(), exception.getErrorCode());
    }

    // 7. Test kostID unik untuk tiap instance
    @Test
    public void testKostIDUnik() {
        Kost kost1 = new Kost("Kos A", "Jl. A", "Dekat kampus", 12, 850000);
        Kost kost2 = new Kost("Kos B", "Jl. B", "Dekat pusat perbelanjaan", 8, 900000);

        assertNotEquals(kost1.getKostID(), kost2.getKostID(), "kostID harus unik untuk setiap instance");
    }

    // 8. Test nama tidak boleh kosong (setter)
    @Test
    public void testNamaTidakBolehKosong() {
        Kost kost1 = new Kost();
        ValidationException exception = assertThrows(ValidationException.class, () -> kost1.setNama(""));
        assertEquals(ValidationErrorCode.NULL_OR_EMPTY_VALUE.getCode(), exception.getErrorCode());

        Kost kost2 = new Kost();
        exception = assertThrows(ValidationException.class, () -> kost2.setNama(null));
        assertEquals(ValidationErrorCode.NULL_OR_EMPTY_VALUE.getCode(), exception.getErrorCode());
    }

    // 9. Test alamat tidak boleh kosong (setter)
    @Test
    public void testAlamatTidakBolehKosong() {
        Kost kost1 = new Kost();
        ValidationException exception = assertThrows(ValidationException.class, () -> kost1.setAlamat(""));
        assertEquals(ValidationErrorCode.NULL_OR_EMPTY_VALUE.getCode(), exception.getErrorCode());

        Kost kost2 = new Kost();
        exception = assertThrows(ValidationException.class, () -> kost2.setAlamat(null));
        assertEquals(ValidationErrorCode.NULL_OR_EMPTY_VALUE.getCode(), exception.getErrorCode());
    }

    // 10. Test constructor parameter gagal validasi nama kosong
    @Test
    public void testConstructorNamaKosongGagal() {
        ValidationException exception = assertThrows(ValidationException.class, () ->
                new Kost("", "Alamat", "Deskripsi", 1, 1000)
        );
        assertEquals(ValidationErrorCode.NULL_OR_EMPTY_VALUE.getCode(), exception.getErrorCode());
    }

    // 11. Test constructor parameter gagal validasi alamat kosong
    @Test
    public void testConstructorAlamatKosongGagal() {
        ValidationException exception = assertThrows(ValidationException.class, () ->
                new Kost("Nama", " ", "Deskripsi", 1, 1000)
        );
        assertEquals(ValidationErrorCode.NULL_OR_EMPTY_VALUE.getCode(), exception.getErrorCode());
    }

    // 12. Test constructor parameter gagal validasi deskripsi kosong
    @Test
    public void testConstructorDeskripsiKosongGagal() {
        ValidationException exception = assertThrows(ValidationException.class, () ->
                new Kost("Nama", "Alamat", "", 1, 1000)
        );
        assertEquals(ValidationErrorCode.NULL_OR_EMPTY_VALUE.getCode(), exception.getErrorCode());
    }

    // 13. Test constructor parameter gagal validasi jumlah kamar negatif
    @Test
    public void testConstructorJumlahKamarNegatifGagal() {
        ValidationException exception = assertThrows(ValidationException.class, () ->
                new Kost("Nama", "Alamat", "Deskripsi", -1, 1000)
        );
        assertEquals(ValidationErrorCode.NEGATIVE_VALUE.getCode(), exception.getErrorCode());
    }

    // 14. Test constructor parameter gagal validasi harga per bulan nol atau negatif
    @Test
    public void testConstructorHargaPerBulanNegatifGagal() {
        ValidationException exception = assertThrows(ValidationException.class, () ->
                new Kost("Nama", "Alamat", "Deskripsi", 1, 0)
        );
        assertEquals(ValidationErrorCode.ZERO_OR_NEGATIVE_VALUE.getCode(), exception.getErrorCode());

        exception = assertThrows(ValidationException.class, () ->
                new Kost("Nama", "Alamat", "Deskripsi", 1, -100)
        );
        assertEquals(ValidationErrorCode.ZERO_OR_NEGATIVE_VALUE.getCode(), exception.getErrorCode());
    }

    // 15. Test constructor with ownerId null fails
    @Test
    public void testConstructorOwnerIdNullThrows() {
        ValidationException exception = assertThrows(ValidationException.class, () ->
                new Kost("Nama", "Alamat", "Deskripsi", 1, 1000, null)
        );
        assertEquals(ValidationErrorCode.NULL_OR_EMPTY_VALUE.getCode(), exception.getErrorCode());
    }

    // 16. Test setOwnerId validation
    @Test
    public void testSetOwnerIdValidation() {
        Kost kost = new Kost();
        ValidationException exception = assertThrows(ValidationException.class, () -> kost.setOwnerId(null));
        assertEquals(ValidationErrorCode.NULL_OR_EMPTY_VALUE.getCode(), exception.getErrorCode());

        UUID ownerId = UUID.randomUUID();
        kost.setOwnerId(ownerId);
        assertEquals(ownerId, kost.getOwnerId());
    }

    // 17. Test setKostID setter (coverage for setter)
    @Test
    public void testSetKostID() {
        Kost kost = new Kost();
        UUID newId = UUID.randomUUID();
        kost.setKostID(newId);
        assertEquals(newId, kost.getKostID());
    }
}