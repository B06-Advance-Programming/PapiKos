package id.cs.ui.advprog.inthecost.Repository;

import id.cs.ui.advprog.inthecost.Model.Role;
import id.cs.ui.advprog.inthecost.Model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private Role userRole;

    @BeforeEach
    public void setUp() {
        // Menyiapkan role USER untuk pengujian
        userRole = new Role("USER");

        // Menyimpan role terlebih dahulu (jika role belum ada di database)
        // Dalam aplikasi nyata, role mungkin sudah ada di database
    }

    @Test
    public void testSaveUser() {
        // Uji apakah user dapat disimpan ke dalam repository
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        User user = new User("alice", "password123", "alice@example.com", roles);

        User savedUser = userRepository.save(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("alice");
        assertThat(savedUser.getEmail()).isEqualTo("alice@example.com");
        assertThat(savedUser.getRoles()).contains(userRole);
    }

    @Test
    public void testFindByUsername() {
        // Uji apakah bisa menemukan pengguna berdasarkan username
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        User user = new User("john_doe", "password123", "john@example.com", roles);

        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByUsername("john_doe");

        assertThat(foundUser).isPresent(); // Pastikan user ditemukan
        assertThat(foundUser.get().getUsername()).isEqualTo("john_doe");
        assertThat(foundUser.get().getEmail()).isEqualTo("john@example.com");
        assertThat(foundUser.get().getRoles()).contains(userRole); // Pastikan role yang terkait ada
    }

    @Test
    public void testFindByEmail() {
        // Uji apakah bisa menemukan pengguna berdasarkan username
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        User user = new User("john_doe", "password123", "john@example.com", roles);

        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByEmail("john@example.com");

        assertThat(foundUser).isPresent(); // Pastikan user ditemukan
        assertThat(foundUser.get().getUsername()).isEqualTo("john_doe");
        assertThat(foundUser.get().getEmail()).isEqualTo("john@example.com");
        assertThat(foundUser.get().getRoles()).contains(userRole); // Pastikan role yang terkait ada
    }

    @Test
    public void testFindByUsernameNotFound() {
        // Uji jika username tidak ada di database
        Optional<User> foundUser = userRepository.findByUsername("non_existing_user");

        assertThat(foundUser).isNotPresent(); // Pastikan user tidak ditemukan
    }

    @Test
    public void testDeleteUser() {
        // Uji apakah user dapat dihapus
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        User user = new User("john_doe", "password123", "john@example.com", roles);

        User savedUser = userRepository.save(user);

        // Hapus user
        userRepository.delete(savedUser);

        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertThat(foundUser).isNotPresent(); // Pastikan user sudah dihapus
    }

    @Test
    public void testUpdateUser() {
        // Uji apakah data user bisa diperbarui
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        User user = new User("john_doe", "password123", "john@example.com", roles);

        User savedUser = userRepository.save(user);

        // Update user
        savedUser.setEmail("new_email@example.com");
        savedUser.setUsername("john_doe_updated");

        User updatedUser = userRepository.save(savedUser);

        // Pastikan data user telah diperbarui
        assertThat(updatedUser.getEmail()).isEqualTo("new_email@example.com");
        assertThat(updatedUser.getUsername()).isEqualTo("john_doe_updated");
    }

    @Test
    public void testUserRoles() {
        // Uji apakah role dapat dikaitkan dengan user
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        User user = new User("john_doe", "password123", "john@example.com", roles);

        User savedUser = userRepository.save(user);

        assertThat(savedUser.getRoles()).contains(userRole); // Pastikan role terhubung dengan user
    }
}
