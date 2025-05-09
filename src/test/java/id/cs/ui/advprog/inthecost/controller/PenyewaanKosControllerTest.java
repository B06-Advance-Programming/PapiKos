package id.cs.ui.advprog.inthecost.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.cs.ui.advprog.inthecost.builder.PenyewaanKosBuilder;
import id.cs.ui.advprog.inthecost.enums.StatusPenyewaan;
import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.PenyewaanKos;
import id.cs.ui.advprog.inthecost.service.PenyewaanKosService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PenyewaanKosController.class)
public class PenyewaanKosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private PenyewaanKosService service;

    @Autowired
    private ObjectMapper objectMapper;

    private PenyewaanKos dummy;

    @BeforeEach
    void setUp() {
        Kost kos = new Kost("Kos A", "Jakarta", "Dekat kampus", 10, 1000000);
        dummy = PenyewaanKosBuilder.builder()
                .namaLengkap("Budi")
                .nomorTelepon("08123456789")
                .tanggalCheckIn(LocalDate.of(2025, 5, 10))
                .durasiBulan(6)
                .kos(kos)
                .status(StatusPenyewaan.DIAJUKAN)
                .build();
    }

    @Test
    void testCreatePenyewaan() throws Exception {
        Mockito.when(service.create(any(PenyewaanKos.class))).thenReturn(dummy);

        mockMvc.perform(post("/api/penyewaan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dummy)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.namaLengkap").value("Budi"))
                .andExpect(jsonPath("$.status").value("DIAJUKAN"));
    }

    @Test
    void testGetAllPenyewaan() throws Exception {
        Mockito.when(service.findAll()).thenReturn(List.of(dummy));

        mockMvc.perform(get("/api/penyewaan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testUpdatePenyewaan() throws Exception {
        dummy.setDurasiBulan(12);
        Mockito.when(service.update(any(PenyewaanKos.class))).thenReturn(dummy);

        mockMvc.perform(put("/api/penyewaan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dummy)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.durasiBulan").value(12));
    }

    @Test
    void testDeletePenyewaan() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/penyewaan/{id}", id))
                .andExpect(status().isOk());
    }
}
