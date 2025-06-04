package id.cs.ui.advprog.inthecost.repository;

import id.cs.ui.advprog.inthecost.model.Kupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface KuponRepository extends JpaRepository<Kupon, UUID> {

    // Fungsi tambahan, di luar default JPARepository

    Optional<Kupon> findByKodeUnik(String kodeUnik);

    @Query("SELECT k FROM Kupon k JOIN FETCH k.kosPemilik kp WHERE kp.kostID = :kostId")
    List<Kupon> findByKostId(@Param("kostId") UUID kostId);
}