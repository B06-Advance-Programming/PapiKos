package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.dto.LoginUserDto;
import id.cs.ui.advprog.inthecost.dto.RegisterUserDto;
import id.cs.ui.advprog.inthecost.model.Role;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.repository.RoleRepository;
import id.cs.ui.advprog.inthecost.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserRolesService userRolesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void signupSuccess() {
        RegisterUserDto dto = new RegisterUserDto();
        dto.setUsername("user");
        dto.setEmail("user@example.com");
        dto.setPassword("pass");
        dto.setRole("PENYEWA");

        Role userRole = new Role("USER");
        Role specificRole = new Role("PENYEWA");

        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(roleRepository.findByName("PENYEWA")).thenReturn(Optional.of(specificRole));
        when(passwordEncoder.encode("pass")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = authenticationService.signup(dto);

        assertNotNull(result);
        assertEquals("user", result.getUsername());
        assertEquals("user@example.com", result.getEmail());
        assertEquals("hashed", result.getPassword());
        assertTrue(result.getRoles().contains(userRole));
        assertTrue(result.getRoles().contains(specificRole));

        verify(userRolesService).assignRole(result, userRole);
        verify(userRolesService).assignRole(result, specificRole);
    }

    @Test
    void signupFailsWithAdminRole() {
        RegisterUserDto dto = new RegisterUserDto();
        dto.setRole("ADMIN");

        User result = authenticationService.signup(dto);
        assertNull(result);
    }

    @Test
    void signupFailsWhenUserRoleMissing() {
        RegisterUserDto dto = new RegisterUserDto();
        dto.setRole("PENYEWA");

        when(roleRepository.findByName("USER")).thenReturn(Optional.empty());

        User result = authenticationService.signup(dto);
        assertNull(result);
    }

    @Test
    void signupFailsWhenSpecificRoleMissing() {
        RegisterUserDto dto = new RegisterUserDto();
        dto.setRole("PENYEWA");

        when(roleRepository.findByName("USER")).thenReturn(Optional.of(new Role("USER")));
        when(roleRepository.findByName("PENYEWA")).thenReturn(Optional.empty());

        User result = authenticationService.signup(dto);
        assertNull(result);
    }

    @Test
    void authenticateSuccess() {
        LoginUserDto dto = new LoginUserDto();
        dto.setEmail("user@example.com");
        dto.setPassword("pass");

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("user");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        User result = authenticationService.authenticate(dto);

        assertNotNull(result);
        assertEquals("user", result.getUsername());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void authenticateFailsWhenUserNotFound() {
        LoginUserDto dto = new LoginUserDto();
        dto.setEmail("notfound@example.com");
        dto.setPassword("pass");

        when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authenticationService.authenticate(dto));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
