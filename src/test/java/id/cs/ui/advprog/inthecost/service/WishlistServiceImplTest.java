package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.model.Wishlist;
import id.cs.ui.advprog.inthecost.repository.WishlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WishlistServiceImplTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @InjectMocks
    private WishlistServiceImpl wishlistService;
    
    @Captor
    private ArgumentCaptor<UUID> userIdCaptor;
    
    private UUID testUserId;
    private Kost testKost1;
    private Kost testKost2;
    private User testUser;
    private List<Wishlist> testWishlistEntries;
    
    @BeforeEach
    void setUp() {
        // Setup test data
        testUserId = UUID.randomUUID();
        
        testUser = new User();
        testUser.setId(testUserId);
        testUser.setUsername("testUser");
        
        testKost1 = new Kost("Test Kost 1", "Test Address 1", "Test Description 1", 5, 1000000);
        testKost1.setKostID(UUID.randomUUID());
        
        testKost2 = new Kost("Test Kost 2", "Test Address 2", "Test Description 2", 3, 800000);
        testKost2.setKostID(UUID.randomUUID());
        
        // Create wishlist entries
        Wishlist entry1 = new Wishlist();
        entry1.setId(1L);
        entry1.setUser(testUser);
        entry1.setKos(testKost1);
        entry1.setCreatedAt(LocalDateTime.now().minusDays(1));
        
        Wishlist entry2 = new Wishlist();
        entry2.setId(2L);
        entry2.setUser(testUser);
        entry2.setKos(testKost2);
        entry2.setCreatedAt(LocalDateTime.now());
        
        testWishlistEntries = Arrays.asList(entry1, entry2);
    }
    
    @Test
    void getWishlistByUserId_ShouldUseCorrectRepositoryMethod() {
        // Arrange
        when(wishlistRepository.findByUser_Id(any(UUID.class))).thenReturn(new ArrayList<>());
        
        // Act
        wishlistService.getWishlistByUserId(testUserId);
        
        // Assert
        verify(wishlistRepository).findByUser_Id(userIdCaptor.capture());
        assertEquals(testUserId, userIdCaptor.getValue());
    }
    
    @Test
    void getWishlistByUserId_WithValidWishlists_ShouldReturnKostsList() {
        // Arrange
        when(wishlistRepository.findByUser_Id(testUserId)).thenReturn(testWishlistEntries);
        
        // Act
        List<Kost> result = wishlistService.getWishlistByUserId(testUserId);
        
        // Assert
        assertEquals(2, result.size());
        assertEquals(testKost1, result.get(0));
        assertEquals(testKost2, result.get(1));
        
        // Verify correct ID passed to repository
        verify(wishlistRepository).findByUser_Id(testUserId);
    }
    
    @Test
    void getWishlistByUserId_WhenDatabaseThrowsException_ShouldPropagateException() {
        // Arrange
        when(wishlistRepository.findByUser_Id(testUserId)).thenThrow(new DataAccessException("Database error") {});
        
        // Act & Assert
        assertThrows(DataAccessException.class, () -> wishlistService.getWishlistByUserId(testUserId));
    }
    
    @Test
    void getWishlistByUserId_WithSortOrder_ShouldPreserveOrder() {
        // Arrange - Reverse the order to ensure the test is meaningful
        List<Wishlist> reversedEntries = Arrays.asList(testWishlistEntries.get(1), testWishlistEntries.get(0));
        when(wishlistRepository.findByUser_Id(testUserId)).thenReturn(reversedEntries);
        
        // Act
        List<Kost> result = wishlistService.getWishlistByUserId(testUserId);
        
        // Assert - Order should be preserved
        assertEquals(2, result.size());
        assertEquals(testKost2, result.get(0));
        assertEquals(testKost1, result.get(1));
    }
    
    @Test
    void getWishlistByUserId_WithNullKostInWishlist_ShouldHandleGracefully() {
        // Arrange - Create a wishlist entry with null kost
        Wishlist nullKostEntry = new Wishlist();
        nullKostEntry.setId(3L);
        nullKostEntry.setUser(testUser);
        nullKostEntry.setKos(null);
        nullKostEntry.setCreatedAt(LocalDateTime.now());
        
        List<Wishlist> entriesWithNull = new ArrayList<>(testWishlistEntries);
        entriesWithNull.add(nullKostEntry);
        
        when(wishlistRepository.findByUser_Id(testUserId)).thenReturn(entriesWithNull);
        
        // Act & Assert
        assertThrows(NullPointerException.class, () -> wishlistService.getWishlistByUserId(testUserId));
    }
}
