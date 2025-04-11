package id.cs.ui.advprog.inthecost.model;

import org.junit.jupiter.api.Test;

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
        InboxNotification notif1 = new InboxNotification("user1", "Tes");
        InboxNotification notif2 = new InboxNotification("user1", "Tes");

        assertEquals(notif1, notif2);
        assertEquals(notif1.hashCode(), notif2.hashCode());
    }

    @Test
    void testToStringIsNotNull() {
        InboxNotification notif = new InboxNotification("user2", "Notif contoh");
        assertNotNull(notif.toString());
    }
}
