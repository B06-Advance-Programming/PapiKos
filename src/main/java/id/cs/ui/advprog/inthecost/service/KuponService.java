package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.model.Kupon;

import java.util.List;

public interface KuponService {
    Kupon createKupon(Kupon kupon);
    Kupon updateKupon(Kupon kupon);
    Kupon getKuponById(String id);
    Kupon getKuponByKodeUnik(String kodeUnik);
    void deleteKupon(String id);
    List<Kupon> getAllKupon();
}
