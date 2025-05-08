package id.cs.ui.advprog.inthecost.repository;

import id.cs.ui.advprog.inthecost.model.Wishlist;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;
import java.util.UUID;

public interface WishlistRepository extends CrudRepository<Wishlist, UUID> {
    @Query("SELECT w.userId FROM Wishlist w WHERE w.kos.kostID = :kostId")
    Set<String> findUserIdsByKostId(UUID kostId);
}
