package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.dto.RegisterAdminDto;
import id.cs.ui.advprog.inthecost.model.Role;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.repository.RoleRepository;
import id.cs.ui.advprog.inthecost.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserRolesService userRolesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void allUsersReturnsList() {
        User user1 = new User();
        User user2 = new User();
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<User> result = userService.allUsers();

        assertEquals(2, result.size());
        assertTrue(result.contains(user1));
        assertTrue(result.contains(user2));
    }

    @Test
    void createAdministratorSuccess() {
        RegisterAdminDto dto = new RegisterAdminDto();
        dto.setUsername("admin");
        dto.setEmail("admin@example.com");
        dto.setPassword("pass");

        Role adminRole = new Role("ADMIN");
        Role userRole = new Role("USER");

        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(adminRole));
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode("pass")).thenReturn("hashed");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.createAdministrator(dto);

        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        assertEquals("admin@example.com", result.getEmail());
        assertEquals("hashed", result.getPassword());
        assertTrue(result.getRoles().contains(adminRole));
        assertTrue(result.getRoles().contains(userRole));

        verify(userRolesService).assignRole(result, adminRole);
        verify(userRolesService).assignRole(result, userRole);
    }

    @Test
    void createAdministratorFailsIfAdminRoleMissing() {
        RegisterAdminDto dto = new RegisterAdminDto();
        dto.setUsername("admin");

        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.empty());

        User result = userService.createAdministrator(dto);

        assertNull(result);
    }

    @Test
    void createAdministratorFailsIfUserRoleMissing() {
        RegisterAdminDto dto = new RegisterAdminDto();
        dto.setUsername("admin");

        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(new Role("ADMIN")));
        when(roleRepository.findByName("USER")).thenReturn(Optional.empty());

        User result = userService.createAdministrator(dto);

        assertNull(result);
    }
}
