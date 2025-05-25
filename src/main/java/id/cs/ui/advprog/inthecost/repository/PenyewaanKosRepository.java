package id.cs.ui.advprog.inthecost.repository;

import id.cs.ui.advprog.inthecost.model.PenyewaanKos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
import id.cs.ui.advprog.inthecost.enums.StatusPenyewaan;

public interface PenyewaanKosRepository extends JpaRepository<PenyewaanKos, UUID> {
    List<PenyewaanKos> findByKos_KostIDAndUserIdAndStatus(UUID kostId, UUID userId, StatusPenyewaan status);
    List<PenyewaanKos> findByUserIdAndStatus(UUID userId, StatusPenyewaan status);
}
