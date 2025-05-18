package id.cs.ui.advprog.inthecost.controller;

import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.Kupon;
import id.cs.ui.advprog.inthecost.model.Role;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.repository.KostRepository;
import id.cs.ui.advprog.inthecost.repository.KuponRepository;
import id.cs.ui.advprog.inthecost.repository.UserRepository;
import id.cs.ui.advprog.inthecost.service.KuponService;
import id.cs.ui.advprog.inthecost.service.KuponServiceImpl;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ComponentScan(basePackages = "id.cs.ui.advprog.inthecost")
public class KuponControllerTest {

    @InjectMocks
    KuponController kuponController;

    @Autowired
    private EntityManager entityManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private KostRepository kostRepository;

    @Mock
    private KuponServiceImpl kuponService;

    @Mock
    private KuponRepository kuponRepository;

    public User admin;

    public Kost kos1;
    @Autowired
    private ResourceLoader resourceLoader;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);

        Role adminRole = new Role("Admin");
        entityManager.persist(adminRole);

        Set<Role> role1 = new HashSet<>();
        role1.add(adminRole);

        admin = new User("Admin", "123456", "admin@example.com", role1);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                    User user = invocation.getArgument(0);
                    if (user.getId() == null) {
                        user.setId(UUID.randomUUID());
                    }
                    return user;
                }
        );
        userRepository.save(admin);

        kos1 = new Kost("Kos Alamanda", "Jl. Melati No. 1", "Nyaman", 10, 1000000);
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
    }
    private void injectId(Kupon kupon, UUID id) {
        try {
            Field field = Kupon.class.getDeclaredField("idKupon");
            field.setAccessible(true);
            field.set(kupon, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void testCreateKupon(){
        KuponController.KuponRequest kuponRequest = new KuponController.KuponRequest();

        kuponRequest.setPemilik(admin.getId());
        kuponRequest.setKosPemilik(List.of(kos1.getKostID()));
        kuponRequest.setPersentase(10);
        kuponRequest.setNamaKupon("Kupon Test Controller");
        kuponRequest.setMasaBerlaku(LocalDate.of(2026, 7, 12));
        kuponRequest.setDeskripsi("Test Kupon For Controller Test");

        Kupon dummyKupon = new Kupon(
                admin,
                List.of(kos1),
                kuponRequest.getNamaKupon(),
                kuponRequest.getMasaBerlaku(),
                kuponRequest.getPersentase(),
                kuponRequest.getDeskripsi()
        );

        when(userRepository.findById(admin.getId())).thenReturn(java.util.Optional.of(admin));
        when(kostRepository.findAllById(kuponRequest.getKosPemilik())).thenReturn(List.of(kos1));
        when(kuponService.createKupon(any(Kupon.class))).thenReturn(dummyKupon);

        var response = kuponController.createKupon(kuponRequest);

        assert response.getStatusCode().is2xxSuccessful();
        assert response.getBody() != null;
        assert response.getBody().getNamaKupon().equals("Kupon Test Controller");
        assert response.getBody().getPersentase() == 10;
    }

    @Test
    void testUpdateKupon() {
        KuponController.KuponRequest createRequest = new KuponController.KuponRequest();
        createRequest.setPemilik(admin.getId());
        createRequest.setKosPemilik(List.of(kos1.getKostID()));
        createRequest.setPersentase(10);
        createRequest.setNamaKupon("Kupon Test Controller");
        createRequest.setMasaBerlaku(LocalDate.of(2026, 7, 12));
        createRequest.setDeskripsi("Test Kupon For Controller Test");

        Kupon dummyCreate = new Kupon(
                admin,
                List.of(kos1),
                createRequest.getNamaKupon(),
                createRequest.getMasaBerlaku(),
                createRequest.getPersentase(),
                createRequest.getDeskripsi()
        );
        injectId(dummyCreate, UUID.fromString("adf47413-df12-426e-b06e-0c57e2c69cd5"));

        when(userRepository.findById(admin.getId())).thenReturn(java.util.Optional.of(admin));
        when(kostRepository.findAllById(createRequest.getKosPemilik())).thenReturn(List.of(kos1));
        when(kuponService.createKupon(any(Kupon.class))).thenReturn(dummyCreate);

        kuponController.createKupon(createRequest);

        KuponController.KuponRequest editRequest = new KuponController.KuponRequest();
        editRequest.setPemilik(admin.getId());
        editRequest.setKosPemilik(List.of(kos1.getKostID()));
        editRequest.setPersentase(15);
        editRequest.setNamaKupon("Kupon Test Controller Baru");
        editRequest.setMasaBerlaku(LocalDate.of(2027, 7, 12));
        editRequest.setDeskripsi("Test Kupon For Controller Test");

        Kupon dummyUpdate = new Kupon(
                admin,
                List.of(kos1),
                editRequest.getNamaKupon(),
                editRequest.getMasaBerlaku(),
                editRequest.getPersentase(),
                editRequest.getDeskripsi()
        );
        injectId(dummyUpdate, UUID.fromString("adf47413-df12-426e-b06e-0c57e2c69cd5"));

        when(kuponService.getKuponById(dummyUpdate.getIdKupon())).thenReturn(dummyUpdate);
        when(kuponService.updateKupon(dummyUpdate.getIdKupon(), dummyUpdate.getPemilik().getId(), dummyUpdate.getKosPemilik().stream().map(Kost::getKostID).collect(Collectors.toList()), dummyUpdate.getPersentase(), dummyUpdate.getNamaKupon(),
                dummyUpdate.getMasaBerlaku(), dummyUpdate.getDeskripsi())).thenReturn(dummyUpdate);

        var response = kuponController.updateKupon(dummyUpdate.getIdKupon(), editRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Kupon Test Controller Baru", response.getBody().getNamaKupon());
        assertEquals(15, response.getBody().getPersentase());
    }

    @Test
    void testDeleteKupon(){
        KuponController.KuponRequest createRequest = new KuponController.KuponRequest();
        createRequest.setPemilik(admin.getId());createRequest.setKosPemilik(List.of(kos1.getKostID()));createRequest.setPersentase(10);createRequest.setNamaKupon("Kupon Test Controller");createRequest.setMasaBerlaku(LocalDate.of(2026, 7, 12));createRequest.setDeskripsi("Test Kupon For Controller Test");

        Kupon dummyData = new Kupon(
                admin,
                List.of(kos1),
                createRequest.getNamaKupon(),
                createRequest.getMasaBerlaku(),
                createRequest.getPersentase(),
                createRequest.getDeskripsi()
        );
        injectId(dummyData, UUID.fromString("adf47413-df12-426e-b06e-0c57e2c69cd5"));

        when(userRepository.findById(admin.getId())).thenReturn(java.util.Optional.of(admin));
        when(kostRepository.findAllById(createRequest.getKosPemilik())).thenReturn(List.of(kos1));
        when(kuponService.createKupon(any(Kupon.class))).thenReturn(dummyData);

        kuponController.createKupon(createRequest);
        when(kuponService.getKuponById(dummyData.getIdKupon())).thenReturn(dummyData);

        var response = kuponController.deleteKupon(dummyData.getIdKupon());

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testGetKuponById(){
        KuponController.KuponRequest createRequest = new KuponController.KuponRequest();
        createRequest.setPemilik(admin.getId());createRequest.setKosPemilik(List.of(kos1.getKostID()));createRequest.setPersentase(10);createRequest.setNamaKupon("Kupon Test Controller");createRequest.setMasaBerlaku(LocalDate.of(2026, 7, 12));createRequest.setDeskripsi("Test Kupon For Controller Test");

        Kupon dummyData = new Kupon(
                admin,
                List.of(kos1),
                createRequest.getNamaKupon(),
                createRequest.getMasaBerlaku(),
                createRequest.getPersentase(),
                createRequest.getDeskripsi()
        );
        injectId(dummyData, UUID.fromString("adf47413-df12-426e-b06e-0c57e2c69cd5"));
        when(userRepository.findById(admin.getId())).thenReturn(java.util.Optional.of(admin));
        when(kostRepository.findAllById(createRequest.getKosPemilik())).thenReturn(List.of(kos1));
        when(kuponService.createKupon(any(Kupon.class))).thenReturn(dummyData);

        kuponController.createKupon(createRequest);
        when(kuponService.getKuponById(dummyData.getIdKupon())).thenReturn(dummyData);

        var response = kuponController.getKuponById(UUID.fromString("adf47413-df12-426e-b06e-0c57e2c69cd5"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Kupon Test Controller", response.getBody().getNamaKupon());
        assertEquals(LocalDate.of(2026, 7, 12), response.getBody().getMasaBerlaku());
    }

    @Test
    void testGetAllKupon(){
        KuponController.KuponRequest createRequest = new KuponController.KuponRequest();
        createRequest.setPemilik(admin.getId());createRequest.setKosPemilik(List.of(kos1.getKostID()));createRequest.setPersentase(10);createRequest.setNamaKupon("Kupon Test Controller 1");createRequest.setMasaBerlaku(LocalDate.of(2026, 7, 12));createRequest.setDeskripsi("Test Kupon For Controller Test");

        Kupon dummyData = new Kupon(
                admin,
                List.of(kos1),
                createRequest.getNamaKupon(),
                createRequest.getMasaBerlaku(),
                createRequest.getPersentase(),
                createRequest.getDeskripsi()
        );
        injectId(dummyData, UUID.fromString("adf47413-df12-426e-b06e-0c57e2c69cd5"));
        when(userRepository.findById(admin.getId())).thenReturn(java.util.Optional.of(admin));
        when(kostRepository.findAllById(createRequest.getKosPemilik())).thenReturn(List.of(kos1));
        when(kuponService.createKupon(any(Kupon.class))).thenReturn(dummyData);

        kuponController.createKupon(createRequest);

        KuponController.KuponRequest createRequest1 = new KuponController.KuponRequest();
        createRequest.setPemilik(admin.getId());createRequest.setKosPemilik(List.of(kos1.getKostID()));createRequest.setPersentase(10);createRequest.setNamaKupon("Kupon Test Controller 2");createRequest.setMasaBerlaku(LocalDate.of(2026, 7, 12));createRequest.setDeskripsi("Test Kupon For Controller Test");

        Kupon dummyData1 = new Kupon(
                admin,
                List.of(kos1),
                createRequest.getNamaKupon(),
                createRequest.getMasaBerlaku(),
                createRequest.getPersentase(),
                createRequest.getDeskripsi()
        );
        injectId(dummyData1, UUID.fromString("adf47413-df12-426e-b06e-0c57e2c69ea9"));

        kuponController.createKupon(createRequest1);
        when(kuponService.getAllKupon()).thenReturn(List.of(dummyData, dummyData1));

        ResponseEntity<List<KuponController.KuponResponse>> response = kuponController.getAllKupon();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        KuponController.KuponResponse response1 = response.getBody().get(0);
        assertEquals("Kupon Test Controller 1", response1.getNamaKupon());

        KuponController.KuponResponse response2 = response.getBody().get(1);
        assertEquals("Kupon Test Controller 2", response2.getNamaKupon());
    }
}
