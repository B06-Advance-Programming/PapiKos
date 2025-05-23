package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.model.Role;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.model.UserRoles;
import id.cs.ui.advprog.inthecost.repository.UserRolesRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserRolesServiceImpl implements UserRolesService {

    private final UserRolesRepository repository;

    public UserRolesServiceImpl(UserRolesRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserRoles assignRole(User user, Role role) {
        Optional<UserRoles> existing = repository.findByUserAndRole(user, role);
        if (existing.isPresent()) return existing.get();

        UserRoles newRole = new UserRoles(user, role);
        return repository.save(newRole);
    }

    @Override
    public void removeRole(User user, Role role) {
        repository.findByUserAndRole(user, role).ifPresent(repository::delete);
    }

    @Override
    public List<UserRoles> getRolesByUser(User user) {
        return repository.findByUser(user);
    }

    @Override
    public boolean hasRole(User user, String roleName) {
        return repository.findByUser(user).stream()
                .anyMatch(ur -> ur.getRole().getName().equalsIgnoreCase(roleName));
    }
}
