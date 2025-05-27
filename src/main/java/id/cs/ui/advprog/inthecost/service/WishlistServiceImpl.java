package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.model.Wishlist;
import id.cs.ui.advprog.inthecost.repository.KostRepository;
import id.cs.ui.advprog.inthecost.repository.UserRepository;
import id.cs.ui.advprog.inthecost.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of the WishlistService interface
 * Handles user wishlist operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final KostRepository kostRepository;

    /**
     * {@inheritDoc}
     * 
     * @throws NullPointerException if userId is null or if any wishlist entry has a null kost
     */    @Override
    @Transactional(readOnly = true)
    public List<Kost> getWishlistByUserId(UUID userId) {
        log.debug("Retrieving wishlist for user: {}", userId);
        
        // Explicitly check for null userId and throw NullPointerException
        if (userId == null) {
            log.error("Attempted to get wishlist with null userId");
            throw new NullPointerException("User ID cannot be null");
        }
        
        try {
            List<Wishlist> entries = wishlistRepository.findByUser_Id(userId);
            log.debug("Found {} wishlist entries for user: {}", entries.size(), userId);
            
            // Check for null kosts before processing stream
            for (Wishlist entry : entries) {
                if (entry.getKos() == null) {
                    log.error("Found wishlist entry with null kost for user: {}, entry ID: {}", userId, entry.getId());
                    throw new NullPointerException("Kost cannot be null in wishlist entry");
                }
            }
            
            List<Kost> result = entries.stream()
                          .map(Wishlist::getKos)
                          .collect(Collectors.toList());
            
            log.info("Successfully retrieved {} kosts from wishlist for user: {}", result.size(), userId);
            return result;
        } catch (Exception e) {
            log.error("Error retrieving wishlist for user: {} - {}", userId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * {@inheritDoc}
     * 
     * @throws IllegalArgumentException if the user or kost does not exist
     * @throws IllegalStateException if the kost is already in the user's wishlist
     */    @Override
    @Transactional
    public void addToWishlist(UUID userId, UUID kostId) {
        log.info("Adding kost {} to wishlist for user: {}", kostId, userId);
        
        if (userId == null || kostId == null) {
            log.error("Attempted to add to wishlist with null parameters - userId: {}, kostId: {}", userId, kostId);
            throw new NullPointerException("User ID and Kost ID cannot be null");
        }
        
        try {
            // Check if kost is already in wishlist
            if (isInWishlist(userId, kostId)) {
                log.warn("Kost {} is already in wishlist for user: {}", kostId, userId);
                throw new IllegalStateException("Kost is already in user's wishlist");
            }
            
            // Get user and kost entities
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.error("User not found with ID: {}", userId);
                        return new IllegalArgumentException("User not found with ID: " + userId);
                    });
                    
            Kost kost = kostRepository.findById(kostId)
                    .orElseThrow(() -> {
                        log.error("Kost not found with ID: {}", kostId);
                        return new IllegalArgumentException("Kost not found with ID: " + kostId);
                    });
            
            // Create and save new wishlist entry
            Wishlist wishlist = new Wishlist();
            wishlist.setUser(user);
            wishlist.setKos(kost);
            wishlist.setCreatedAt(LocalDateTime.now());
            
            wishlistRepository.save(wishlist);
            log.info("Successfully added kost {} to wishlist for user: {}", kostId, userId);
        } catch (Exception e) {
            log.error("Error adding kost {} to wishlist for user: {} - {}", kostId, userId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * {@inheritDoc}
     * 
     * @throws IllegalArgumentException if the entry does not exist
     */    @Override
    @Transactional
    public void removeFromWishlist(UUID userId, UUID kostId) {
        log.info("Removing kost {} from wishlist for user: {}", kostId, userId);
        
        if (userId == null || kostId == null) {
            log.error("Attempted to remove from wishlist with null parameters - userId: {}, kostId: {}", userId, kostId);
            throw new NullPointerException("User ID and Kost ID cannot be null");
        }
        
        try {
            // Find wishlist entries for this user and kost
            List<Wishlist> entries = wishlistRepository.findByUser_IdAndKos_KostID(userId, kostId);
            log.debug("Found {} wishlist entries to remove for user: {}, kost: {}", entries.size(), userId, kostId);
            
            if (entries.isEmpty()) {
                log.warn("Attempted to remove kost {} from wishlist for user: {}, but it's not in wishlist", kostId, userId);
                throw new IllegalArgumentException("Kost is not in user's wishlist");
            }
            
            // Delete all matching entries (should normally be just one)
            for (Wishlist entry : entries) {
                wishlistRepository.delete(entry);
                log.debug("Deleted wishlist entry with ID: {} for user: {}, kost: {}", entry.getId(), userId, kostId);
            }
            
            log.info("Successfully removed kost {} from wishlist for user: {}", kostId, userId);
        } catch (Exception e) {
            log.error("Error removing kost {} from wishlist for user: {} - {}", kostId, userId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * {@inheritDoc}
     */    @Override
    @Transactional(readOnly = true)
    public boolean isInWishlist(UUID userId, UUID kostId) {
        log.debug("Checking if kost {} is in wishlist for user: {}", kostId, userId);
        
        if (userId == null || kostId == null) {
            log.error("Attempted to check wishlist with null parameters - userId: {}, kostId: {}", userId, kostId);
            throw new NullPointerException("User ID and Kost ID cannot be null");
        }
        
        try {
            List<Wishlist> entries = wishlistRepository.findByUser_IdAndKos_KostID(userId, kostId);
            boolean isInWishlist = !entries.isEmpty();
            
            log.debug("Wishlist check result for user: {}, kost: {} -> {}", userId, kostId, isInWishlist);
            return isInWishlist;
        } catch (Exception e) {
            log.error("Error checking wishlist for user: {}, kost: {} - {}", userId, kostId, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * {@inheritDoc}
     */    @Override
    @Transactional(readOnly = true)
    public int countWishlistsByKostId(UUID kostId) {
        log.debug("Counting wishlists for kost: {}", kostId);
        
        if (kostId == null) {
            log.error("Attempted to count wishlists with null kostId");
            throw new NullPointerException("Kost ID cannot be null");
        }
        
        try {
            // Get the set of user IDs who wishlisted this kost
            Set<String> userIds = wishlistRepository.findUserIdsByKostId(kostId);
            int count = userIds.size();
            
            log.info("Found {} users who wishlisted kost: {}", count, kostId);
            return count;
        } catch (Exception e) {
            log.error("Error counting wishlists for kost: {} - {}", kostId, e.getMessage(), e);
            throw e;
        }
    }
}
