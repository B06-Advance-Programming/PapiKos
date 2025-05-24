package id.cs.ui.advprog.inthecost.controller;

import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.service.PengelolaanKost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class PengelolaanKostControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PengelolaanKost pengelolaanKost;

    @InjectMocks
    private PengelolaanKostController pengelolaanKostController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(pengelolaanKostController).build();
    }

    @Test
    public void testAddKost() throws Exception {
        // Setup dummy return
        when(pengelolaanKost.addKost(any(Kost.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        // Kirim request async
        MvcResult mvcResult = mockMvc.perform(post("/api/pengelolaan_kost")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nama\": \"Kost Baru\", \"hargaPerBulan\": 2000000}"))
                .andExpect(request().asyncStarted()) // pastikan async jalan
                .andReturn();

        // Tunggu dan validasi hasil
        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().string("Kost berhasil ditambahkan."));
    }

    @Test
    public void testGetAllKost() throws Exception {
        List<Kost> kostList = new ArrayList<>();
        Kost kost1 = new Kost();
        kost1.setNama("Kost Baru");
        kost1.setHargaPerBulan(2000000);

        Kost kost2 = new Kost();
        kost2.setNama("Kost bura");
        kost2.setHargaPerBulan(1000000);
        kostList.add(kost1);
        kostList.add(kost2);

        // Mocking the getAllKost method
        when(pengelolaanKost.getAllKost())
                .thenReturn(CompletableFuture.completedFuture(kostList));

        MvcResult res = mockMvc.perform(get("/api/pengelolaan_kost"))
                .andExpect(request().asyncStarted()) // pastikan async jalan
                .andReturn();

        mockMvc.perform(asyncDispatch(res))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].nama").value("Kost Baru"))
                .andExpect(jsonPath("$[1].nama").value("Kost bura"));
        verify(pengelolaanKost, times(1)).getAllKost();
    }

    @Test
    public void testUpdateKost() throws Exception {
        UUID id = UUID.randomUUID();  // ID untuk path variable

        // Mocking the updateKostByID method to return a completed future
        when(pengelolaanKost.updateKostByID(eq(id), any(Kost.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        // JSON body sesuai dengan field di kelas Kost
        String requestBody = """
    {
        "nama": "Kost Updated",
        "hargaPerBulan": 2500000
    }
    """;

        // Kirim request dan verifikasi async dimulai
        MvcResult result = mockMvc.perform(put("/api/pengelolaan_kost/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(request().asyncStarted())
                .andReturn();

        // Dispatch hasil async dan verifikasi response akhir
        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().string("Kost berhasil diperbarui."));

        // Verifikasi bahwa method service dipanggil sekali
        verify(pengelolaanKost, times(1)).updateKostByID(eq(id), any(Kost.class));
    }

    @Test
    public void testDeleteKost() throws Exception {
        UUID id = UUID.randomUUID();

        // Mocking the deleteKost method to return CompletableFuture
        when(pengelolaanKost.deleteKost(eq(id)))
                .thenReturn(CompletableFuture.completedFuture(null));

        // Perform the request and wait for async to start
        MvcResult result = mockMvc.perform(delete("/api/pengelolaan_kost/{id}", id))
                .andExpect(request().asyncStarted())
                .andReturn();

        // Dispatch async result and verify final response
        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(content().string("Kost berhasil dihapus."));

        // Verify that the service was called once
        verify(pengelolaanKost, times(1)).deleteKost(eq(id));
    }
}
