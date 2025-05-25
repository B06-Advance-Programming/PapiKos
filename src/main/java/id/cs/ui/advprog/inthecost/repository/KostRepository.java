package id.cs.ui.advprog.inthecost.repository;

import id.cs.ui.advprog.inthecost.model.Kost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface KostRepository extends JpaRepository<Kost, UUID> {
    // custom query
    List<Kost> findByOwnerId(UUID ownerId);
}
