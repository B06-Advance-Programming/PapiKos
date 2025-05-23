package id.cs.ui.advprog.inthecost.repository;

import id.cs.ui.advprog.inthecost.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRolesRepository extends JpaRepository<User, UUID> {
}
