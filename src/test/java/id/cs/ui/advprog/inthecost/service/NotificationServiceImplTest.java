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
        
        testKost = new Kost("Test Kost", "Test Address", "Test Description", 5, 1000000);
        testKost.setKostID(UUID.randomUUID());
    }
    
    @Test
    void notifyUsers_WithDuplicateNotifications_ShouldNotCreateDuplicates() {
        // Arrange
        Set<String> wishlistedUsers = Set.of(testUserId.toString());
        String expectedMessage = "Kamar tersedia di Test Kost";
        
        when(wishlistRepository.findUserIdsByKostId(testKost.getKostID())).thenReturn(wishlistedUsers);
        when(inboxRepository.existsByUserIdAndMessage(testUserId, expectedMessage)).thenReturn(true);
        
        // Act
        notificationService.notifyUsers(testKost);
        
        // Assert
        verify(inboxRepository, never()).save(any(InboxNotification.class));
    }
    
    @Test
    void notifyUsers_WithExceptionDuringRetrieval_ShouldHandleGracefully() {
        // Arrange
        when(wishlistRepository.findUserIdsByKostId(testKost.getKostID())).thenThrow(new RuntimeException("Database error"));
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> notificationService.notifyUsers(testKost));
    }
    
    @Test
    void notifyUsers_WithUserNotFound_ShouldThrowException() {
        // Arrange
        Set<String> wishlistedUsers = Set.of(testUserId.toString());
        
        when(wishlistRepository.findUserIdsByKostId(testKost.getKostID())).thenReturn(wishlistedUsers);
        when(inboxRepository.existsByUserIdAndMessage(eq(testUserId), anyString())).thenReturn(false);
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> notificationService.notifyUsers(testKost));
    }
    
    @Test
    void notifyUsers_WithValidInputs_ShouldCreateNotificationWithCorrectTimestamp() {
        // Arrange
        Set<String> wishlistedUsers = Set.of(testUserId.toString());
        
        when(wishlistRepository.findUserIdsByKostId(testKost.getKostID())).thenReturn(wishlistedUsers);
        when(inboxRepository.existsByUserIdAndMessage(eq(testUserId), anyString())).thenReturn(false);
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        
        // Act
        notificationService.notifyUsers(testKost);
        
        // Assert
        verify(inboxRepository).save(notificationCaptor.capture());
        InboxNotification savedNotification = notificationCaptor.getValue();
        
        assertEquals(testUser, savedNotification.getUser());
        assertTrue(savedNotification.getMessage().contains(testKost.getNama()));
        
        // If your InboxNotification has a createdAt field, verify it's recent
        if (savedNotification.getCreatedAt() != null) {
            LocalDateTime now = LocalDateTime.now();
            assertTrue(savedNotification.getCreatedAt().isBefore(now.plusSeconds(1)));
            assertTrue(savedNotification.getCreatedAt().isAfter(now.minusMinutes(1)));
        }
    }
    
    @Test
    void getInbox_WithInvalidUUID_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> notificationService.getInbox("not-a-uuid"));
    }
    
    @Test
    void getInbox_WithDatabaseError_ShouldPropagateException() {
        // Arrange
        when(inboxRepository.findByUserId(any(UUID.class))).thenThrow(new DataIntegrityViolationException("Database error"));
        
        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> notificationService.getInbox(testUserId.toString()));
    }
    
    @Test
    void getInbox_WithEmptyResult_ShouldReturnEmptyList() {
        // Arrange
        when(inboxRepository.findByUserId(testUserId)).thenReturn(new ArrayList<>());
        
        // Act
        List<InboxNotification> result = notificationService.getInbox(testUserId.toString());
        
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
        when(inboxRepository.findByUserId(testUserId)).thenReturn(notifications);
        
        // Act
        long count = notificationService.countNotifications(testUserId.toString());
        
        // Assert
        assertEquals(2, count);
    }
    
    @Test
    void countNotifications_WithInvalidUUID_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            notificationService.countNotifications("invalid-uuid"));
    }
    
    @Test
    void createNotification_WithValidData_ShouldSaveNotification() {
        // Arrange
        String message = "Test notification message";
        InboxNotification notification = new InboxNotification(testUser, message);
        
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(inboxRepository.save(any(InboxNotification.class))).thenReturn(notification);
        
        // Act
        InboxNotification result = notificationService.createNotification(testUserId.toString(), message);
        
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
    void createNotification_WithNullUserId_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            notificationService.createNotification(null, "Test message"));
    }
    
    @Test
    void createNotification_WithEmptyMessage_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            notificationService.createNotification(testUserId.toString(), ""));
    }
    
    @Test
    void createNotification_WithUserNotFound_ShouldThrowException() {
        // Arrange
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            notificationService.createNotification(testUserId.toString(), "Test message"));
    }
    
    @Test
    void deleteNotification_WithExistingId_ShouldDeleteNotification() {
        // Arrange
        Long notificationId = 1L;
        when(inboxRepository.existsById(notificationId)).thenReturn(true);
        
        // Act
        notificationService.deleteNotification(notificationId);
        
        // Assert
        verify(inboxRepository).deleteById(notificationId);
    }
    
    @Test
    void deleteNotification_WithNonExistentId_ShouldThrowException() {
        // Arrange
        Long notificationId = 1L;
        when(inboxRepository.existsById(notificationId)).thenReturn(false);
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            notificationService.deleteNotification(notificationId));
    }
    
    @Test
    void deleteNotification_WithNullId_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            notificationService.deleteNotification(null));
    }
    
    @Test
    void getNotificationById_WithExistingId_ShouldReturnNotification() {
        // Arrange
        Long notificationId = 1L;
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
        Long notificationId = 1L;
        when(inboxRepository.findById(notificationId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            notificationService.getNotificationById(notificationId));
    }
    
    @Test
    void getNotificationById_WithNullId_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            notificationService.getNotificationById(null));
    }
}
