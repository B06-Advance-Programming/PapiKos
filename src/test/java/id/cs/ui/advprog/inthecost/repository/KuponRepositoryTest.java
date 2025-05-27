package id.cs.ui.advprog.inthecost.repository;

import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.Kupon;
import id.cs.ui.advprog.inthecost.model.User;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ComponentScan(basePackages = "id.cs.ui.advprog.inthecost")
class KuponRepositoryTest {
    @Autowired
    private KuponRepository kuponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KostRepository kostRepository;

    List<Kupon> kuponList = new ArrayList<>();

    @BeforeEach
    void setUp(){
        // Create and save User owner
        User owner = new User("owneruser", "password", "owneruser@example.com", new HashSet<>());
        owner = userRepository.save(owner);

        Kost kos = new Kost();
        kos.setKostID(UUID.fromString("7370b889-e97d-46a2-8e0d-486489777333"));
        kos.setNama("Kos Mawar");
        kos.setAlamat("Jl. Melati No. 2");
        kos.setDeskripsi("Kos nyaman");
        kos.setJumlahKamar(5);
        kos.setHargaPerBulan(1200000);
        kos.setOwnerId(owner.getId());

        Kost kos1 = kostRepository.save(kos);

        Kupon kupon1 = new Kupon(new ArrayList<>(List.of(kos1)), "Kupon Pahlawan", LocalDate.of(2026, 10, 15), 7, "Kupon Hari Pahlawan 2025", 6);
        Kupon kupon2 = new Kupon(new ArrayList<>(List.of(kos1)), "Kupon Maba", LocalDate.of(2026, 10, 22), 8, "Kupon Semester Baru", 2);

        kuponList.add(kupon1);
        kuponList.add(kupon2);
    }

    @Test
    void testSaveCreateKupon(){
        Kupon kupon = kuponList.getFirst();
        Kupon savedKupon = kuponRepository.save(kupon);

        Optional<Kupon> findResult = kuponRepository.findById(savedKupon.getIdKupon());
        assertThat(findResult).isPresent();
        assertThat(findResult.get().getDeskripsi()).isEqualTo("Kupon Hari Pahlawan 2025");
    }

    @Test
    void testUpdateKupon(){
        Kupon kupon = kuponRepository.save(kuponList.getFirst());

        kupon.setDeskripsi("Update Kupon Semester Baru");
        kupon.setPersentase(15);
        Kupon updatedKupon = kuponRepository.save(kupon);

        assertThat(updatedKupon.getDeskripsi()).isEqualTo("Update Kupon Semester Baru");
        assertThat(updatedKupon.getPersentase()).isEqualTo(15);
    }

    @Test
    void testFindById(){
        Kupon saved = kuponRepository.save(kuponList.getFirst()); // Simpan kupon admin
        Optional<Kupon> result = kuponRepository.findById(saved.getIdKupon());

        assertThat(result).isPresent();
        assertThat(result.get().getDeskripsi()).isEqualTo("Kupon Hari Pahlawan 2025");
        assertThat(result.get().getKodeUnik()).isNotBlank();
    }

    @Test
    void testDeleteById() {
        Kupon kupon = kuponRepository.save(kuponList.getFirst());
        UUID id = kupon.getIdKupon();

        kuponRepository.deleteById(id);

        Optional<Kupon> deleted = kuponRepository.findById(id);
        assertThat(deleted).isEmpty();
    }

    @Test
    void testFindAll() {
        kuponRepository.saveAll(kuponList);
        List<Kupon> result = kuponRepository.findAll();

        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
        assertThat(result.stream().anyMatch(k -> k.getDeskripsi().equals("Kupon Semester Baru"))).isTrue();
        assertThat(result.stream().anyMatch(k -> k.getDeskripsi().equals("Kupon Hari Pahlawan 2025"))).isTrue();
    }

    @Test
    void testFindByKostId(){
        kuponRepository.saveAll(kuponList);
        List<Kupon> kupons = kuponRepository.findByKostId(UUID.fromString("7370b889-e97d-46a2-8e0d-486489777333"));
        Kupon kupon = kupons.getFirst();

        assertThat(kupon).isNotNull();
        assertThat(kupon.getNamaKupon()).isEqualTo("Kupon Pahlawan");
        assertThat(kupon.getQuantity()).isEqualTo(6);
    }
}