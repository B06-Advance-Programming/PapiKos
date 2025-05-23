package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.model.Wishlist;
import id.cs.ui.advprog.inthecost.repository.KostRepository;
import id.cs.ui.advprog.inthecost.repository.UserRepository;
import id.cs.ui.advprog.inthecost.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of the WishlistService interface
 * Handles user wishlist operations
 */
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
     */
    @Override
    @Transactional(readOnly = true)
    public List<Kost> getWishlistByUserId(UUID userId) {
        // Explicitly check for null userId and throw NullPointerException
        if (userId == null) {
            throw new NullPointerException("User ID cannot be null");
        }
        
        List<Wishlist> entries = wishlistRepository.findByUser_Id(userId);
        
        // Check for null kosts before processing stream
        for (Wishlist entry : entries) {
            if (entry.getKos() == null) {
                throw new NullPointerException("Kost cannot be null in wishlist entry");
            }
        }
        
        return entries.stream()
                      .map(Wishlist::getKos)
                      .collect(Collectors.toList());
    }
    
    /**
     * {@inheritDoc}
     * 
     * @throws IllegalArgumentException if the user or kost does not exist
     * @throws IllegalStateException if the kost is already in the user's wishlist
     */
    @Override
    @Transactional
    public void addToWishlist(UUID userId, UUID kostId) {
        if (userId == null || kostId == null) {
            throw new NullPointerException("User ID and Kost ID cannot be null");
        }
        
        // Check if kost is already in wishlist
        if (isInWishlist(userId, kostId)) {
            throw new IllegalStateException("Kost is already in user's wishlist");
        }
        
        // Get user and kost entities
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
                
        Kost kost = kostRepository.findById(kostId)
                .orElseThrow(() -> new IllegalArgumentException("Kost not found with ID: " + kostId));
        
        // Create and save new wishlist entry
        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setKos(kost);
        wishlist.setCreatedAt(LocalDateTime.now());
        
        wishlistRepository.save(wishlist);
    }
    
    /**
     * {@inheritDoc}
     * 
     * @throws IllegalArgumentException if the entry does not exist
     */
    @Override
    @Transactional
    public void removeFromWishlist(UUID userId, UUID kostId) {
        if (userId == null || kostId == null) {
            throw new NullPointerException("User ID and Kost ID cannot be null");
        }
        
        // Find wishlist entries for this user and kost
        List<Wishlist> entries = wishlistRepository.findByUser_IdAndKos_KostID(userId, kostId);
        
        if (entries.isEmpty()) {
            throw new IllegalArgumentException("Kost is not in user's wishlist");
        }
        
        // Delete all matching entries (should normally be just one)
        for (Wishlist entry : entries) {
            wishlistRepository.delete(entry);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isInWishlist(UUID userId, UUID kostId) {
        if (userId == null || kostId == null) {
            throw new NullPointerException("User ID and Kost ID cannot be null");
        }
        
        List<Wishlist> entries = wishlistRepository.findByUser_IdAndKos_KostID(userId, kostId);
        return !entries.isEmpty();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public int countWishlistsByKostId(UUID kostId) {
        if (kostId == null) {
            throw new NullPointerException("Kost ID cannot be null");
        }
        
        // Get the set of user IDs who wishlisted this kost
        Set<String> userIds = wishlistRepository.findUserIdsByKostId(kostId);
        
        // Return the count
        return userIds.size();
    }
}
