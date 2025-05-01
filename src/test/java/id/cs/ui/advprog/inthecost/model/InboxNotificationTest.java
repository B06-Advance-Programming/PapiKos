package id.cs.ui.advprog.inthecost.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class InboxNotificationTest {

    @Test
    void testCreateNotification() {
        InboxNotification notif = new InboxNotification("user1", "Kamar tersedia di Kos Mawar");

        assertEquals("user1", notif.getUserId());
        assertEquals("Kamar tersedia di Kos Mawar", notif.getMessage());
        assertNotNull(notif.getCreatedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();

        InboxNotification notif1 = new InboxNotification("user1", "Tes", now);
        InboxNotification notif2 = new InboxNotification("user1", "Tes", now);

        assertEquals(notif1, notif2);
        assertEquals(notif1.hashCode(), notif2.hashCode());
    }

    @Test
    void testToStringIsNotNull() {
        InboxNotification notif = new InboxNotification("user2", "Notif contoh");
        assertNotNull(notif.toString());
    }

    @Test
    void testDefaultCreatedAt() {
        InboxNotification notif = new InboxNotification("user3", "Default timestamp test");
        assertNotNull(notif.getCreatedAt());
        assertTrue(notif.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
}