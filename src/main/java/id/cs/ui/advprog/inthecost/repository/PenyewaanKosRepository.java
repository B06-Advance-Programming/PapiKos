package id.cs.ui.advprog.inthecost.repository;

import id.cs.ui.advprog.inthecost.model.PenyewaanKos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PenyewaanKosRepository extends JpaRepository<PenyewaanKos, UUID> {
    // Bisa tambahkan custom query jika diperlukan nanti
}
