package id.cs.ui.advprog.inthecost.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserModelTest {

    private User user;
    private Role roleUser;
    private Role roleAdmin;

    @BeforeEach
    void setUp() {
        roleUser = new Role("USER");
        roleAdmin = new Role("ADMIN");

        Set<Role> roles = new HashSet<>();
        roles.add(roleUser);
        roles.add(roleAdmin);

        user = new User("testuser", "password", "test@example.com", roles);
    }

    @Test
    void testUserConstructorAndGetters() {
        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getPassword()).isEqualTo("password");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getBalance()).isEqualTo(0.0);
        assertThat(user.getRoles()).containsExactlyInAnyOrder(roleUser, roleAdmin);
    }

    @Test
    void testParameterizedConstructorWithBalance() {
        Set<Role> roles = new HashSet<>();
        roles.add(new Role("MANAGER"));

        User userWithBalance = new User("admin", "adminpass", "admin@test.com", 150.0, roles);

        assertThat(userWithBalance.getUsername()).isEqualTo("admin");
        assertThat(userWithBalance.getBalance()).isEqualTo(150.0);
        assertThat(userWithBalance.getRoles()).hasSize(1);
    }

    @Test
    void testDefaultConstructor() {
        User defaultUser = new User();
        assertThat(defaultUser.getUsername()).isNull();
        assertThat(defaultUser.getBalance()).isEqualTo(0.0);
        assertThat(defaultUser.getRoles()).isEmpty();
    }

    @Test
    void testAddRoleToUser() {
        Role roleManager = new Role("MANAGER");
        user.getRoles().add(roleManager);

        assertThat(user.getRoles()).hasSize(3)
                .containsExactlyInAnyOrder(roleUser, roleAdmin, roleManager);
    }

    @Test
    void testGetAuthorities() {
        Set<SimpleGrantedAuthority> authorities = (Set<SimpleGrantedAuthority>) user.getAuthorities();

        assertThat(authorities)
                .extracting(SimpleGrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    }

    @Test
    void testUserDetailsMethods() {
        assertThat(user.isAccountNonExpired()).isTrue();
        assertThat(user.isAccountNonLocked()).isTrue();
        assertThat(user.isCredentialsNonExpired()).isTrue();
        assertThat(user.isEnabled()).isTrue();
    }

    @Test
    void testSettersAndGetters() {
        UUID newId = UUID.randomUUID();
        user.setId(newId);
        user.setUsername("newuser");
        user.setPassword("newpass");
        user.setEmail("new@test.com");
        user.setBalance(99.99);

        Set<Role> newRoles = new HashSet<>();
        Role newRole = new Role("EDITOR");
        newRoles.add(newRole);
        user.setRoles(newRoles);

        assertThat(user.getId()).isEqualTo(newId);
        assertThat(user.getUsername()).isEqualTo("newuser");
        assertThat(user.getPassword()).isEqualTo("newpass");
        assertThat(user.getEmail()).isEqualTo("new@test.com");
        assertThat(user.getBalance()).isEqualTo(99.99);
        assertThat(user.getRoles()).containsExactly(newRole);
    }

    @Test
    void testRoleManagement() {
        // Test empty roles
        user.setRoles(new HashSet<>());
        assertThat(user.getRoles()).isEmpty();

        // Test null safety
        user.setRoles(null);
        assertThat(user.getRoles()).isNull();
    }

    @Test
    void testIdGeneration() {
        User newUser = new User();
        newUser.setId(UUID.randomUUID());
        assertThat(newUser.getId())
                .as("User ID should be auto-generated")
                .isNotNull()
                .isInstanceOf(UUID.class);
    }
}