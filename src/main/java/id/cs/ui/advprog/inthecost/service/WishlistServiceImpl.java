package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.Wishlist;
import id.cs.ui.advprog.inthecost.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of the WishlistService interface
 * Handles user wishlist operations
 */
@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;    /**
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
}
