package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.dto.LoginUserDto;
import id.cs.ui.advprog.inthecost.dto.RegisterUserDto;
import id.cs.ui.advprog.inthecost.model.Role;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.repository.RoleRepository;
import id.cs.ui.advprog.inthecost.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserRolesService userRolesService;

    public AuthenticationService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            UserRolesService userRolesService
    ) {
        this.authenticationManager = authenticationManager;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRolesService = userRolesService;
    }

    public User signup(RegisterUserDto input) {
        if (input.getRole().equals("ADMIN") || input.getRole().equals("SUPER_ADMIN")) {
            return null;
        }

        Optional<Role> optionalRole = roleRepository.findByName("USER");

        if (optionalRole.isEmpty()) {
            return null;
        }

        Optional<Role> optionalRole2 = roleRepository.findByName(input.getRole());

        if (optionalRole2.isEmpty()) {
            return null;
        }
        Set<Role> roles = new HashSet<>();
        roles.add(optionalRole.get());
        roles.add(optionalRole2.get());

        log.debug("Registering new user with email: {}", input.getEmail());
        User user = new User();
        user.setUsername(input.getUsername());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setRoles(roles);

        user = userRepository.save(user);

        userRolesService.assignRole(user, optionalRole.get());
        userRolesService.assignRole(user, optionalRole2.get());

        return user;
    }

    public User authenticate(LoginUserDto input) {
        log.debug("Authenticating user with email: {}", input.getEmail());
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        log.debug("User authenticated successfully: {}", user.getUsername());
        return user;
    }
}