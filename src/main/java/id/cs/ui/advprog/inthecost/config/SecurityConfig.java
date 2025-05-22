package id.cs.ui.advprog.inthecost.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // <--- disables CSRF globally!
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // <--- permit everything for now
                );
        return http.build();
    }
}