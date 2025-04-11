package id.cs.ui.advprog.inthecost.repository;

import id.cs.ui.advprog.inthecost.model.Kupon;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class KuponRepository {
    public Kupon save(Kupon kupon) {return null;}

    public Kupon findById(String idKupon) {return null;}

    public Kupon findByKodeUnik(String kodeUnik) {
        return null;
    }

    public boolean deleteById(String idKupon) {return true;}

    public List<Kupon> findAll() {
        return null;
    }
}
