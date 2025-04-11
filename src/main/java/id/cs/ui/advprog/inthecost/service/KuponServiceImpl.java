package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.model.Kupon;
import id.cs.ui.advprog.inthecost.repository.KuponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class KuponServiceImpl implements KuponService {
    @Autowired
    private KuponRepository kuponRepository;

    @Override
    public Kupon createKupon(Kupon kupon) {return null;}

    @Override
    public Kupon getKuponById(String id) {return null;}

    @Override
    public Kupon getKuponByKodeUnik(String kodeUnik) {return null;}

    @Override
    public void deleteKupon(String id){}

    @Override
    public List<Kupon> getAllKupon() {return null;}
}

