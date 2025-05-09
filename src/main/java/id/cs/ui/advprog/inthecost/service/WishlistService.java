package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.Wishlist;
import id.cs.ui.advprog.inthecost.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;

    /**
     * Retrieve all Kosts that a user has wish‐listed.
     */
    public List<Kost> getWishlistByUserId(UUID userId) {
        List<Wishlist> entries = wishlistRepository.findByUser_Id(userId);
        return entries.stream()
                      .map(Wishlist::getKos)
                      .collect(Collectors.toList());
    }
}