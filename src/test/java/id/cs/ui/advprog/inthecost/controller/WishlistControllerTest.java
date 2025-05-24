package id.cs.ui.advprog.inthecost.controller;

import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.service.WishlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class WishlistControllerTest {

    @InjectMocks
    private WishlistController wishlistController;

    @Mock
    private WishlistService wishlistService;

    private UUID userId;
    private UUID kostId;
    private Kost testKost;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        userId = UUID.randomUUID();
        kostId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        
        testKost = new Kost("Test Kost", "Test Address", "Test Description", 5, 1000000, ownerId);
        testKost.setKostID(kostId);
    }

    @Test
    void testGetWishlistByUserId_Success() {
        // Arrange
        List<Kost> kosts = List.of(testKost);
        when(wishlistService.getWishlistByUserId(userId)).thenReturn(kosts);
        
        // Act
        ResponseEntity<List<Kost>> response = wishlistController.getWishlistByUserId(userId.toString());
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(kosts, response.getBody());
    }
    
    @Test
    void testGetWishlistByUserId_InvalidUserId() {
        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> {
            wishlistController.getWishlistByUserId("invalid-id");
        });
    }
    
    @Test
    void testGetWishlistByUserId_NullPointerException() {
        // Arrange
        when(wishlistService.getWishlistByUserId(any(UUID.class))).thenThrow(new NullPointerException("Test exception"));
        
        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> {
            wishlistController.getWishlistByUserId(userId.toString());
        });
    }
    
    @Test
    void testAddToWishlist_Success() {
        // Arrange
        doNothing().when(wishlistService).addToWishlist(userId, kostId);
        
        // Act
        ResponseEntity<Map<String, String>> response = wishlistController.addToWishlist(userId.toString(), kostId.toString());
        
        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("Kost added to wishlist successfully", response.getBody().get("message"));
        
        verify(wishlistService, times(1)).addToWishlist(userId, kostId);
    }
    
    @Test
    void testAddToWishlist_InvalidId() {
        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> {
            wishlistController.addToWishlist("invalid-id", kostId.toString());
        });
        
        assertThrows(ResponseStatusException.class, () -> {
            wishlistController.addToWishlist(userId.toString(), "invalid-id");
        });
    }
    
    @Test
    void testRemoveFromWishlist_Success() {
        // Arrange
        doNothing().when(wishlistService).removeFromWishlist(userId, kostId);
        
        // Act
        ResponseEntity<Map<String, String>> response = wishlistController.removeFromWishlist(userId.toString(), kostId.toString());
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("Kost removed from wishlist successfully", response.getBody().get("message"));
        
        verify(wishlistService, times(1)).removeFromWishlist(userId, kostId);
    }
    
    @Test
    void testRemoveFromWishlist_InvalidId() {
        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> {
            wishlistController.removeFromWishlist("invalid-id", kostId.toString());
        });
    }
    
    @Test
    void testIsInWishlist_True() {
        // Arrange
        when(wishlistService.isInWishlist(userId, kostId)).thenReturn(true);
        
        // Act
        ResponseEntity<Map<String, Boolean>> response = wishlistController.isInWishlist(userId.toString(), kostId.toString());
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().get("inWishlist"));
    }
    
    @Test
    void testIsInWishlist_False() {
        // Arrange
        when(wishlistService.isInWishlist(userId, kostId)).thenReturn(false);
        
        // Act
        ResponseEntity<Map<String, Boolean>> response = wishlistController.isInWishlist(userId.toString(), kostId.toString());
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().get("inWishlist"));
    }
    
    @Test
    void testGetWishlistCount_Success() {
        // Arrange
        when(wishlistService.countWishlistsByKostId(kostId)).thenReturn(5);
        
        // Act
        ResponseEntity<Map<String, Integer>> response = wishlistController.getWishlistCount(kostId.toString());
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(5, response.getBody().get("count"));
    }
    
    @Test
    void testGetWishlistCount_InvalidId() {
        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> {
            wishlistController.getWishlistCount("invalid-id");
        });
    }
    
    @Test
    void testHandleIllegalArgumentException() {
        // Arrange
        IllegalArgumentException ex = new IllegalArgumentException("Test exception");
        
        // Act
        ResponseEntity<Map<String, String>> response = wishlistController.handleIllegalArgumentException(ex);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("error", response.getBody().get("status"));
        assertEquals("Invalid ID format: Test exception", response.getBody().get("message"));
    }
}
