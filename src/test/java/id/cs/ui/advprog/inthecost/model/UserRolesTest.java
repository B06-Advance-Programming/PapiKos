package id.cs.ui.advprog.inthecost.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserRolesTest {

    @Test
    public void testConstructorAndGetters() {
        User user = new User();
        user.setId(UUID.randomUUID());

        Role role = new Role("PENYEWA");

        UserRoles userRoles = new UserRoles(user, role);

        assertEquals(user, userRoles.getUser());
        assertEquals(role, userRoles.getRole());
        assertNull(userRoles.getId()); // ID belum di-set
    }

    @Test
    public void testSetters() {
        UserRoles userRoles = new UserRoles();
        userRoles.setId(1);

        User user = new User();
        user.setId(UUID.randomUUID());
        userRoles.setUser(user);

        Role role = new Role("ADMIN");
        userRoles.setRole(role);

        assertEquals(1, userRoles.getId());
        assertEquals(user, userRoles.getUser());
        assertEquals(role, userRoles.getRole());
    }

    @Test
    public void testEqualsAndHashCode_sameObject() {
        User user = new User();
        user.setId(UUID.randomUUID());

        Role role = new Role("ADMIN");

        UserRoles ur1 = new UserRoles(user, role);
        UserRoles ur2 = new UserRoles(user, role);

        assertEquals(ur1, ur2);
        assertEquals(ur1.hashCode(), ur2.hashCode());
    }

    @Test
    public void testEqualsAndHashCode_differentObject() {
        User user1 = new User();
        user1.setId(UUID.randomUUID());

        User user2 = new User();
        user2.setId(UUID.randomUUID());

        Role role1 = new Role("ADMIN");
        Role role2 = new Role("PENYEWA");

        UserRoles ur1 = new UserRoles(user1, role1);
        UserRoles ur2 = new UserRoles(user2, role1);
        UserRoles ur3 = new UserRoles(user1, role2);

        assertNotEquals(ur1, ur2);
        assertNotEquals(ur1, ur3);
    }

    @Test
    public void testEqualsWithNullAndOtherType() {
        User user = new User();
        user.setId(UUID.randomUUID());

        Role role = new Role("ADMIN");

        UserRoles ur = new UserRoles(user, role);

        assertNotEquals(ur, null);
        assertNotEquals(ur, "Some String");
    }
}
