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
}