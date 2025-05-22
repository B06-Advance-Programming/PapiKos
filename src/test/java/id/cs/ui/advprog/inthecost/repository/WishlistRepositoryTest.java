package id.cs.ui.advprog.inthecost.repository;

import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.Role;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.model.Wishlist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class WishlistRepositoryTest {

    @Autowired
    private WishlistRepository wishlistRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private KostRepository kostRepository;
    
    private User testUser1;
    private User testUser2;
    private Kost testKost1;
    private Kost testKost2;
    
    @BeforeEach
    void setUp() {
        // Clear existing data
        wishlistRepository.deleteAll();
        
        // Create test users
        Set<Role> roles = new HashSet<>();
        testUser1 = new User("user1", "password", "user1@example.com", roles);
        testUser2 = new User("user2", "password", "user2@example.com", roles);
        
        // Create two separate owner users
        User ownerUser1 = new User("owner1", "password", "owner1@example.com", roles);
        User ownerUser2 = new User("owner2", "password", "owner2@example.com", roles);
        
        // Save users to get their IDs
        testUser1 = userRepository.save(testUser1);
        testUser2 = userRepository.save(testUser2);
        ownerUser1 = userRepository.save(ownerUser1);
        ownerUser2 = userRepository.save(ownerUser2);
        
        // Create test kosts with all required fields
        testKost1 = new Kost("Kost A", "Address A", "Description A", 5, 1000000);
        testKost2 = new Kost("Kost B", "Address B", "Description B", 3, 800000);
        
        // Set different owner IDs for each kost
        testKost1.setOwnerId(ownerUser1.getId()); // First owner's ID
        testKost2.setOwnerId(ownerUser2.getId()); // Second owner's ID
        
        // Save the kosts to the database
        testKost1 = kostRepository.save(testKost1);
        testKost2 = kostRepository.save(testKost2);
    }
    
    @Test
    void testFindByUserId() {
        // Create complete wishlist entry for user 1 and kost 1
        Wishlist wishlist1 = createWishlist(testUser1, testKost1);
        wishlistRepository.save(wishlist1);
        
        // Create complete wishlist entry for user 1 and kost 2
        Wishlist wishlist2 = createWishlist(testUser1, testKost2);
        wishlistRepository.save(wishlist2);
        
        // Test finding by user ID
        List<Wishlist> wishlists = wishlistRepository.findByUser_Id(testUser1.getId());
        
        // Assertions
        assertFalse(wishlists.isEmpty(), "Wishlist should not be empty");
        assertEquals(2, wishlists.size(), "Should have 2 wishlist entries for user1");
        assertTrue(wishlists.stream().anyMatch(w -> w.getKos().getKostID().equals(testKost1.getKostID())), 
                 "Should contain kost1");
        assertTrue(wishlists.stream().anyMatch(w -> w.getKos().getKostID().equals(testKost2.getKostID())), 
                 "Should contain kost2");
    }
    
    @Test
    void testFindByUserIdReturnsEmptyListWhenNoWishlists() {
        // No need to create wishlists for this test
        // Test finding by user ID when no wishlists exist
        List<Wishlist> wishlists = wishlistRepository.findByUser_Id(testUser2.getId());
        
        // Assertions
        assertTrue(wishlists.isEmpty(), "Wishlist should be empty for user with no entries");
    }
    
    @Test
    void testFindUserIdsByKostId() {
        // Create complete wishlist entry for user 1 and kost 1
        Wishlist wishlist1 = createWishlist(testUser1, testKost1);
        wishlistRepository.save(wishlist1);
        
        // Create complete wishlist entry for user 2 and kost 1
        Wishlist wishlist2 = createWishlist(testUser2, testKost1);
        wishlistRepository.save(wishlist2);
        
        // Test finding user IDs by kost ID
        Set<String> userIds = wishlistRepository.findUserIdsByKostId(testKost1.getKostID());
        
        // Assertions
        assertFalse(userIds.isEmpty(), "User IDs set should not be empty");
        assertEquals(2, userIds.size(), "Should have 2 users who wishlisted kost1");
        assertTrue(userIds.contains(testUser1.getId().toString()), "Should contain user1 ID");
        assertTrue(userIds.contains(testUser2.getId().toString()), "Should contain user2 ID");
    }
    
    @Test
    void testFindUserIdsByKostIdReturnsEmptySetWhenNoWishlists() {
        // Test finding user IDs by kost ID when no wishlists exist
        Set<String> userIds = wishlistRepository.findUserIdsByKostId(testKost2.getKostID());
        
        // Assertions
        assertTrue(userIds.isEmpty(), "User IDs set should be empty for kost with no wishlist entries");
    }
    
    /**
     * Helper method to create a Wishlist with all required fields
     */
    private Wishlist createWishlist(User user, Kost kost) {
        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setKos(kost);
        
        // Set any other required fields that might be missing
        // For example, if Wishlist has a createdAt field:
        // wishlist.setCreatedAt(LocalDateTime.now());
        
        return wishlist;
    }
}
