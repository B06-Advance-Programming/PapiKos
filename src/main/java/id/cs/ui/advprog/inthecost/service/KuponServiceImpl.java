package id.cs.ui.advprog.inthecost.service;
import id.cs.ui.advprog.inthecost.model.Kupon;
import id.cs.ui.advprog.inthecost.repository.KuponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class KuponServiceImpl implements KuponService {

    @Autowired
    KuponRepository kuponRepository;

    @Override
    public Kupon createKupon(Kupon kupon){return null;}

    @Override
    public Kupon updateKupon(Kupon kupon) {return null;}

    @Override
    public Kupon getKuponById(UUID id) {return null;}

    @Override
    public Kupon getKuponByKodeUnik(String kodeUnik){return null;}

    @Override
    public void deleteKupon(UUID id) {}

    @Override
    public List<Kupon> getAllKupon() {return null;}
}