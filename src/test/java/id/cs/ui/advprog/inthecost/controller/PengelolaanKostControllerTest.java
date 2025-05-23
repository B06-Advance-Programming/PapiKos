//package id.cs.ui.advprog.inthecost.controller;
//
//import id.cs.ui.advprog.inthecost.model.Kost;
//import id.cs.ui.advprog.inthecost.service.PengelolaanKost;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ExtendWith(MockitoExtension.class)
//public class PengelolaanKostControllerTest {
//
//    private MockMvc mockMvc;
//
//    @Mock
//    private PengelolaanKost pengelolaanKost;
//
//    @InjectMocks
//    private PengelolaanKostController pengelolaanKostController;
//
//    @BeforeEach
//    public void setup() {
//        mockMvc = MockMvcBuilders.standaloneSetup(pengelolaanKostController).build();
//    }
//
//    @Test
//    public void testAddKost() throws Exception {
//        Kost newKost = new Kost();
//        newKost.setNama("Kost Baru");
//        newKost.setHargaPerBulan(2000000);
//
//        mockMvc.perform(post("/api/pengelolaan_kost")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"nama\": \"Kost Baru\", \"hargaPerBulan\": 2000000}"))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Kost berhasil ditambahkan."));
//
//        // Verifikasi bahwa addKost dipanggil tepat satu kali
//        verify(pengelolaanKost, times(1)).addKost(any(Kost.class));
//    }
//
//    @Test
//    public void testGetAllKost() throws Exception {
//        List<Kost> kostList = new ArrayList<>();
//        Kost kost1 = new Kost();
//        kost1.setNama("Kost Baru");
//        kost1.setHargaPerBulan(2000000);
//
//        Kost kost2 = new Kost();
//        kost2.setNama("Kost bura");
//        kost2.setHargaPerBulan(1000000);
//        kostList.add(kost1);
//        kostList.add(kost2);
//
//        // Mocking the getAllKost method
//        when(pengelolaanKost.getAllKost()).thenReturn(kostList);
//
//        mockMvc.perform(get("/api/pengelolaan_kost"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.size()").value(2))
//                .andExpect(jsonPath("$[0].nama").value("Kost Baru"))
//                .andExpect(jsonPath("$[1].nama").value("Kost bura"));
//
//        verify(pengelolaanKost, times(1)).getAllKost();
//    }
//
//    @Test
//    public void testUpdateKost() throws Exception {
//        UUID id = UUID.randomUUID();  // ID untuk path variable
//
//        // Mocking the updateKostByID method to do nothing
//        doNothing().when(pengelolaanKost).updateKostByID(eq(id), any(Kost.class));
//
//        // JSON body harus sesuai field di kelas Kost
//        String requestBody = """
//        {
//            "nama": "Kost Updated",
//            "hargaPerBulan": 2500000
//        }
//        """;
//
//        mockMvc.perform(put("/api/pengelolaan_kost/{id}", id)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestBody))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Kost berhasil diperbarui."));
//
//        // Verifikasi bahwa method dipanggil dengan benar
//        verify(pengelolaanKost, times(1)).updateKostByID(eq(id), any(Kost.class));
//    }
//
//    @Test
//    public void testDeleteKost() throws Exception {
//        UUID id = UUID.randomUUID();
//
//        // Mocking the deleteKost method
//        doNothing().when(pengelolaanKost).deleteKost(eq(id));
//
//        mockMvc.perform(delete("/api/pengelolaan_kost/{id}", id))
//                .andExpect(status().isOk())
//                .andExpect(content().string("Kost berhasil dihapus."));
//
//        verify(pengelolaanKost, times(1)).deleteKost(eq(id));
//    }
//}
