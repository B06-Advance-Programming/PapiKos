package id.cs.ui.advprog.inthecost.controller;

import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.service.WishlistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WishlistController.class)
public class WishlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WishlistService wishlistService;

    @Test
    void testGetWishlistForUser() throws Exception {
        UUID userId = UUID.randomUUID();
        Kost sample = new Kost("Kos A", "Jl. A", "Desc A", 5, 500000);
        when(wishlistService.getWishlistByUserId(userId)).thenReturn(List.of(sample));

        mockMvc.perform(get("/api/wishlist/{userId}", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].nama").value("Kos A"));
    }
}
