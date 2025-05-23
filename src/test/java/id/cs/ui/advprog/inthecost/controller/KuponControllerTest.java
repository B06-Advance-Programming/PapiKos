package id.cs.ui.advprog.inthecost.controller;

import id.cs.ui.advprog.inthecost.dto.KuponRequest;
import id.cs.ui.advprog.inthecost.dto.KuponResponse;
import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.Kupon;
import id.cs.ui.advprog.inthecost.repository.KostRepository;
import id.cs.ui.advprog.inthecost.service.KuponServiceImpl;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
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

    @Mock
    private KostRepository kostRepository;

    @Mock
    private KuponServiceImpl kuponService;

    public Kost kos1;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);

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

        assert response.getStatusCode().is2xxSuccessful();
        assert response.getBody() != null;
        assert response.getBody().getNamaKupon().equals("Kupon Test Controller");
        assert response.getBody().getPersentase() == 10;
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
}
