package id.cs.ui.advprog.inthecost.repository;

import id.cs.ui.advprog.inthecost.model.InboxNotification;
import id.cs.ui.advprog.inthecost.model.Role;
import id.cs.ui.advprog.inthecost.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class InboxRepositoryIntegrationTest {

    @Autowired
    private InboxRepository inboxRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Clear any existing data
        inboxRepository.deleteAll();
        
        // Create and save a test user
        Set<Role> roles = new HashSet<>();
        testUser = new User("testuser", "password", "test@example.com", roles);
        testUser = userRepository.save(testUser);
    }

    @Test
    void testSaveAndRetrieveNotification() {
        // Create a notification for the user
        InboxNotification notification = new InboxNotification();
        notification.setUser(testUser);
        notification.setMessage("Test notification message");
        notification.setCreatedAt(LocalDateTime.now());
        inboxRepository.save(notification);

        // Retrieve notifications by user ID
        List<InboxNotification> notifications = inboxRepository.findByUserId(testUser.getId());
        
        // Assertions
        assertFalse(notifications.isEmpty(), "Notifications list should not be empty");
        assertEquals(1, notifications.size(), "Should have exactly one notification");
        assertEquals("Test notification message", notifications.get(0).getMessage(), 
                    "Message content should match");
        assertEquals(testUser.getId(), notifications.get(0).getUser().getId(), 
                    "User ID should match");
    }

    @Test
    void testFindByUserIdReturnsEmptyListIfNoNotifications() {
        // Create a user with no notifications
        Set<Role> roles = new HashSet<>();
        User anotherUser = new User("anotheruser", "password", "another@example.com", roles);
        anotherUser = userRepository.save(anotherUser);

        // Retrieve notifications for the user
        List<InboxNotification> notifications = inboxRepository.findByUserId(anotherUser.getId());
        
        // Assertion
        assertTrue(notifications.isEmpty(), "Notifications list should be empty");
    }

    @Test
    void testExistsByUserIdAndMessage() {
        // Create a notification with a specific message
        String message = "Unique test message";
        InboxNotification notification = new InboxNotification();
        notification.setUser(testUser);
        notification.setMessage(message);
        notification.setCreatedAt(LocalDateTime.now());
        inboxRepository.save(notification);

        // Check if the notification exists
        boolean exists = inboxRepository.existsByUserIdAndMessage(testUser.getId(), message);
        boolean nonExistent = inboxRepository.existsByUserIdAndMessage(testUser.getId(), "Non-existent message");
        
        // Assertions
        assertTrue(exists, "Should find the existing notification");
        assertFalse(nonExistent, "Should not find a non-existent notification");
    }

    @Test
    void testMultipleNotificationsForSameUser() {
        // Create multiple notifications for the same user
        InboxNotification notification1 = new InboxNotification();
        notification1.setUser(testUser);
        notification1.setMessage("First notification");
        notification1.setCreatedAt(LocalDateTime.now());
        inboxRepository.save(notification1);

        InboxNotification notification2 = new InboxNotification();
        notification2.setUser(testUser);
        notification2.setMessage("Second notification");
        notification2.setCreatedAt(LocalDateTime.now());
        inboxRepository.save(notification2);

        // Retrieve all notifications for the user
        List<InboxNotification> notifications = inboxRepository.findByUserId(testUser.getId());
        
        // Assertions
        assertEquals(2, notifications.size(), "Should have exactly two notifications");
        assertTrue(notifications.stream().anyMatch(n -> n.getMessage().equals("First notification")), 
                  "Should contain the first notification");
        assertTrue(notifications.stream().anyMatch(n -> n.getMessage().equals("Second notification")), 
                  "Should contain the second notification");
    }
}