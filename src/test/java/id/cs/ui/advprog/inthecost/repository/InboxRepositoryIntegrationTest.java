package id.cs.ui.advprog.inthecost.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class InboxRepositoryIntegrationTest {

    static class User {
        private UUID id;
        private String username;

        public User(String username) {
            this.username = username;
            this.id = UUID.randomUUID();
        }

        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
    }

    static class InboxNotification {
        private Long id;
        private User user;
        private String message;
        private LocalDateTime createdAt;

        public InboxNotification() {
            this.createdAt = LocalDateTime.now();
        }

        public InboxNotification(User user, String message, LocalDateTime createdAt) {
            this.user = user;
            this.message = message;
            this.createdAt = createdAt;
        }

        public InboxNotification(User user, String message) {
            this(user, message, LocalDateTime.now());
        }

        // getters/setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }

    static class FakeInboxRepository {
        private final Map<Long, InboxNotification> store = new HashMap<>();
        private long idSequence = 1L;

        public InboxNotification save(InboxNotification notification) {
            if (notification.getId() == null) {
                notification.setId(idSequence++);
            }
            store.put(notification.getId(), notification);
            return notification;
        }

        public List<InboxNotification> findByUserId(UUID userId) {
            return store.values().stream()
                    .filter(n -> n.getUser() != null && userId.equals(n.getUser().getId()))
                    .collect(Collectors.toList());
        }

        public boolean existsByUserIdAndMessage(UUID userId, String message) {
            return store.values().stream()
                    .anyMatch(n -> n.getUser() != null
                            && userId.equals(n.getUser().getId())
                            && Objects.equals(n.getMessage(), message));
        }

        public void deleteAll() {
            store.clear();
        }
    }

    private FakeInboxRepository inboxRepository;
    private User testUser;

    @BeforeEach
    void setUp() {
        inboxRepository = new FakeInboxRepository();
        inboxRepository.deleteAll();

        testUser = new User("testuser");
        // ID already set in constructor as UUID.randomUUID()
    }

    @Test
    void testSaveAndRetrieveNotification() {
        InboxNotification notification = new InboxNotification(testUser, "Test notification message");
        inboxRepository.save(notification);

        List<InboxNotification> notifications = inboxRepository.findByUserId(testUser.getId());
        assertFalse(notifications.isEmpty());
        assertEquals(1, notifications.size());
        assertEquals("Test notification message", notifications.get(0).getMessage());
        assertEquals(testUser.getId(), notifications.get(0).getUser().getId());
    }

    @Test
    void testFindByUserIdReturnsEmptyIfNoNotifications() {
        User otherUser = new User("otheruser");

        List<InboxNotification> notifications = inboxRepository.findByUserId(otherUser.getId());
        assertTrue(notifications.isEmpty());
    }

    @Test
    void testExistsByUserIdAndMessage() {
        String message = "Unique test message";
        InboxNotification notification = new InboxNotification(testUser, message);
        inboxRepository.save(notification);

        assertTrue(inboxRepository.existsByUserIdAndMessage(testUser.getId(), message));
        assertFalse(inboxRepository.existsByUserIdAndMessage(testUser.getId(), "Non-existent message"));
    }

    @Test
    void testMultipleNotificationsForSameUser() {
        InboxNotification n1 = new InboxNotification(testUser, "First notification");
        inboxRepository.save(n1);

        InboxNotification n2 = new InboxNotification(testUser, "Second notification");
        inboxRepository.save(n2);

        List<InboxNotification> notifications = inboxRepository.findByUserId(testUser.getId());
        assertEquals(2, notifications.size());
        assertTrue(notifications.stream().anyMatch(n -> n.getMessage().equals("First notification")));
        assertTrue(notifications.stream().anyMatch(n -> n.getMessage().equals("Second notification")));
    }
}