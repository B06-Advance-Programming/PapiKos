package id.cs.ui.advprog.inthecost.repository;

import id.cs.ui.advprog.inthecost.model.Kost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class KostRepositoryTest {

    @Autowired
    private KostRepository kostRepository;

    private Kost kost;

    @BeforeEach
    void setUp() {
        // Membuat data Kost untuk testing
        kost = new Kost("Kos Putri", "Jl. Merdeka No. 10", "Kosan nyaman untuk putri", 10, 1000000);
    }

    @Test
    void testSaveKost() {
        // Simpan objek Kost
        Kost savedKost = kostRepository.save(kost);

        // Pastikan objek yang disimpan memiliki ID yang valid
        assertNotNull(savedKost.getKostID());
        assertEquals(kost.getNama(), savedKost.getNama());
        assertEquals(kost.getAlamat(), savedKost.getAlamat());
        assertEquals(kost.getDeskripsi(), savedKost.getDeskripsi());
        assertEquals(kost.getJumlahKamar(), savedKost.getJumlahKamar());
        assertEquals(kost.getHargaPerBulan(), savedKost.getHargaPerBulan());
    }

    @Test
    void testFindByID() {
        // Simpan objek Kost terlebih dahulu
        Kost savedKost = kostRepository.save(kost);

        // Cari objek Kost berdasarkan ID
        Kost foundKost = kostRepository.findById(savedKost.getKostID()).orElse(null);

        // Pastikan objek yang ditemukan sesuai dengan yang disimpan
        assertNotNull(foundKost);
        assertEquals(savedKost.getKostID(), foundKost.getKostID());
    }

    @Test
    void testFindAll() {
        // Simpan beberapa objek Kost untuk testing
        Kost kost2 = new Kost("Kos Cowok", "Jl. Kemerdekaan No. 12", "Kosan untuk cowok", 8, 1200000);
        kostRepository.save(kost);
        kostRepository.save(kost2);

        // Ambil semua kost dari repository
        List<Kost> kostList = kostRepository.findAll();

        // Pastikan jumlah data yang diambil sesuai
        assertNotNull(kostList);
        assertFalse(kostList.isEmpty());
    }

    @Test
    void testDeleteByID() {
        // Simpan objek Kost terlebih dahulu
        Kost savedKost = kostRepository.save(kost);

        // Hapus objek Kost berdasarkan ID
        kostRepository.deleteById(savedKost.getKostID());

        // Pastikan objek sudah terhapus
        Kost deletedKost = kostRepository.findById(savedKost.getKostID()).orElse(null);
        assertNull(deletedKost);
    }

    @Test
    void testUpdateKostByID() {
        // Simpan objek Kost terlebih dahulu
        Kost savedKost = kostRepository.save(kost);

        // Update properti Kost
        savedKost.setNama("Kos Putri Baru");
        savedKost.setHargaPerBulan(1500000);

        // Simpan objek yang sudah diupdate
        Kost updatedKost = kostRepository.save(savedKost);

        // Pastikan objek yang diupdate memiliki perubahan yang benar
        assertEquals("Kos Putri Baru", updatedKost.getNama());
        assertEquals(1500000, updatedKost.getHargaPerBulan());
    }
}
