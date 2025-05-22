package id.cs.ui.advprog.inthecost.controller;

import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.Kupon;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.service.KuponServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import id.cs.ui.advprog.inthecost.repository.UserRepository;
import id.cs.ui.advprog.inthecost.repository.KostRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/kupon")
public class KuponController {
    @Autowired
    private KuponServiceImpl kuponService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KostRepository kostRepository;


    @GetMapping("")
    public String showKupons(Model model) {
        model.addAttribute("kupons", kuponService.getAllKupon());
        return "kupon_list";
    }

    @GetMapping("/create")
    public String createKuponPage(Model model) {
        Kupon kupon = new Kupon();

        List<User> users = userRepository.findAll();
        List<Kost> kosts = kostRepository.findAll();

        model.addAttribute("kupon", kupon);
        model.addAttribute("users", users);
        model.addAttribute("kosts", kosts);

        return "create_kupon";
    }

    @PostMapping("/create")
    public String createKuponPost(@ModelAttribute("kupon") Kupon kupon,
                                  @RequestParam("pemilikId") UUID pemilikId,
                                  @RequestParam("kosIds") List<UUID> kosIds) {

        User pemilik = userRepository.findById(pemilikId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id"));

        List<Kost> kosList = kostRepository.findAllById(kosIds);

        kupon.setKosPemilik(kosList);

        kuponService.createKupon(kupon);

        return "redirect:/kupon";
    }

    @GetMapping("/edit/{id}")
    public String editKuponPage(@PathVariable("id") UUID id, Model model) {
        Kupon kupon = kuponService.getKuponById(id);
        System.out.println("ID Kupon yang di-get: " + kupon.getIdKupon());
        List<User> users = userRepository.findAll();
        List<Kost> kosts = kostRepository.findAll();

        model.addAttribute("selectedKostIds", kupon.getKosPemilik().stream()
                .map(Kost::getKostID)
                .collect(Collectors.toList()));

        model.addAttribute("kupon", kupon);
        model.addAttribute("users", users);
        model.addAttribute("kosts", kosts);

        return "edit_kupon";
    }

    @PostMapping("/edit")
    public String editKuponPost(@ModelAttribute Kupon kupon){
        System.out.println("ID Kupon setelah submit: " + kupon.getIdKupon());
        if (kupon.getIdKupon() == null) {
            throw new IllegalArgumentException("ID Kupon tidak boleh null");
        }
        kuponService.updateKupon(kupon);
        return "redirect:/kupon";
    }
}
