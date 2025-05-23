package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.model.Wishlist;
import id.cs.ui.advprog.inthecost.repository.KostRepository;
import id.cs.ui.advprog.inthecost.repository.UserRepository;
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
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WishlistServiceImplTest {

    @Mock
    private WishlistRepository wishlistRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private KostRepository kostRepository;

    @InjectMocks
    private WishlistServiceImpl wishlistService;
    
    @Captor
    private ArgumentCaptor<UUID> userIdCaptor;
    
    @Captor
    private ArgumentCaptor<Wishlist> wishlistCaptor;
    
    private UUID testUserId;
    private UUID testKostId1;
    private UUID testKostId2;
    private Kost testKost1;
    private Kost testKost2;
    private User testUser;
    private List<Wishlist> testWishlistEntries;
    
    @BeforeEach
    void setUp() {
        // Setup test data
        testUserId = UUID.randomUUID();
        testKostId1 = UUID.randomUUID();
        testKostId2 = UUID.randomUUID();
        
        testUser = new User();
        testUser.setId(testUserId);
        testUser.setUsername("testUser");
        
        testKost1 = new Kost("Test Kost 1", "Test Address 1", "Test Description 1", 5, 1000000);
        testKost1.setKostID(testKostId1);
        
        testKost2 = new Kost("Test Kost 2", "Test Address 2", "Test Description 2", 3, 800000);
        testKost2.setKostID(testKostId2);
        
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
    
    @Test
    void addToWishlist_Success() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(kostRepository.findById(testKostId1)).thenReturn(Optional.of(testKost1));
        when(wishlistRepository.findByUser_IdAndKos_KostID(testUserId, testKostId1)).thenReturn(Collections.emptyList());
        
        // Act
        wishlistService.addToWishlist(testUserId, testKostId1);
        
        // Assert
        verify(wishlistRepository).save(wishlistCaptor.capture());
        Wishlist savedWishlist = wishlistCaptor.getValue();
        
        assertEquals(testUser, savedWishlist.getUser());
        assertEquals(testKost1, savedWishlist.getKos());
        assertNotNull(savedWishlist.getCreatedAt());
    }
    
    @Test
    void addToWishlist_UserNotFound() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            wishlistService.addToWishlist(testUserId, testKostId1));
        
        verify(wishlistRepository, never()).save(any());
    }
    
    @Test
    void addToWishlist_KostNotFound() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(kostRepository.findById(testKostId1)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            wishlistService.addToWishlist(testUserId, testKostId1));
        
        verify(wishlistRepository, never()).save(any());
    }
    
    @Test
    void addToWishlist_AlreadyInWishlist() {
        // Arrange
        when(wishlistRepository.findByUser_IdAndKos_KostID(testUserId, testKostId1))
            .thenReturn(Collections.singletonList(testWishlistEntries.get(0)));
        
        // Act & Assert
        assertThrows(IllegalStateException.class, () -> 
            wishlistService.addToWishlist(testUserId, testKostId1));
        
        verify(userRepository, never()).findById(any());
        verify(kostRepository, never()).findById(any());
        verify(wishlistRepository, never()).save(any());
    }
    
    @Test
    void removeFromWishlist_Success() {
        // Arrange
        when(wishlistRepository.findByUser_IdAndKos_KostID(testUserId, testKostId1))
            .thenReturn(Collections.singletonList(testWishlistEntries.get(0)));
        
        // Act
        wishlistService.removeFromWishlist(testUserId, testKostId1);
        
        // Assert
        verify(wishlistRepository).delete(testWishlistEntries.get(0));
    }
    
    @Test
    void removeFromWishlist_NotInWishlist() {
        // Arrange
        when(wishlistRepository.findByUser_IdAndKos_KostID(testUserId, testKostId1))
            .thenReturn(Collections.emptyList());
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            wishlistService.removeFromWishlist(testUserId, testKostId1));
        
        verify(wishlistRepository, never()).delete(any());
    }
    
    @Test
    void isInWishlist_True() {
        // Arrange
        when(wishlistRepository.findByUser_IdAndKos_KostID(testUserId, testKostId1))
            .thenReturn(Collections.singletonList(testWishlistEntries.get(0)));
        
        // Act
        boolean result = wishlistService.isInWishlist(testUserId, testKostId1);
        
        // Assert
        assertTrue(result);
    }
    
    @Test
    void isInWishlist_False() {
        // Arrange
        when(wishlistRepository.findByUser_IdAndKos_KostID(testUserId, testKostId1))
            .thenReturn(Collections.emptyList());
        
        // Act
        boolean result = wishlistService.isInWishlist(testUserId, testKostId1);
        
        // Assert
        assertFalse(result);
    }
    
    @Test
    void countWishlistsByKostId_Success() {
        // Arrange
        Set<String> userIds = Set.of(testUserId.toString(), UUID.randomUUID().toString());
        when(wishlistRepository.findUserIdsByKostId(testKostId1)).thenReturn(userIds);
        
        // Act
        int count = wishlistService.countWishlistsByKostId(testKostId1);
        
        // Assert
        assertEquals(2, count);
    }
    
    @Test
    void countWishlistsByKostId_NoWishlists() {
        // Arrange
        when(wishlistRepository.findUserIdsByKostId(testKostId1)).thenReturn(Collections.emptySet());
        
        // Act
        int count = wishlistService.countWishlistsByKostId(testKostId1);
        
        // Assert
        assertEquals(0, count);
    }
    
    @Test
    void countWishlistsByKostId_NullKostId() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> 
            wishlistService.countWishlistsByKostId(null));
    }
}
