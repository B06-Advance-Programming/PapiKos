package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.Wishlist;
import id.cs.ui.advprog.inthecost.repository.WishlistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WishlistServiceTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @InjectMocks
    private WishlistServiceImpl wishlistService;

    @Test
    void getWishlistByUserId_WithValidId_ReturnsCorrectKosts() {
        // Arrange
        UUID userId = UUID.randomUUID();
        
        // Create test kosts
        Kost kost1 = new Kost("Kost 1", "Address 1", "Description 1", 5, 1000000);
        Kost kost2 = new Kost("Kost 2", "Address 2", "Description 2", 3, 800000);
        
        // Create test wishlists
        List<Wishlist> wishlistEntries = new ArrayList<>();
        Wishlist entry1 = new Wishlist();
        entry1.setKos(kost1);
        Wishlist entry2 = new Wishlist();
        entry2.setKos(kost2);
        wishlistEntries.add(entry1);
        wishlistEntries.add(entry2);
        
        when(wishlistRepository.findByUser_Id(userId)).thenReturn(wishlistEntries);
        
        // Act
        List<Kost> result = wishlistService.getWishlistByUserId(userId);
        
        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(kost1));
        assertTrue(result.contains(kost2));
        verify(wishlistRepository).findByUser_Id(userId);
    }
    
    @Test
    void getWishlistByUserId_WithEmptyWishlist_ReturnsEmptyList() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(wishlistRepository.findByUser_Id(userId)).thenReturn(new ArrayList<>());
        
        // Act
        List<Kost> result = wishlistService.getWishlistByUserId(userId);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(wishlistRepository).findByUser_Id(userId);
    }
    
    @Test
    void getWishlistByUserId_WithNullId_ShouldHandleGracefully() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> wishlistService.getWishlistByUserId(null));
        verifyNoInteractions(wishlistRepository);
    }
}
