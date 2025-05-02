package id.cs.ui.advprog.inthecost.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class UserModelTest {

    private User user;
    private Role roleUser;
    private Role roleAdmin;

    @BeforeEach
    public void setUp() {
        // Menyiapkan Role (mock)
        roleUser = new Role("USER");
        roleAdmin = new Role("ADMIN");

        // Menyiapkan User dan menetapkan roles
        Set<Role> roles = new HashSet<>();
        roles.add(roleUser);
        roles.add(roleAdmin);

        user = new User("testuser", "password", "test@example.com", roles);
    }

    @Test
    public void testUserConstructorAndGetters() {
        // Memastikan objek user terbuat dengan benar
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getPassword()).isEqualTo("password");
        assertThat(user.getEmail()).isEqualTo("test@example.com");

        // Memastikan roles terpasang dengan benar
        assertThat(user.getRoles()).hasSize(2);
        assertThat(user.getRoles()).contains(roleUser, roleAdmin);
    }

    @Test
    public void testAddRoleToUser() {
        // Menambahkan role baru
        Role roleManager = new Role("DUMMYROLE-TIDK-LEWAT-DB-AMAN");
        user.getRoles().add(roleManager);

        // Memastikan role baru ditambahkan dengan benar
        assertThat(user.getRoles()).hasSize(3);
        assertThat(user.getRoles()).contains(roleUser, roleAdmin, roleManager);
    }

    @Test
    public void testGetAuthorities() {
        // Memastikan authorities berfungsi dengan benar
        Set<SimpleGrantedAuthority> authorities = (Set<SimpleGrantedAuthority>) user.getAuthorities();

        assertThat(authorities).hasSize(2);
        assertThat(authorities).contains(new SimpleGrantedAuthority("USER"), new SimpleGrantedAuthority("ADMIN"));
    }

    @Test
    public void testUserDetailsMethods() {
        // Memastikan semua metode UserDetails bekerja dengan benar
        assertThat(user.isAccountNonExpired()).isTrue();
        assertThat(user.isAccountNonLocked()).isTrue();
        assertThat(user.isCredentialsNonExpired()).isTrue();
        assertThat(user.isEnabled()).isTrue();
    }

    @Test
    public void testSettersAndGetters() {
        // Memastikan setter dan getter berfungsi dengan benar
        user.setUsername("newUsername");
        user.setPassword("newPassword");
        user.setEmail("newemail@example.com");

        assertThat(user.getUsername()).isEqualTo("newUsername");
        assertThat(user.getPassword()).isEqualTo("newPassword");
        assertThat(user.getEmail()).isEqualTo("newemail@example.com");
    }
}
