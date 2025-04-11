package id.cs.ui.advprog.inthecost.repository;

import id.cs.ui.advprog.inthecost.model.Kupon;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class KuponRepository {
    private final Map<String, Kupon> kuponById = new HashMap<>();
    private final Map<String, Kupon> kuponByKodeUnik = new HashMap<>();

    private void putKupon(Kupon kupon) {
        kuponById.put(kupon.getIdKupon(), kupon);
        kuponByKodeUnik.put(kupon.getKodeUnik(), kupon);
    }

    public Kupon save(Kupon kupon) {
        putKupon(kupon);
        return kupon;
    }

    public Kupon findById(String idKupon) {
        return kuponById.get(idKupon);
    }

    public Kupon findByKodeUnik(String kodeUnik) {
        return kuponByKodeUnik.get(kodeUnik);
    }

    public boolean deleteById(String idKupon) {
        Kupon removed = kuponById.remove(idKupon);
        if (removed == null) return false;
        kuponByKodeUnik.remove(removed.getKodeUnik());
        return true;
    }

    public List<Kupon> findAll() {
        return new ArrayList<>(kuponById.values());
    }
}
