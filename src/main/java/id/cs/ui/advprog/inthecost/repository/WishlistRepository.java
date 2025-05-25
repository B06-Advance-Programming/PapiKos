package id.cs.ui.advprog.inthecost.repository;

import id.cs.ui.advprog.inthecost.model.Wishlist;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface WishlistRepository extends CrudRepository<Wishlist, Long> {
    // fetch all Wishlist entries for a given user‐id
    List<Wishlist> findByUser_Id(UUID userId);
    
    // fetch all Wishlist entries for a specific user-id and kost-id combination
    List<Wishlist> findByUser_IdAndKos_KostID(UUID userId, UUID kostId);

    // existing query to fetch all userIds who wishlisted a kost
    @Query("SELECT w.user.id FROM Wishlist w WHERE w.kos.kostID = :kostId")
    Set<String> findUserIdsByKostId(UUID kostId);
}
