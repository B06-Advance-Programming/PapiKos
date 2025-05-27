package id.cs.ui.advprog.inthecost.controller;

import id.cs.ui.advprog.inthecost.dto.KuponRequest;
import id.cs.ui.advprog.inthecost.dto.KuponResponse;
import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.Kupon;
import id.cs.ui.advprog.inthecost.repository.KostRepository;
import id.cs.ui.advprog.inthecost.service.KuponServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@ComponentScan(basePackages = "id.cs.ui.advprog.inthecost")
public class KuponControllerTest {

    @InjectMocks
    KuponController kuponController;

    @Mock
    private KostRepository kostRepository;

    @Mock
    private KuponServiceImpl kuponService;

    public Kost kos1;

    @BeforeEach
    void setUp(){
        kos1 = new Kost("Kos Alamanda", "Jl. Melati No. 1", "Nyaman", 10, 1000000);
        UUID kos1Id = UUID.fromString("2c387f5f-9362-4937-8964-acb0e8ccdde5");

        try {
            Field field = Kost.class.getDeclaredField("kostID");
            field.setAccessible(true);
            field.set(kos1, kos1Id);
        } catch (Exception e) { throw new RuntimeException(e); }
    }
    private void injectId(Kupon kupon, UUID id) {
        try {
            Field field = Kupon.class.getDeclaredField("idKupon");
            field.setAccessible(true);
            field.set(kupon, id);
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    private void injectKodeUnik(Kupon kupon, String kodeUnik){
        try {
            Field field = Kupon.class.getDeclaredField("kodeUnik");
            field.setAccessible(true);
            field.set(kupon, kodeUnik);
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    void testCreateKupon(){
        KuponRequest kuponRequest = new KuponRequest();

        kuponRequest.setKosPemilik(List.of(kos1.getKostID()));
        kuponRequest.setPersentase(10);
        kuponRequest.setNamaKupon("Kupon Test Controller");
        kuponRequest.setMasaBerlaku(LocalDate.of(2026, 7, 12));
        kuponRequest.setDeskripsi("Test Kupon For Controller Test");
        kuponRequest.setQuantity(5);

        Kupon dummyKupon = new Kupon(
                List.of(kos1),
                kuponRequest.getNamaKupon(),
                kuponRequest.getMasaBerlaku(),
                kuponRequest.getPersentase(),
                kuponRequest.getDeskripsi(),
                kuponRequest.getQuantity()
        );

        when(kostRepository.findAllById(kuponRequest.getKosPemilik())).thenReturn(List.of(kos1));
        when(kuponService.createKupon(any(Kupon.class))).thenReturn(dummyKupon);

        var response = kuponController.createKupon(kuponRequest);

        assertNotNull(response.getBody());
        assertEquals("Kupon Test Controller", response.getBody().getNamaKupon());
        assertEquals(10, response.getBody().getPersentase());
        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    void testUpdateKupon() {
        KuponRequest createRequest = new KuponRequest();
        createRequest.setKosPemilik(List.of(kos1.getKostID()));
        createRequest.setPersentase(10);
        createRequest.setNamaKupon("Kupon Test Controller");
        createRequest.setMasaBerlaku(LocalDate.of(2026, 7, 12));
        createRequest.setDeskripsi("Test Kupon For Controller Test");
        createRequest.setQuantity(10);

        Kupon dummyCreate = new Kupon(
                List.of(kos1),
                createRequest.getNamaKupon(),
                createRequest.getMasaBerlaku(),
                createRequest.getPersentase(),
                createRequest.getDeskripsi(),
                createRequest.getQuantity()
        );
        injectId(dummyCreate, UUID.fromString("adf47413-df12-426e-b06e-0c57e2c69cd5"));

        when(kostRepository.findAllById(createRequest.getKosPemilik())).thenReturn(List.of(kos1));
        when(kuponService.createKupon(any(Kupon.class))).thenReturn(dummyCreate);

        kuponController.createKupon(createRequest);

        KuponRequest editRequest = new KuponRequest();
        editRequest.setKosPemilik(List.of(kos1.getKostID()));
        editRequest.setPersentase(15);
        editRequest.setNamaKupon("Kupon Test Controller Baru");
        editRequest.setMasaBerlaku(LocalDate.of(2027, 7, 12));
        editRequest.setDeskripsi("Test Kupon For Controller Test");
        editRequest.setQuantity(3);

        Kupon dummyUpdate = new Kupon(
                List.of(kos1),
                editRequest.getNamaKupon(),
                editRequest.getMasaBerlaku(),
                editRequest.getPersentase(),
                editRequest.getDeskripsi(),
                editRequest.getQuantity()
        );
        injectId(dummyUpdate, UUID.fromString("adf47413-df12-426e-b06e-0c57e2c69cd5"));

        when(kuponService.getKuponById(dummyUpdate.getIdKupon())).thenReturn(CompletableFuture.completedFuture(dummyUpdate));
        when(kuponService.updateKupon(dummyUpdate.getIdKupon(), dummyUpdate.getKosPemilik().stream().map(Kost::getKostID).collect(Collectors.toList()), dummyUpdate.getPersentase(), dummyUpdate.getNamaKupon(),
                dummyUpdate.getMasaBerlaku(), dummyUpdate.getDeskripsi(), dummyUpdate.getQuantity())).thenReturn(dummyUpdate);

        var response = kuponController.updateKupon(dummyUpdate.getIdKupon(), editRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Kupon Test Controller Baru", response.getBody().getNamaKupon());
        assertEquals(15, response.getBody().getPersentase());
    }

    @Test
    void testDeleteKupon(){
        KuponRequest createRequest = new KuponRequest();
        createRequest.setKosPemilik(List.of(kos1.getKostID()));createRequest.setPersentase(10);createRequest.setNamaKupon("Kupon Test Controller");createRequest.setMasaBerlaku(LocalDate.of(2026, 7, 12));createRequest.setDeskripsi("Test Kupon For Controller Test");createRequest.setQuantity(5);

        Kupon dummyData = new Kupon(
                List.of(kos1),
                createRequest.getNamaKupon(),
                createRequest.getMasaBerlaku(),
                createRequest.getPersentase(),
                createRequest.getDeskripsi(),
                createRequest.getQuantity()
        );
        injectId(dummyData, UUID.fromString("adf47413-df12-426e-b06e-0c57e2c69cd5"));

        when(kostRepository.findAllById(createRequest.getKosPemilik())).thenReturn(List.of(kos1));
        when(kuponService.createKupon(any(Kupon.class))).thenReturn(dummyData);

        kuponController.createKupon(createRequest);
        when(kuponService.getKuponById(dummyData.getIdKupon())).thenReturn(CompletableFuture.completedFuture(dummyData));

        var response = kuponController.deleteKupon(dummyData.getIdKupon());

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testGetKuponById(){
        KuponRequest createRequest = new KuponRequest();
        createRequest.setKosPemilik(List.of(kos1.getKostID()));createRequest.setPersentase(10);createRequest.setNamaKupon("Kupon Test Controller");createRequest.setMasaBerlaku(LocalDate.of(2026, 7, 12));createRequest.setDeskripsi("Test Kupon For Controller Test");createRequest.setQuantity(5);

        Kupon dummyData = new Kupon(
                List.of(kos1),
                createRequest.getNamaKupon(),
                createRequest.getMasaBerlaku(),
                createRequest.getPersentase(),
                createRequest.getDeskripsi(),
                createRequest.getQuantity()
        );
        injectId(dummyData, UUID.fromString("adf47413-df12-426e-b06e-0c57e2c69cd5"));
        when(kostRepository.findAllById(createRequest.getKosPemilik())).thenReturn(List.of(kos1));
        when(kuponService.createKupon(any(Kupon.class))).thenReturn(dummyData);

        kuponController.createKupon(createRequest);
        when(kuponService.getKuponById(dummyData.getIdKupon())).thenReturn(CompletableFuture.completedFuture(dummyData));

        var response = kuponController.getKuponById(UUID.fromString("adf47413-df12-426e-b06e-0c57e2c69cd5"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Kupon Test Controller", response.getBody().getNamaKupon());
        assertEquals(LocalDate.of(2026, 7, 12), response.getBody().getMasaBerlaku());
    }

    @Test
    void testGetAllKupon(){
        KuponRequest createRequest = new KuponRequest();
        createRequest.setKosPemilik(List.of(kos1.getKostID()));createRequest.setPersentase(10);createRequest.setNamaKupon("Kupon Test Controller");createRequest.setMasaBerlaku(LocalDate.of(2026, 7, 12));createRequest.setDeskripsi("Test Kupon For Controller Test");createRequest.setQuantity(5);

        Kupon dummyData = new Kupon(
                List.of(kos1),
                createRequest.getNamaKupon(),
                createRequest.getMasaBerlaku(),
                createRequest.getPersentase(),
                createRequest.getDeskripsi(),
                createRequest.getQuantity()
        );
        injectId(dummyData, UUID.fromString("adf47413-df12-426e-b06e-0c57e2c69cd5"));
        when(kostRepository.findAllById(createRequest.getKosPemilik())).thenReturn(List.of(kos1));
        when(kuponService.createKupon(any(Kupon.class))).thenReturn(dummyData);

        kuponController.createKupon(createRequest);

        KuponRequest createRequest1 = new KuponRequest();
        createRequest.setKosPemilik(List.of(kos1.getKostID()));createRequest.setPersentase(10);createRequest.setNamaKupon("Kupon Test Controller");createRequest.setMasaBerlaku(LocalDate.of(2026, 7, 12));createRequest.setDeskripsi("Test Kupon For Controller Test");createRequest.setQuantity(5);

        Kupon dummyData1 = new Kupon(
                List.of(kos1),
                createRequest.getNamaKupon(),
                createRequest.getMasaBerlaku(),
                createRequest.getPersentase(),
                createRequest.getDeskripsi(),
                createRequest.getQuantity()
        );
        injectId(dummyData1, UUID.fromString("adf47413-df12-426e-b06e-0c57e2c69ea9"));

        kuponController.createKupon(createRequest1);
        when(kuponService.getAllKupon())
                .thenReturn(CompletableFuture.completedFuture(List.of(dummyData, dummyData1)));


        ResponseEntity<List<KuponResponse>> response = kuponController.getAllKupon();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        KuponResponse response1 = response.getBody().get(0);
        assertEquals("Kupon Test Controller", response1.getNamaKupon());

        KuponResponse response2 = response.getBody().get(1);
        assertEquals("Kupon Test Controller", response2.getNamaKupon());
    }

    @Test
    void testCreateKupon_InvalidPercentage() {
        KuponRequest kuponRequest = new KuponRequest();
        kuponRequest.setKosPemilik(List.of(kos1.getKostID()));
        kuponRequest.setPersentase(-5); // Invalid percentage
        kuponRequest.setNamaKupon("Invalid Kupon");
        kuponRequest.setMasaBerlaku(LocalDate.of(2026, 7, 12));
        kuponRequest.setDeskripsi("Invalid Kupon");
        kuponRequest.setQuantity(5);

        var response = kuponController.createKupon(kuponRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testCreateKupon_EmptyKostList() {
        KuponRequest kuponRequest = new KuponRequest();
        kuponRequest.setKosPemilik(List.of()); // Empty list
        kuponRequest.setPersentase(10);
        kuponRequest.setNamaKupon("Invalid Kupon");
        kuponRequest.setMasaBerlaku(LocalDate.of(2026, 7, 12));
        kuponRequest.setDeskripsi("Invalid Kupon");
        kuponRequest.setQuantity(5);

        var response = kuponController.createKupon(kuponRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testGetKuponsByKost() {
        // Create test data
        KuponRequest createRequest = new KuponRequest();
        createRequest.setKosPemilik(List.of(kos1.getKostID()));
        createRequest.setPersentase(10);
        createRequest.setNamaKupon("Kupon Test");
        createRequest.setMasaBerlaku(LocalDate.of(2026, 7, 12));
        createRequest.setDeskripsi("Test Kupon");
        createRequest.setQuantity(5);

        Kupon dummyKupon = new Kupon(
                List.of(kos1),
                createRequest.getNamaKupon(),
                createRequest.getMasaBerlaku(),
                createRequest.getPersentase(),
                createRequest.getDeskripsi(),
                createRequest.getQuantity()
        );

        when(kostRepository.findAllById(createRequest.getKosPemilik())).thenReturn(List.of(kos1));
        when(kuponService.createKupon(any(Kupon.class))).thenReturn(dummyKupon);
        kuponController.createKupon(createRequest);

        // Test with valid kost ID
        when(kuponService.findByKostId(kos1.getKostID())).thenReturn(CompletableFuture.completedFuture(List.of(dummyKupon)));
        var response = kuponController.getKuponsByKost(kos1.getKostID());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        // Test with invalid kost ID
        UUID invalidId = UUID.randomUUID();
        when(kuponService.findByKostId(invalidId)).thenReturn(CompletableFuture.completedFuture(List.of()));
        var emptyResponse = kuponController.getKuponsByKost(invalidId);
        assertEquals(HttpStatus.NO_CONTENT, emptyResponse.getStatusCode());
    }

    @Test
    void testGetKuponsByOwnerId() {
        // Create test data
        UUID ownerId = UUID.randomUUID();
        kos1.setOwnerId(ownerId);

        KuponRequest createRequest = new KuponRequest();
        createRequest.setKosPemilik(List.of(kos1.getKostID()));
        createRequest.setPersentase(10);
        createRequest.setNamaKupon("Owner Kupon");
        createRequest.setMasaBerlaku(LocalDate.of(2026, 7, 12));
        createRequest.setDeskripsi("Owner Kupon");
        createRequest.setQuantity(5);

        Kupon dummyKupon = new Kupon(
                List.of(kos1),
                createRequest.getNamaKupon(),
                createRequest.getMasaBerlaku(),
                createRequest.getPersentase(),
                createRequest.getDeskripsi(),
                createRequest.getQuantity()
        );

        when(kostRepository.findAllById(createRequest.getKosPemilik())).thenReturn(List.of(kos1));
        when(kuponService.createKupon(any(Kupon.class))).thenReturn(dummyKupon);
        kuponController.createKupon(createRequest);

        // Test with valid owner ID
        when(kostRepository.findByOwnerId(ownerId)).thenReturn(List.of(kos1));
        when(kuponService.getAllKupon()).thenReturn(CompletableFuture.completedFuture(List.of(dummyKupon)));

        var response = kuponController.getKuponsByOwnerId(ownerId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        // Test with invalid owner ID
        UUID invalidOwnerId = UUID.randomUUID();
        when(kostRepository.findByOwnerId(invalidOwnerId)).thenReturn(List.of());
        var emptyResponse = kuponController.getKuponsByOwnerId(invalidOwnerId);
        assertEquals(HttpStatus.NO_CONTENT, emptyResponse.getStatusCode());
    }

    @Test
    void testGetKuponByKodeUnik() {
        // Create test data
        KuponRequest createRequest = new KuponRequest();
        createRequest.setKosPemilik(List.of(kos1.getKostID()));
        createRequest.setPersentase(10);
        createRequest.setNamaKupon("Kode Unik Kupon");
        createRequest.setMasaBerlaku(LocalDate.of(2026, 7, 12));
        createRequest.setDeskripsi("Kode Unik Kupon");
        createRequest.setQuantity(5);

        Kupon dummyKupon = new Kupon(
                List.of(kos1),
                createRequest.getNamaKupon(),
                createRequest.getMasaBerlaku(),
                createRequest.getPersentase(),
                createRequest.getDeskripsi(),
                createRequest.getQuantity()
        );
        injectKodeUnik(dummyKupon, "TEST123");

        when(kostRepository.findAllById(createRequest.getKosPemilik())).thenReturn(List.of(kos1));
        when(kuponService.createKupon(any(Kupon.class))).thenReturn(dummyKupon);
        kuponController.createKupon(createRequest);

        // Test with valid kode unik
        when(kuponService.getKuponByKodeUnik("TEST123")).thenReturn(CompletableFuture.completedFuture(dummyKupon));
        var response = kuponController.getKuponByKodeUnik("TEST123");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Kode Unik Kupon", response.getBody().getNamaKupon());

        // Test with invalid kode unik
        when(kuponService.getKuponByKodeUnik("INVALID")).thenThrow(new RuntimeException("Kupon tidak ditemukan"));
        var notFoundResponse = kuponController.getKuponByKodeUnik("INVALID");
        assertEquals(HttpStatus.NOT_FOUND, notFoundResponse.getStatusCode());
    }

    @Test
    void testUpdateKupon_NotFound() {
        UUID nonExistentId = UUID.randomUUID();
        KuponRequest request = new KuponRequest();
        request.setKosPemilik(List.of(kos1.getKostID()));
        request.setPersentase(10);
        request.setNamaKupon("Non-existent");
        request.setMasaBerlaku(LocalDate.of(2026, 7, 12));
        request.setDeskripsi("Non-existent");
        request.setQuantity(5);

        when(kuponService.getKuponById(nonExistentId)).thenThrow(new EntityNotFoundException());
        var response = kuponController.updateKupon(nonExistentId, request);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteKupon_NotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(kuponService.getKuponById(nonExistentId)).thenThrow(new RuntimeException());
        var response = kuponController.deleteKupon(nonExistentId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetKuponById_NotFound() {
        UUID nonExistentId = UUID.randomUUID();
        when(kuponService.getKuponById(nonExistentId)).thenThrow(new RuntimeException("Not Found"));

        ResponseEntity<KuponResponse> response = kuponController.getKuponById(nonExistentId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(kuponService, times(1)).getKuponById(nonExistentId);
    }

    @Test
    void testLogCurrentUser_AnonymousUser() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(null);

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            kuponController.logCurrentUser();
            verify(securityContext, times(1)).getAuthentication();
        }
    }

    @Test
    void testLogCurrentUser_Unauthenticated() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(false);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);

        try (MockedStatic<SecurityContextHolder> mocked = mockStatic(SecurityContextHolder.class)) {
            mocked.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            kuponController.logCurrentUser();
            verify(auth).isAuthenticated();
            verify(securityContext, times(1)).getAuthentication();
        }
    }
}

