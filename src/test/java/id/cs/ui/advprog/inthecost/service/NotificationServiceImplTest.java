package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.InboxNotification;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.repository.InboxRepository;
import id.cs.ui.advprog.inthecost.repository.UserRepository;
import id.cs.ui.advprog.inthecost.repository.WishlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private InboxRepository inboxRepository;

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Captor
    private ArgumentCaptor<InboxNotification> notificationCaptor;

    private Kost testKost;
    private User testUser;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(testUserId);
        testUser.setUsername("testUser");

        UUID ownerId = UUID.randomUUID(); // Added for Kost constructor if needed
        testKost = new Kost("Test Kost", "Test Address", "Test Description", 5, 1000000, ownerId);
        testKost.setKostID(UUID.randomUUID());
    }    @Test
    void notifyUsers_WithValidInputs_ShouldCreateNotification() {
        // Arrange
        Set<String> wishlistedUsers = Set.of(testUserId.toString());

        when(wishlistRepository.findUserIdsByKostId(testKost.getKostID())).thenReturn(wishlistedUsers);
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // Act
        final Kost kostArg = testKost;
        notificationService.notifyUsers(kostArg);

        // Assert
        verify(inboxRepository).save(notificationCaptor.capture());
        InboxNotification savedNotification = notificationCaptor.getValue();

        assertEquals(testUser, savedNotification.getUser());
        assertTrue(savedNotification.getMessage().contains(testKost.getNama()));
        assertTrue(savedNotification.getMessage().contains("kamar tersedia"));

        if (savedNotification.getCreatedAt() != null) {
            LocalDateTime now = LocalDateTime.now();
            assertTrue(savedNotification.getCreatedAt().isBefore(now.plusSeconds(1)));
            assertTrue(savedNotification.getCreatedAt().isAfter(now.minusMinutes(1)));
        }
    }

    @Test
    void notifyUsers_WithExceptionDuringRetrieval_ShouldHandleGracefully() {
        // Arrange
        RuntimeException dbException = new RuntimeException("Database error");
        when(wishlistRepository.findUserIdsByKostId(testKost.getKostID())).thenThrow(dbException);

        // Act & Assert
        final Kost kostArg = testKost;
        assertThrows(RuntimeException.class, () -> notificationService.notifyUsers(kostArg));
    }    @Test
    void notifyUsers_WithUserNotFound_ShouldThrowException() {
        // Arrange
        Set<String> wishlistedUsers = Set.of(testUserId.toString());

        when(wishlistRepository.findUserIdsByKostId(testKost.getKostID())).thenReturn(wishlistedUsers);
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // Act & Assert
        final Kost kostArg = testKost;
        assertThrows(IllegalArgumentException.class, () -> notificationService.notifyUsers(kostArg));
    }    @Test
    void notifyUsers_WithValidInputs_ShouldCreateNotificationWithCorrectTimestamp() {
        // Arrange
        Set<String> wishlistedUsers = Set.of(testUserId.toString());

        when(wishlistRepository.findUserIdsByKostId(testKost.getKostID())).thenReturn(wishlistedUsers);
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // Act
        final Kost kostArg = testKost;
        notificationService.notifyUsers(kostArg);

        // Assert
        verify(inboxRepository).save(notificationCaptor.capture());
        InboxNotification savedNotification = notificationCaptor.getValue();

        assertEquals(testUser, savedNotification.getUser());
        assertTrue(savedNotification.getMessage().contains(testKost.getNama()));
        assertTrue(savedNotification.getMessage().contains("kamar tersedia"));

        if (savedNotification.getCreatedAt() != null) {
            LocalDateTime now = LocalDateTime.now();
            assertTrue(savedNotification.getCreatedAt().isBefore(now.plusSeconds(1)));
            assertTrue(savedNotification.getCreatedAt().isAfter(now.minusMinutes(1)));
        }
    }

    @Test
    void getInbox_WithInvalidUUID_ShouldThrowException() {
        // Act & Assert
        final String invalidUuid = "not-a-uuid";
        assertThrows(IllegalArgumentException.class, () -> notificationService.getInbox(invalidUuid));
    }

    @Test
    void getInbox_WithDatabaseError_ShouldPropagateException() {
        // Arrange
        DataIntegrityViolationException dbException = new DataIntegrityViolationException("Database error");
        when(inboxRepository.findByUserId(any(UUID.class))).thenThrow(dbException);

        // Act & Assert
        final String userIdStr = testUserId.toString();
        assertThrows(DataIntegrityViolationException.class, () -> notificationService.getInbox(userIdStr));
    }

    @Test
    void getInbox_WithEmptyResult_ShouldReturnEmptyList() {
        // Arrange
        final String userIdStr = testUserId.toString();
        when(inboxRepository.findByUserId(testUserId)).thenReturn(new ArrayList<>());

        // Act
        List<InboxNotification> result = notificationService.getInbox(userIdStr);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void countNotifications_WithValidUserId_ShouldReturnCorrectCount() {
        // Arrange
        List<InboxNotification> notifications = List.of(
                new InboxNotification(testUser, "Test notification 1"),
                new InboxNotification(testUser, "Test notification 2")
        );
        final String userIdStr = testUserId.toString();
        when(inboxRepository.findByUserId(testUserId)).thenReturn(notifications);

        // Act
        long count = notificationService.countNotifications(userIdStr);

        // Assert
        assertEquals(2, count);
    }

    @Test
    void countNotifications_WithInvalidUUID_ShouldThrowException() {
        // Act & Assert
        final String invalidUuid = "invalid-uuid";
        assertThrows(IllegalArgumentException.class, () ->
                notificationService.countNotifications(invalidUuid));
    }

    @Test
    void createNotification_WithValidData_ShouldSaveNotification() {
        // Arrange
        final String message = "Test notification message";
        InboxNotification notification = new InboxNotification(testUser, message);
        final String userIdStr = testUserId.toString();

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(inboxRepository.save(any(InboxNotification.class))).thenReturn(notification);

        // Act
        InboxNotification result = notificationService.createNotification(userIdStr, message);

        // Assert
        assertNotNull(result);
        assertEquals(message, result.getMessage());
        assertEquals(testUser, result.getUser());

        verify(inboxRepository).save(notificationCaptor.capture());
        InboxNotification savedNotification = notificationCaptor.getValue();
        assertEquals(testUser, savedNotification.getUser());
        assertEquals(message, savedNotification.getMessage());
    }

    @Test
    void createNotificationForAllUsers_WithValidMessage_ShouldNotifyAllUsers() {
        // Arrange
        final String message = "Broadcast notification";
        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setUsername("user1");

        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setUsername("user2");

        List<User> allUsers = List.of(user1, user2);

        when(userRepository.findAll()).thenReturn(allUsers);
        when(inboxRepository.save(any(InboxNotification.class))).thenReturn(new InboxNotification());

        // Act
        int count = notificationService.createNotificationForAllUsers(message);

        // Assert
        assertEquals(2, count);
        verify(inboxRepository, times(2)).save(any(InboxNotification.class));
    }

    @Test
    void createNotificationForAllUsers_WithEmptyMessage_ShouldThrowException() {
        // Act & Assert
        final String emptyMessage = "";
        assertThrows(IllegalArgumentException.class, () ->
                notificationService.createNotificationForAllUsers(emptyMessage));
    }

    @Test
    void createNotificationForAllUsers_WithNullMessage_ShouldThrowException() {
        // Act & Assert
        final String nullMessage = null;
        assertThrows(IllegalArgumentException.class, () ->
                notificationService.createNotificationForAllUsers(nullMessage));
    }

    @Test
    void createNotification_WithNullUserId_ShouldThrowException() {
        // Act & Assert
        final String nullUserId = null;
        final String message = "Test message";
        assertThrows(IllegalArgumentException.class, () ->
                notificationService.createNotification(nullUserId, message));
    }

    @Test
    void createNotification_WithEmptyMessage_ShouldThrowException() {
        // Act & Assert
        final String userIdStr = testUserId.toString();
        final String emptyMessage = "";
        assertThrows(IllegalArgumentException.class, () ->
                notificationService.createNotification(userIdStr, emptyMessage));
    }

    @Test
    void createNotification_WithWhitespaceOnlyMessage_ShouldThrowException() {
        // Act & Assert
        final String userIdStr = testUserId.toString();
        final String whitespaceMessage = "   ";
        assertThrows(IllegalArgumentException.class, () ->
                notificationService.createNotification(userIdStr, whitespaceMessage));
    }

    @Test
    void createNotification_WithUserNotFound_ShouldThrowException() {
        // Arrange
        final String userIdStr = testUserId.toString();
        final String message = "Test message";
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                notificationService.createNotification(userIdStr, message));
    }

    @Test
    void deleteNotification_WithExistingId_ShouldDeleteNotification() {
        // Arrange
        final UUID notificationId = UUID.randomUUID();
        when(inboxRepository.existsById(notificationId)).thenReturn(true);

        // Act
        notificationService.deleteNotification(notificationId);

        // Assert
        verify(inboxRepository).deleteById(notificationId);
    }

    @Test
    void deleteNotification_WithNonExistentId_ShouldThrowException() { // Sonar line 256 area
        // Arrange
        final UUID notificationId = UUID.randomUUID();
        when(inboxRepository.existsById(notificationId)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                notificationService.deleteNotification(notificationId));
    }

    @Test
    void deleteNotification_WithNullId_ShouldThrowException() {
        // Act & Assert
        final UUID nullNotificationId = null;
        assertThrows(IllegalArgumentException.class, () ->
                notificationService.deleteNotification(nullNotificationId));
    }

    @Test
    void getNotificationById_WithExistingId_ShouldReturnNotification() {
        // Arrange
        final UUID notificationId = UUID.randomUUID();
        InboxNotification notification = new InboxNotification(testUser, "Test notification");

        when(inboxRepository.findById(notificationId)).thenReturn(Optional.of(notification));

        // Act
        InboxNotification result = notificationService.getNotificationById(notificationId);

        // Assert
        assertNotNull(result);
        assertEquals(notification, result);
    }

    @Test
    void getNotificationById_WithNonExistentId_ShouldThrowException() {
        // Arrange
        final UUID notificationId = UUID.randomUUID();
        when(inboxRepository.findById(notificationId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                notificationService.getNotificationById(notificationId));
    }

    @Test
    void getNotificationById_WithNullId_ShouldThrowException() {
        // Act & Assert
        final UUID nullNotificationId = null;
        assertThrows(IllegalArgumentException.class, () ->
                notificationService.getNotificationById(nullNotificationId));
    }

    @Test
    void notifyUsers_WithNullKost_ShouldThrowException() {
        // Act & Assert
        final Kost nullKost = null;
        assertThrows(NullPointerException.class, () -> notificationService.notifyUsers(nullKost));
    }    @Test
    void notifyUsers_WithRepositorySaveFailure_ShouldPropagateException() {
        // Arrange
        Set<String> wishlistedUsers = Set.of(testUserId.toString());
        DataIntegrityViolationException saveException = new DataIntegrityViolationException("Save failed");
        final Kost kostArg = testKost;

        when(wishlistRepository.findUserIdsByKostId(kostArg.getKostID())).thenReturn(wishlistedUsers);
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(inboxRepository.save(any(InboxNotification.class))).thenThrow(saveException);

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> notificationService.notifyUsers(kostArg));
    }

    @Test
    void getInbox_WithNullUserId_ShouldThrowException() {
        // Act & Assert
        final String nullUserId = null;
        assertThrows(NullPointerException.class, () -> notificationService.getInbox(nullUserId));
    }

    @Test
    void countNotifications_WithNullUserId_ShouldThrowException() {
        // Act & Assert
        final String nullUserId = null;
        assertThrows(NullPointerException.class, () -> notificationService.countNotifications(nullUserId));
    }

    @Test
    void countNotifications_WithDatabaseError_ShouldPropagateException() {
        // Arrange
        DataIntegrityViolationException dbException = new DataIntegrityViolationException("Database error");
        final String userIdStr = testUserId.toString();
        when(inboxRepository.findByUserId(any(UUID.class))).thenThrow(dbException);

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> notificationService.countNotifications(userIdStr));
    }

    @Test
    void createNotificationForAllUsers_WithDatabaseErrorOnUserRetrieval_ShouldPropagateException() { // Sonar line 369 area
        // Arrange
        DataIntegrityViolationException dbException = new DataIntegrityViolationException("Database error");
        final String message = "Test message";
        when(userRepository.findAll()).thenThrow(dbException);

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () ->
                notificationService.createNotificationForAllUsers(message));
    }

    @Test
    void createNotificationForAllUsers_WithSaveFailure_ShouldPropagateException() {
        // Arrange
        List<User> users = List.of(testUser);
        DataIntegrityViolationException saveException = new DataIntegrityViolationException("Save failed");
        final String message = "Test message";
        when(userRepository.findAll()).thenReturn(users);
        when(inboxRepository.save(any(InboxNotification.class))).thenThrow(saveException);

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () ->
                notificationService.createNotificationForAllUsers(message));
    }

    @Test
    void deleteNotification_WithDatabaseError_ShouldPropagateException() {
        // Arrange
        final UUID notificationId = UUID.randomUUID();
        DataIntegrityViolationException dbException = new DataIntegrityViolationException("Database error");
        when(inboxRepository.existsById(notificationId)).thenThrow(dbException);

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () ->
                notificationService.deleteNotification(notificationId));
    }

    @Test
    void getNotificationById_WithDatabaseError_ShouldPropagateException() {
        // Arrange
        final UUID notificationId = UUID.randomUUID();
        DataIntegrityViolationException dbException = new DataIntegrityViolationException("Database error");
        when(inboxRepository.findById(notificationId)).thenThrow(dbException);

        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () ->
                notificationService.getNotificationById(notificationId));
    }

    @Test
    void notifyUsers_WithKostHavingNullId_ShouldHandleGracefully() {
        // Arrange
        final Kost kostWithNullId = new Kost("Test Kost", "Test Address", "Test Description", 5, 1000000, testUserId); // Assuming ownerId is testUserId for constructor
        kostWithNullId.setKostID(null); // Explicitly set to null

        when(wishlistRepository.findUserIdsByKostId(null)).thenReturn(null); // Or an empty set depending on expected behavior

        // Act & Assert - should not throw an exception
        assertDoesNotThrow(() -> notificationService.notifyUsers(kostWithNullId));

        // Verify that no repository save calls were made if that's the expected outcome
        verify(inboxRepository, never()).save(any(InboxNotification.class));
    }
}