package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.Kupon;
import id.cs.ui.advprog.inthecost.model.Role;
import id.cs.ui.advprog.inthecost.model.User;

import id.cs.ui.advprog.inthecost.repository.KostRepository;
import id.cs.ui.advprog.inthecost.repository.KuponRepository;
import id.cs.ui.advprog.inthecost.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
@Transactional
@ComponentScan(basePackages = "id.cs.ui.advprog.inthecost")
public class KuponServiceTest{
    @InjectMocks
    KuponServiceImpl kuponService;

    @Mock
    KuponRepository kuponRepository;

    @Autowired
    private EntityManager entityManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private KostRepository kostRepository;

    List<Kupon> kuponList = new ArrayList<>();

    @BeforeEach
    void setUp() {
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
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    if (user.getId() == null) {
                        user.setId(UUID.randomUUID());
                    }
                    return user;
                }
        );
        userRepository.save(admin);
        userRepository.save(pemilik1);
        userRepository.save(pemilik2);

        Kost kos1 = new Kost("Kos Alamanda", "Jl. Melati No. 1", "Nyaman", 10, 1000000);
        Kost kos2 = new Kost("Kos Griya Asri", "Jl. Anggrek No. 2", "Asri", 8, 950000);
        Kost kos3 = new Kost("Kos Amara 51/2", "Jl. KH. Ahmad Dahlan No. 120", "Mantap", 22, 1900000);
        Kost kos4 = new Kost("Kos Zano's", "Jl. Juragan Sinda III No. 19", "Parkiran aman dan lingkungan tentram", 2, 1100000);
        Kost kos5 = new Kost("Kos Pinisia", "Jl. Sadewa IV No. 13", "Tetangga ramah dan Asri", 12, 1500000);
        when(kostRepository.save(any(Kost.class))).thenAnswer(invocation -> {
                    Kost kos = invocation.getArgument(0);
                    if (kos.getKostID() == null) {
                        Field field = Kost.class.getDeclaredField("kostID");
                        field.setAccessible(true);
                        field.set(kos, UUID.randomUUID());
                    }
                    return kos;
                }
        );
        kostRepository.save(kos1);
        kostRepository.save(kos2);
        kostRepository.save(kos3);
        kostRepository.save(kos4);
        kostRepository.save(kos5);

        kuponList.add(new Kupon(admin, List.of(kos1, kos2, kos3, kos4, kos5), "Kupon Pahlawan",LocalDate.of(2026, 10, 15), 7, "Kupon Hari Pahlawan 2025"));
        kuponList.add(new Kupon(pemilik1, List.of(kos1, kos2, kos3), "Kupon Ahmad",LocalDate.of(2025, 10, 20), 12, "Promo Pak Ahmad"));
        kuponList.add(new Kupon(pemilik2, List.of(kos4, kos5), "Kupon Maba",LocalDate.of(2026, 2, 10), 10, "Kupon Semester Baru"));
    }

    @Test
    void testCreateKupon(){
        Kupon selectedKupon = kuponList.getFirst();
        when(kuponRepository.save(any(Kupon.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Kupon inputKupon = kuponService.createKupon(selectedKupon);

        assertThat(inputKupon.getIdKupon()).isEqualTo(selectedKupon.getIdKupon());
        assertThat(inputKupon.getKodeUnik()).isEqualTo(selectedKupon.getKodeUnik());
    }

    @Test
    void testUpdateKuponSuccess() {
        // Setup test data
        Kupon selectedKupon = kuponList.getFirst();

        // Mock the save operation
        when(kuponRepository.save(any(Kupon.class))).thenAnswer(invocation -> {
            Kupon kupon = invocation.getArgument(0);
            if (kupon.getIdKupon() == null) {
                try {
                    Field field = Kupon.class.getDeclaredField("idKupon");
                    field.setAccessible(true);
                    field.set(kupon, UUID.randomUUID());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return kupon;
        });

        // Create initial kupon
        Kupon inputKupon = kuponService.createKupon(selectedKupon);

        UUID pemilikId = inputKupon.getPemilik().getId();
        List<UUID> kostIds = inputKupon.getKosPemilik().stream()
                .map(Kost::getKostID)
                .collect(Collectors.toList());

        when(kuponRepository.findById(inputKupon.getIdKupon()))
                .thenReturn(Optional.of(inputKupon));

        when(userRepository.findById(pemilikId))
                .thenReturn(Optional.of(inputKupon.getPemilik()));

        when(kostRepository.findAllById(kostIds))
                .thenReturn(new ArrayList<>(inputKupon.getKosPemilik()));

        // Perform update
        Kupon updatedKupon = kuponService.updateKupon(
                inputKupon.getIdKupon(),
                pemilikId,
                kostIds,
                25,
                "Kupon UI",
                LocalDate.of(2027, 10, 22),
                "Kupon Khusus Mahasiswa Universitas Indonesia"
        );

        // Verify results
        assertThat(updatedKupon.getIdKupon()).isEqualTo(inputKupon.getIdKupon());
        assertThat(updatedKupon.getKodeUnik()).isEqualTo(inputKupon.getKodeUnik());
        assertThat(updatedKupon.getPersentase()).isEqualTo(25);
        assertThat(updatedKupon.getMasaBerlaku()).isEqualTo(LocalDate.of(2027, 10, 22));
        assertThat(updatedKupon.getDeskripsi()).isEqualTo("Kupon Khusus Mahasiswa Universitas Indonesia");
    }


    @Test
    void testUpdateKuponFail() {
        Kupon selectedKupon = kuponList.get(0);

        when(kuponRepository.findById(kuponList.get(1).getIdKupon()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> kuponService.updateKupon(kuponList.get(1).getIdKupon(), kuponList.get(1).getPemilik().getId(), kuponList.get(1).getKosPemilik().stream().map(Kost::getKostID).collect(Collectors.toList()), kuponList.get(1).getPersentase(), kuponList.get(1).getNamaKupon(), kuponList.get(1).getMasaBerlaku(), kuponList.get(1).getDeskripsi()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testGetKuponByIdSuccess() {
        Kupon selectedKupon = kuponList.get(2);
        UUID id = selectedKupon.getIdKupon();

        when(kuponRepository.findById(id)).thenReturn(Optional.of(selectedKupon));

        Kupon result = kuponService.getKuponById(id);

        assertThat(result).isNotNull();
        assertThat(result.getIdKupon()).isEqualTo(id);
        assertThat(result).isEqualTo(selectedKupon);
    }

    @Test
    void testGetKuponByIdFail() {
        UUID invalidId = UUID.randomUUID();

        when(kuponRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> kuponService.getKuponById(invalidId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("tidak ditemukan");
    }


    @Test
    void testGetKuponByKodeUnikSuccess() {
        Kupon selectedKupon = kuponList.getFirst();
        when(kuponRepository.findByKodeUnik(selectedKupon.getKodeUnik()))
                .thenReturn(Optional.of(selectedKupon));

        Kupon result = kuponService.getKuponByKodeUnik(selectedKupon.getKodeUnik());
        assertThat(result).isEqualTo(selectedKupon);
    }

    @Test
    void testGetKuponByKodeUnikFail() {
        String invalidKode = "INVALID123";
        when(kuponRepository.findByKodeUnik(invalidKode))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> kuponService.getKuponByKodeUnik(invalidKode))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("tidak ditemukan");
    }

    @Test
    void testDeleteKuponSuccess() {
        Kupon selectedKupon = kuponList.get(1);
        UUID id = selectedKupon.getIdKupon();

        when(kuponRepository.existsById(id)).thenReturn(true);
        doNothing().when(kuponRepository).deleteById(id);

        kuponService.deleteKupon(id);

        verify(kuponRepository, times(1)).deleteById(id);
    }

    @Test
    void testDeleteKuponFail() {
        UUID invalidId = UUID.randomUUID();
        when(kuponRepository.existsById(invalidId)).thenReturn(false);

        assertThatThrownBy(() -> kuponService.deleteKupon(invalidId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("tidak ditemukan");
    }

    @Test
    void testGetAllKupon() {
        when(kuponRepository.findAll()).thenReturn(kuponList);

        List<Kupon> result = kuponService.getAllKupon();

        assertThat(result).hasSize(kuponList.size());
        assertThat(result).containsExactlyElementsOf(kuponList);
    }

}
