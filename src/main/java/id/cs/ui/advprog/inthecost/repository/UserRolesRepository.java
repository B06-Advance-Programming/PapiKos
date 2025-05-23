package id.cs.ui.advprog.inthecost.repository;

import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.model.UserRoles;
import id.cs.ui.advprog.inthecost.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRolesRepository extends JpaRepository<UserRoles, UUID> {
    List<UserRoles> findByUser(User user);
    List<UserRoles> findByRole(Role role);
    Optional<UserRoles> findByUserAndRole(User user, Role role);
    void deleteByUser(User user);
    void deleteByUserAndRole(User user, Role role);
}
