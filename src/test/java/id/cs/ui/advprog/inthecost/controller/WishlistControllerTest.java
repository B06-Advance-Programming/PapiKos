package id.cs.ui.advprog.inthecost.controller;

import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.service.WishlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest; // Added for parameterized tests
import org.junit.jupiter.params.provider.ValueSource; // Added for parameterized tests
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;

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
        final String userIdString = userId.toString();
        when(wishlistService.getWishlistByUserId(userId)).thenReturn(kosts);

        // Act
        ResponseEntity<List<Kost>> response = wishlistController.getWishlistByUserId(userIdString);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(kosts, response.getBody());
    }

    @Test
    void testGetWishlistByUserId_EmptyList() {
        // Arrange
        List<Kost> emptyKosts = new ArrayList<>();
        final String userIdString = userId.toString();
        when(wishlistService.getWishlistByUserId(userId)).thenReturn(emptyKosts);

        // Act
        ResponseEntity<List<Kost>> response = wishlistController.getWishlistByUserId(userIdString);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(emptyKosts, response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testGetWishlistByUserId_InvalidUserId() {
        // Act & Assert
        final String invalidId = "invalid-id";
        assertThrows(ResponseStatusException.class, () -> {
            wishlistController.getWishlistByUserId(invalidId);
        });
    }

    @Test
    void testGetWishlistByUserId_ServiceException() {
        // Arrange
        RuntimeException serviceException = new RuntimeException("Database connection failed");
        when(wishlistService.getWishlistByUserId(any(UUID.class)))
                .thenThrow(serviceException);

        // Act & Assert
        final String userIdString = userId.toString();
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            wishlistController.getWishlistByUserId(userIdString);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Error retrieving wishlist"));
    }

    @Test
    void testGetWishlistByUserId_NullPointerException() {
        // Arrange
        NullPointerException nullException = new NullPointerException("Test exception");
        when(wishlistService.getWishlistByUserId(any(UUID.class))).thenThrow(nullException);

        // Act & Assert
        final String userIdString = userId.toString();
        assertThrows(ResponseStatusException.class, () -> {
            wishlistController.getWishlistByUserId(userIdString);
        });
    }

    @Test
    void testAddToWishlist_Success() {
        // Arrange
        doNothing().when(wishlistService).addToWishlist(userId, kostId);

        // Act
        final String userIdString = userId.toString();
        final String kostIdString = kostId.toString();
        ResponseEntity<Map<String, String>> response = wishlistController.addToWishlist(userIdString, kostIdString);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("Kost added to wishlist successfully", response.getBody().get("message"));

        verify(wishlistService, times(1)).addToWishlist(userId, kostId);
    }

    @Test
    void testAddToWishlist_ServiceException() {
        // Arrange
        RuntimeException dbException = new RuntimeException("Database error");
        doThrow(dbException).when(wishlistService).addToWishlist(any(UUID.class), any(UUID.class));

        // Act & Assert
        final String userIdString = userId.toString();
        final String kostIdString = kostId.toString();
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            wishlistController.addToWishlist(userIdString, kostIdString);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Error adding to wishlist"));
    }

    @Test
    void testAddToWishlist_InvalidId() {
        // Act & Assert
        final String invalidUserId = "invalid-id";
        final String kostIdString1 = kostId.toString();
        assertThrows(ResponseStatusException.class, () -> {
            wishlistController.addToWishlist(invalidUserId, kostIdString1);
        });

        final String userIdString2 = userId.toString();
        final String invalidKostId = "invalid-id";
        assertThrows(ResponseStatusException.class, () -> {
            wishlistController.addToWishlist(userIdString2, invalidKostId);
        });
    }

    @Test
    void testAddToWishlist_BothInvalidIds() {
        // Act & Assert
        final String invalidUserId = "invalid-user-id";
        final String invalidKostId = "invalid-kost-id";
        assertThrows(ResponseStatusException.class, () -> {
            wishlistController.addToWishlist(invalidUserId, invalidKostId);
        });
    }

    @Test
    void testRemoveFromWishlist_Success() {
        // Arrange
        doNothing().when(wishlistService).removeFromWishlist(userId, kostId);

        // Act
        final String userIdString = userId.toString();
        final String kostIdString = kostId.toString();
        ResponseEntity<Map<String, String>> response = wishlistController.removeFromWishlist(userIdString, kostIdString);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("Kost removed from wishlist successfully", response.getBody().get("message"));

        verify(wishlistService, times(1)).removeFromWishlist(userId, kostId);
    }

    @Test
    void testRemoveFromWishlist_ServiceException() {
        // Arrange
        RuntimeException dbException = new RuntimeException("Database error");
        doThrow(dbException).when(wishlistService).removeFromWishlist(any(UUID.class), any(UUID.class));

        // Act & Assert
        final String userIdString = userId.toString();
        final String kostIdString = kostId.toString();
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            wishlistController.removeFromWishlist(userIdString, kostIdString);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Error removing from wishlist"));
    }

    @Test
    void testRemoveFromWishlist_InvalidId() {
        // Act & Assert
        final String invalidUserId = "invalid-id";
        final String kostIdString = kostId.toString();
        assertThrows(ResponseStatusException.class, () -> {
            wishlistController.removeFromWishlist(invalidUserId, kostIdString);
        });
    }

    @Test
    void testRemoveFromWishlist_InvalidKostId() {
        // Act & Assert
        final String userIdString = userId.toString();
        final String invalidKostId = "invalid-kost-id";
        assertThrows(ResponseStatusException.class, () -> {
            wishlistController.removeFromWishlist(userIdString, invalidKostId);
        });
    }

    @Test
    void testIsInWishlist_True() {
        // Arrange
        when(wishlistService.isInWishlist(userId, kostId)).thenReturn(true);

        // Act
        final String userIdString = userId.toString();
        final String kostIdString = kostId.toString();
        ResponseEntity<Map<String, Boolean>> response = wishlistController.isInWishlist(userIdString, kostIdString);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().get("inWishlist"));
    }

    @Test
    void testIsInWishlist_False() {
        // Arrange
        when(wishlistService.isInWishlist(userId, kostId)).thenReturn(false);

        // Act
        final String userIdString = userId.toString();
        final String kostIdString = kostId.toString();
        ResponseEntity<Map<String, Boolean>> response = wishlistController.isInWishlist(userIdString, kostIdString);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().get("inWishlist"));
    }

    @Test
    void testIsInWishlist_InvalidUserIdFormat() {
        // Act & Assert
        final String invalidUserId = "invalid-id";
        final String kostIdString = kostId.toString();
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            wishlistController.isInWishlist(invalidUserId, kostIdString);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Invalid ID format"));
    }

    @Test
    void testIsInWishlist_InvalidKostIdFormat() {
        // Act & Assert
        final String userIdString = userId.toString();
        final String invalidKostId = "invalid-id";
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            wishlistController.isInWishlist(userIdString, invalidKostId);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Invalid ID format"));
    }

    @Test
    void testIsInWishlist_ServiceException() {
        // Arrange
        RuntimeException dbException = new RuntimeException("Database error");
        when(wishlistService.isInWishlist(any(UUID.class), any(UUID.class)))
                .thenThrow(dbException);

        // Act & Assert
        final String userIdString = userId.toString();
        final String kostIdString = kostId.toString();
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            wishlistController.isInWishlist(userIdString, kostIdString);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Error checking wishlist"));
    }

    // Parameterized test replacing testGetWishlistCount_Success, testGetWishlistCount_ZeroCount, and testGetWishlistCount_LargeCount
    @ParameterizedTest
    @ValueSource(ints = {5, 0, 1000}) // Represents the different counts to test
    void testGetWishlistCount_VaryingCounts(int expectedCount) {
        // Arrange
        final String kostIdString = kostId.toString(); // Assuming kostId is set in @BeforeEach
        when(wishlistService.countWishlistsByKostId(kostId)).thenReturn(expectedCount);

        // Act
        ResponseEntity<Map<String, Integer>> response = wishlistController.getWishlistCount(kostIdString);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedCount, response.getBody().get("count"));
    }

    @Test
    void testGetWishlistCount_ServiceException() {
        // Arrange
        RuntimeException dbException = new RuntimeException("Database error");
        when(wishlistService.countWishlistsByKostId(any(UUID.class)))
                .thenThrow(dbException);

        // Act & Assert
        final String kostIdString = kostId.toString();
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            wishlistController.getWishlistCount(kostIdString);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Error counting wishlists"));
    }

    @Test
    void testGetWishlistCount_InvalidId() {
        // Act & Assert
        final String invalidId = "invalid-id";
        assertThrows(ResponseStatusException.class, () -> {
            wishlistController.getWishlistCount(invalidId);
        });
    }

    @Test
    void testGetWishlistCount_EmptyStringId() {
        // Act & Assert
        final String emptyId = "";
        assertThrows(ResponseStatusException.class, () -> {
            wishlistController.getWishlistCount(emptyId);
        });
    }

    @Test
    void testGetWishlistCount_NullStringId() {
        // Act & Assert
        final String nullId = null;
        assertThrows(ResponseStatusException.class, () -> {
            wishlistController.getWishlistCount(nullId);
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

    @Test
    void testHandleIllegalArgumentException_EmptyMessage() {
        // Arrange
        IllegalArgumentException ex = new IllegalArgumentException("");

        // Act
        ResponseEntity<Map<String, String>> response = wishlistController.handleIllegalArgumentException(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("error", response.getBody().get("status"));
        assertEquals("Invalid ID format: ", response.getBody().get("message"));
    }

    @Test
    void testHandleIllegalArgumentException_NullMessage() {
        // Arrange
        IllegalArgumentException ex = new IllegalArgumentException((String) null);

        // Act
        ResponseEntity<Map<String, String>> response = wishlistController.handleIllegalArgumentException(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("error", response.getBody().get("status"));
        assertEquals("Invalid ID format: null", response.getBody().get("message"));
    }
}