package id.cs.ui.advprog.inthecost.Repository;

import id.cs.ui.advprog.inthecost.Model.Kost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface KostRepository extends JpaRepository<Kost, UUID> {
    // custom query
}
