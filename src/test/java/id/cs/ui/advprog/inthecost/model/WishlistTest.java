package id.cs.ui.advprog.inthecost.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WishlistTest {
    
    private Wishlist wishlist;
    private User user;
    private Kost kost;
    private LocalDateTime testTime;
    
    @BeforeEach
    void setUp() {
        // Create test user
        Set<Role> roles = new HashSet<>();
        user = new User("testuser", "password123", "test@example.com", roles);
        user.setId(UUID.randomUUID());
        
        // Create test kost
        kost = new Kost("Test Kost", "Test Address", "Test Description", 5, 1000000);
        kost.setOwnerId(UUID.randomUUID());
        
        // Initialize time
        testTime = LocalDateTime.now();
        
        // Create wishlist instance
        wishlist = new Wishlist();
        wishlist.setId(1L);
        wishlist.setUser(user);
        wishlist.setKos(kost);
        wishlist.setCreatedAt(testTime);
    }
    
    @Test
    void testNoArgsConstructor() {
        Wishlist newWishlist = new Wishlist();
        assertNull(newWishlist.getId());
        assertNull(newWishlist.getUser());
        assertNull(newWishlist.getKos());
        assertNotNull(newWishlist.getCreatedAt(), "CreatedAt should be initialized with current time");
    }
    
    @Test
    void testAllArgsConstructor() {
        Wishlist allArgsWishlist = new Wishlist(1L, user, kost, testTime);
        assertEquals(1L, allArgsWishlist.getId());
        assertEquals(user, allArgsWishlist.getUser());
        assertEquals(kost, allArgsWishlist.getKos());
        assertEquals(testTime, allArgsWishlist.getCreatedAt());
    }
    
    @Test
    void testGettersAndSetters() {
        assertEquals(1L, wishlist.getId());
        assertEquals(user, wishlist.getUser());
        assertEquals(kost, wishlist.getKos());
        assertEquals(testTime, wishlist.getCreatedAt());
        
        // Test setters with new values
        Long newId = 2L;
        User newUser = new User("newuser", "newpass", "new@example.com", new HashSet<>());
        newUser.setId(UUID.randomUUID());
        Kost newKost = new Kost("New Kost", "New Address", "New Description", 3, 800000);
        LocalDateTime newTime = LocalDateTime.now().plusDays(1);
        
        wishlist.setId(newId);
        wishlist.setUser(newUser);
        wishlist.setKos(newKost);
        wishlist.setCreatedAt(newTime);
        
        assertEquals(newId, wishlist.getId());
        assertEquals(newUser, wishlist.getUser());
        assertEquals(newKost, wishlist.getKos());
        assertEquals(newTime, wishlist.getCreatedAt());
    }
    
    @Test
    void testEqualsAndHashCode() {
        // Same values should be equal
        Wishlist wishlist1 = new Wishlist(1L, user, kost, testTime);
        Wishlist wishlist2 = new Wishlist(1L, user, kost, testTime);
        
        assertEquals(wishlist1, wishlist2);
        assertEquals(wishlist1.hashCode(), wishlist2.hashCode());
        
        // Different ID should not be equal
        Wishlist wishlist3 = new Wishlist(2L, user, kost, testTime);
        assertNotEquals(wishlist1, wishlist3);
        assertNotEquals(wishlist1.hashCode(), wishlist3.hashCode());
        
        // Different user should not be equal
        User differentUser = new User("different", "password", "diff@example.com", new HashSet<>());
        differentUser.setId(UUID.randomUUID());
        Wishlist wishlist4 = new Wishlist(1L, differentUser, kost, testTime);
        assertNotEquals(wishlist1, wishlist4);
        
        // Different kost should not be equal
        Kost differentKost = new Kost("Different Kost", "Different Address", "Different Description", 10, 2000000);
        Wishlist wishlist5 = new Wishlist(1L, user, differentKost, testTime);
        assertNotEquals(wishlist1, wishlist5);
    }
    
    @Test
    void testToString() {
        String toStringResult = wishlist.toString();
        
        // Verify that toString contains important field information
        assertTrue(toStringResult.contains(wishlist.getId().toString()));
        assertTrue(toStringResult.contains(user.toString()));
        assertTrue(toStringResult.contains(kost.toString()));
        assertTrue(toStringResult.contains(testTime.toString()));
    }
    
    @Test
    void testNullUser_ShouldBeValidInModel() {
        // Test that the model allows null user (though database would reject it)
        Wishlist invalidWishlist = new Wishlist(1L, null, kost, testTime);
        
        // Verify the model accepts null user
        assertNull(invalidWishlist.getUser());
        // Note: This would fail at database level due to @JoinColumn(nullable = false)
    }

    @Test
    void testNullKost_ShouldBeValidInModel() {
        // Test that the model allows null kost (though database would reject it)
        Wishlist invalidWishlist = new Wishlist(1L, user, null, testTime);
        
        // Verify the model accepts null kost
        assertNull(invalidWishlist.getKos());
        // Note: This would fail at database level due to @JoinColumn(nullable = false)
    }
    
    @Test
    void testDefaultCreatedAtIsCurrentTime() {
        Wishlist newWishlist = new Wishlist();
        LocalDateTime now = LocalDateTime.now();
        
        // The timestamps should be very close (within a second)
        assertTrue(newWishlist.getCreatedAt().isAfter(now.minusSeconds(1)));
        assertTrue(newWishlist.getCreatedAt().isBefore(now.plusSeconds(1)));
    }
}
