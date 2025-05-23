package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.dto.RegisterAdminDto;
import id.cs.ui.advprog.inthecost.dto.RegisterUserDto;
import id.cs.ui.advprog.inthecost.model.Role;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.repository.RoleRepository;
import id.cs.ui.advprog.inthecost.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRolesService userRolesService;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, UserRolesService userRolesService ,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRolesService = userRolesService;
    }

    public List<User> allUsers() {
        List<User> users = new ArrayList<>();

        userRepository.findAll().forEach(users::add);

        return users;
    }

    public User createAdministrator(RegisterAdminDto input) {
        Optional<Role> optionalRole = roleRepository.findByName("ADMIN");

        if (optionalRole.isEmpty()) {
            return null;
        }

        Optional<Role> optionalRole2 = roleRepository.findByName("USER");

        if (optionalRole2.isEmpty()) {
            return null;
        }

        Set<Role> roles = new HashSet<>();
        roles.add(optionalRole.get());
        roles.add(optionalRole2.get());

        var user = new User();
        user.setUsername(input.getUsername());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));
        user.setRoles(roles);

        user = userRepository.save(user);

        userRolesService.assignRole(user, optionalRole.get());
        userRolesService.assignRole(user, optionalRole2.get());

        return user;
    }
}
