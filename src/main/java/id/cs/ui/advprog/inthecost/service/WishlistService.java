package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.model.Kost;

import java.util.List;
import java.util.UUID;

/**
 * Interface for wishlist-related services
 * Handles user wishlist operations
 */
public interface WishlistService {

    /**
     * Retrieve all Kosts that a user has wish-listed.
     * 
     * @param userId The unique identifier of the user
     * @return List of Kost objects that the user has added to their wishlist
     */
    List<Kost> getWishlistByUserId(UUID userId);
    
    /**
     * Add a kost to a user's wishlist
     * 
     * @param userId The unique identifier of the user
     * @param kostId The unique identifier of the kost
     */
    void addToWishlist(UUID userId, UUID kostId);
    
    /**
     * Remove a kost from a user's wishlist
     * 
     * @param userId The unique identifier of the user
     * @param kostId The unique identifier of the kost
     */
    void removeFromWishlist(UUID userId, UUID kostId);
    
    /**
     * Check if a kost is in a user's wishlist
     * 
     * @param userId The unique identifier of the user
     * @param kostId The unique identifier of the kost
     * @return true if the kost is in the user's wishlist, false otherwise
     */
    boolean isInWishlist(UUID userId, UUID kostId);
    
    /**
     * Count how many users have added a specific kost to their wishlist
     * 
     * @param kostId The unique identifier of the kost
     * @return The number of users who have added the kost to their wishlist
     */
    int countWishlistsByKostId(UUID kostId);
}