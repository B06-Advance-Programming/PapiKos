package id.cs.ui.advprog.inthecost;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.TimeZone;
import java.util.logging.Logger;

@EnableAsync
@SpringBootApplication
public class InthecostApplication {

    @PostConstruct
    public void init() {
        // Set timezone ke Asia/Jakarta (WIB)
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Jakarta"));
    }

    public static void main(String[] args) {
        SpringApplication.run(InthecostApplication.class, args);
    }

}