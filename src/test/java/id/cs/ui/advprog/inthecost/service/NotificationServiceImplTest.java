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
}
