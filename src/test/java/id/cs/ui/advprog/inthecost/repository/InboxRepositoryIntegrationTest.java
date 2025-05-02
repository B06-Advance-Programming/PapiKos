package id.cs.ui.advprog.inthecost.repository;

import id.cs.ui.advprog.inthecost.model.InboxNotification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Use the actual database
public class InboxRepositoryIntegrationTest {

    @Autowired
    private InboxRepository inboxRepository;

    @Test
    void testSaveAndRetrieveNotification() {
        InboxNotification notification = new InboxNotification("user123", "Database test message");
        inboxRepository.save(notification);

        List<InboxNotification> notifications = inboxRepository.findByUserId("user123");

        assertNotNull(notifications);
        assertEquals(1, notifications.size());
        assertEquals("Database test message", notifications.get(0).getMessage());
    }

    @Test
    void testFindByUserIdReturnsEmptyListIfNoNotifications() {
        List<InboxNotification> notifications = inboxRepository.findByUserId("nonexistentUser");

        assertNotNull(notifications);
        assertTrue(notifications.isEmpty());
    }
}