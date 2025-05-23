package id.cs.ui.advprog.inthecost.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class InboxNotificationTest {

    @Test
    void testCreateNotification() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("user1");

        InboxNotification notif = new InboxNotification(user, "Kamar tersedia di Kos Mawar");

        assertEquals(user, notif.getUser());
        assertEquals("Kamar tersedia di Kos Mawar", notif.getMessage());
        assertNotNull(notif.getCreatedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("user1");

        LocalDateTime now = LocalDateTime.now();

        InboxNotification notif1 = new InboxNotification(user, "Tes", now);
        InboxNotification notif2 = new InboxNotification(user, "Tes", now);

        assertEquals(notif1, notif2);
        assertEquals(notif1.hashCode(), notif2.hashCode());
    }

    @Test
    void testToStringIsNotNull() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("user2");

        InboxNotification notif = new InboxNotification(user, "Notif contoh");
        assertNotNull(notif.toString());
    }

    @Test
    void testDefaultCreatedAt() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("user3");

        InboxNotification notif = new InboxNotification(user, "Default timestamp test");
        assertNotNull(notif.getCreatedAt());
        assertTrue(notif.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
}