package id.cs.ui.advprog.inthecost.repository;

import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.Kupon;
import id.cs.ui.advprog.inthecost.model.Role;
import id.cs.ui.advprog.inthecost.model.User;

import jakarta.persistence.EntityManager;
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
public class KuponRepositoryTest {
    @Autowired
    private KuponRepository kuponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KostRepository kostRepository;

    @Autowired
    private EntityManager entityManager;

    List<Kupon> kuponList = new ArrayList<>();

    @BeforeEach
    void setUp(){
        Role adminRole = new Role("Admin");
        Role pemilikKos = new Role("Pemilik Kos");

        entityManager.persist(adminRole);
        entityManager.persist(pemilikKos);

        Set<Role> role1 = new HashSet<>();
        role1.add(adminRole);
        Set<Role> role2 = new HashSet<>();
        role2.add(pemilikKos);

        User admin = new User("Admin", "123456", "admin@example.com", role1);
        User pemilik1 = new User("Ahmad Suardjo", "Ahm@d321!", "ahmadss@gmail.com", role2);
        User pemilik2 = new User("Janice Lim", "J@N1C3005", "janicelim@gmail.com", role2);
        userRepository.save(admin);
        userRepository.save(pemilik1);
        userRepository.save(pemilik2);

        Kost kos1 = new Kost("Kos Alamanda", "Jl. Melati No. 1", "Nyaman", 10, 1000000);
        Kost kos2 = new Kost("Kos Griya Asri", "Jl. Anggrek No. 2", "Asri", 8, 950000);
        Kost kos3 = new Kost("Kos Amara 51/2", "Jl. KH. Ahmad Dahlan No. 120", "Mantap", 22, 1900000);
        Kost kos4 = new Kost("Kos Zano's", "Jl. Juragan Sinda III No. 19", "Parkiran aman dan lingkungan tentram", 2, 1100000);
        Kost kos5 = new Kost("Kos Pinisia", "Jl. Sadewa IV No. 13", "Tetangga ramah dan Asri", 12, 1500000);
        kostRepository.save(kos1);
        kostRepository.save(kos2);
        kostRepository.save(kos3);
        kostRepository.save(kos4);
        kostRepository.save(kos5);

        Kupon kupon1 = new Kupon(admin, List.of(kos1,kos2, kos3, kos4, kos5), LocalDate.of(2026, 10, 15), 7, "Kupon Hari Pahlawan 2025", true);
        Kupon kupon2 = new Kupon(pemilik1, List.of(kos1, kos2, kos3), LocalDate.of(2025, 10, 20), 12, "Promo Pak Ahmad", false);
        Kupon kupon3 = new Kupon(pemilik2, List.of(kos4, kos5), LocalDate.of(2026, 2, 10), 10, "Kupon Semester Baru", false);
        kuponList.add(kupon1);
        kuponList.add(kupon2);
        kuponList.add(kupon3);
    }

    @Test
    void testSaveCreateKupon(){
        Kupon kupon = kuponList.get(1); // Kupon milik pemilik1
        Kupon savedKupon = kuponRepository.save(kupon);

        Optional<Kupon> findResult = kuponRepository.findById(savedKupon.getIdKupon());
        assertThat(findResult).isPresent();
        assertThat(findResult.get().getDeskripsi()).isEqualTo("Promo Pak Ahmad");
    }

    @Test
    void testUpdateKupon(){
        Kupon kupon = kuponRepository.save(kuponList.get(2));
        UUID kuponId = kupon.getIdKupon();

        kupon.setDeskripsi("Update Kupon Semester Baru");
        kupon.setPersentase(15);
        kuponRepository.save(kupon);

        Optional<Kupon> updatedKupon = kuponRepository.findById(kuponId);
        assertThat(updatedKupon).isPresent();
        assertThat(updatedKupon.get().getDeskripsi()).isEqualTo("Update Kupon Semester Baru");
        assertThat(updatedKupon.get().getPersentase()).isEqualTo(15);
    }

    @Test
    void testFindById(){
        Kupon saved = kuponRepository.save(kuponList.get(0)); // Simpan kupon admin
        Optional<Kupon> result = kuponRepository.findById(saved.getIdKupon());

        assertThat(result).isPresent();
        assertThat(result.get().getDeskripsi()).isEqualTo("Kupon Hari Pahlawan 2025");
        assertThat(result.get().getKodeUnik()).isNotBlank();
    }

    @Test
    void testDeleteById() {
        Kupon kupon = kuponRepository.save(kuponList.get(1));
        UUID id = kupon.getIdKupon();

        kuponRepository.deleteById(id);

        Optional<Kupon> deleted = kuponRepository.findById(id);
        assertThat(deleted).isEmpty();
    }

    @Test
    void testFindAll() {
        kuponRepository.saveAll(kuponList);
        List<Kupon> result = kuponRepository.findAll();

        assertThat(result).hasSizeGreaterThanOrEqualTo(3); // Karena semua disimpan
        assertThat(result.stream().anyMatch(k -> k.getDeskripsi().equals("Promo Pak Ahmad"))).isTrue();
        assertThat(result.stream().anyMatch(k -> k.getDeskripsi().equals("Kupon Semester Baru"))).isTrue();
        assertThat(result.stream().anyMatch(k -> k.getDeskripsi().equals("Kupon Hari Pahlawan 2025"))).isTrue();
    }
}
