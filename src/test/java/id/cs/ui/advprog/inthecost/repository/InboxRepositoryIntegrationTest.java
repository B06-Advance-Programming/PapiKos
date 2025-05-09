package id.cs.ui.advprog.inthecost.repository;

import id.cs.ui.advprog.inthecost.model.InboxNotification;
import id.cs.ui.advprog.inthecost.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Use the actual database
public class InboxRepositoryIntegrationTest {

    @Autowired
    private InboxRepository inboxRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndRetrieveNotification() {
        // Create and save a User
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("user123");
        userRepository.save(user);

        // Create and save an InboxNotification
        InboxNotification notification = new InboxNotification(user, "Database test message");
        inboxRepository.save(notification);

        // Retrieve notifications for the user
        List<InboxNotification> notifications = inboxRepository.findByUserId(user.getId());

        assertNotNull(notifications);
        assertEquals(1, notifications.size());
        assertEquals("Database test message", notifications.get(0).getMessage());
    }

    @Test
    void testFindByUserIdReturnsEmptyListIfNoNotifications() {
        // Create and save a User
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("nonexistentUser");
        userRepository.save(user);

        // Retrieve notifications for a user with no notifications
        List<InboxNotification> notifications = inboxRepository.findByUserId(user.getId());

        assertNotNull(notifications);
        assertTrue(notifications.isEmpty());
    }
}