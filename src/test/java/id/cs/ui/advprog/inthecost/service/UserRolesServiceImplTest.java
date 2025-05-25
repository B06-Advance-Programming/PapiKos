package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.model.Role;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.model.UserRoles;
import id.cs.ui.advprog.inthecost.repository.UserRolesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserRolesServiceImplTest {

    @Mock private UserRolesRepository repository;
    @InjectMocks private UserRolesServiceImpl userRolesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void assignRole_returnsExistingIfPresent() {
        User user = new User();
        Role role = new Role("USER");
        UserRoles existing = new UserRoles(user, role);

        when(repository.findByUserAndRole(user, role)).thenReturn(Optional.of(existing));

        UserRoles result = userRolesService.assignRole(user, role);

        assertEquals(existing, result);
        verify(repository, never()).save(any());
    }

    @Test
    void assignRole_savesNewIfNotPresent() {
        User user = new User();
        Role role = new Role("USER");
        UserRoles newRole = new UserRoles(user, role);

        when(repository.findByUserAndRole(user, role)).thenReturn(Optional.empty());
        when(repository.save(any(UserRoles.class))).thenReturn(newRole);

        UserRoles result = userRolesService.assignRole(user, role);

        assertEquals(newRole, result);
        verify(repository).save(any(UserRoles.class));
    }

    @Test
    void removeRole_deletesIfExists() {
        User user = new User();
        Role role = new Role("USER");
        UserRoles existing = new UserRoles(user, role);

        when(repository.findByUserAndRole(user, role)).thenReturn(Optional.of(existing));

        userRolesService.removeRole(user, role);

        verify(repository).delete(existing);
    }

    @Test
    void removeRole_doesNothingIfNotExists() {
        User user = new User();
        Role role = new Role("USER");

        when(repository.findByUserAndRole(user, role)).thenReturn(Optional.empty());

        userRolesService.removeRole(user, role);

        verify(repository, never()).delete(any());
    }

    @Test
    void getRolesByUser_returnsList() {
        User user = new User();
        List<UserRoles> rolesList = List.of(new UserRoles(user, new Role("USER")));

        when(repository.findByUser(user)).thenReturn(rolesList);

        List<UserRoles> result = userRolesService.getRolesByUser(user);

        assertEquals(rolesList, result);
    }

    @Test
    void hasRole_returnsTrueIfRoleFound() {
        User user = new User();
        Role role = new Role("USER");
        List<UserRoles> rolesList = List.of(new UserRoles(user, role));

        when(repository.findByUser(user)).thenReturn(rolesList);

        boolean result = userRolesService.hasRole(user, "USER");

        assertTrue(result);
    }

    @Test
    void hasRole_returnsFalseIfRoleNotFound() {
        User user = new User();
        List<UserRoles> rolesList = List.of(new UserRoles(user, new Role("ADMIN")));

        when(repository.findByUser(user)).thenReturn(rolesList);

        boolean result = userRolesService.hasRole(user, "USER");

        assertFalse(result);
    }
}
