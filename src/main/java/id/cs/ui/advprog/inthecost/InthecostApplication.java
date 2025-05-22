package id.cs.ui.advprog.inthecost;

import id.cs.ui.advprog.inthecost.model.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
public class InthecostApplication {

    @PostConstruct
    public void init() {
        // Set timezone ke Asia/Jakarta (WIB)
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Jakarta"));
        System.out.println("Timezone set to: " + TimeZone.getDefault().getID());
    }

    public static void main(String[] args) {
        SpringApplication.run(InthecostApplication.class, args);
    }

}