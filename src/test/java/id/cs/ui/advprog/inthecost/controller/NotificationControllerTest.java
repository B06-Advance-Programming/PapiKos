package id.cs.ui.advprog.inthecost.controller;

import id.cs.ui.advprog.inthecost.model.InboxNotification;
import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.repository.KostRepository;
import id.cs.ui.advprog.inthecost.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class NotificationControllerTest {

    @InjectMocks
    private NotificationController notificationController;

    @Mock
    private NotificationService notificationService;

    @Mock
    private KostRepository kostRepository;

    private UUID userId;
    private UUID kostId;
    private UUID notificationId;
    private User user;
    private Kost kost;
    private InboxNotification notification;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        userId = UUID.randomUUID();
        kostId = UUID.randomUUID();
        notificationId = UUID.randomUUID();
        
        user = new User();
        user.setId(userId);
        user.setUsername("testUser");
        
        kost = new Kost();
        kost.setKostID(kostId);
        kost.setNama("Test Kost");
        
        notification = new InboxNotification(user, "Test notification");
    }

    @Test
    void testGetInbox_Success() {
        List<InboxNotification> notifications = List.of(notification);
        
        when(notificationService.getInbox(userId.toString())).thenReturn(notifications);

        ResponseEntity<List<InboxNotification>> response = notificationController.getInbox(userId.toString());
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(notifications, response.getBody());
    }

    @Test
    void testGetInbox_EmptyList() {
        List<InboxNotification> emptyNotifications = new ArrayList<>();
        
        when(notificationService.getInbox(userId.toString())).thenReturn(emptyNotifications);

        ResponseEntity<List<InboxNotification>> response = notificationController.getInbox(userId.toString());
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(emptyNotifications, response.getBody());
        assertTrue(response.getBody().isEmpty());
    }    @Test
    void testGetInbox_ServiceException() {
        RuntimeException serviceException = new RuntimeException("Database connection failed");
        when(notificationService.getInbox(anyString()))
                .thenThrow(serviceException);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.getInbox(userId.toString());
        });
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Error retrieving notifications"));
    }
      @Test
    void testGetInbox_InvalidUserId() {
        IllegalArgumentException formatException = new IllegalArgumentException("Invalid UUID format");
        when(notificationService.getInbox(anyString())).thenThrow(formatException);

        assertThrows(ResponseStatusException.class, () -> {
            notificationController.getInbox("invalid-id");
        });
    }
    
    @Test
    void testGetNotificationCount_Success() {
        when(notificationService.countNotifications(userId.toString())).thenReturn(3L);

        ResponseEntity<Map<String, Long>> response = notificationController.getNotificationCount(userId.toString());
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3L, response.getBody().get("count"));
    }

    @Test
    void testGetNotificationCount_ZeroCount() {
        when(notificationService.countNotifications(userId.toString())).thenReturn(0L);

        ResponseEntity<Map<String, Long>> response = notificationController.getNotificationCount(userId.toString());
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0L, response.getBody().get("count"));
    }    @Test
    void testGetNotificationCount_InvalidUserId() {
        IllegalArgumentException formatException = new IllegalArgumentException("Invalid UUID format");
        when(notificationService.countNotifications(anyString()))
                .thenThrow(formatException);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.getNotificationCount("invalid-id");
        });
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Invalid user ID format"));
    }    @Test
    void testGetNotificationCount_ServiceException() {
        RuntimeException serviceException = new RuntimeException("Database error");
        when(notificationService.countNotifications(anyString()))
                .thenThrow(serviceException);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.getNotificationCount(userId.toString());
        });
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Error counting notifications"));
    }
    
    @Test
    void testTriggerNotification_Success() {
        when(kostRepository.findById(kostId)).thenReturn(Optional.of(kost));
        doNothing().when(notificationService).notifyUsers(any(Kost.class));

        ResponseEntity<Map<String, String>> response = notificationController.triggerNotification(kostId.toString());
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("Notifications sent for kost: Test Kost", response.getBody().get("message"));
        
        verify(notificationService, times(1)).notifyUsers(any(Kost.class));
    }

    @Test
    void testTriggerNotification_InvalidKostId() {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.triggerNotification("invalid-kost-id");
        });
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Invalid kost ID format"));
    }    @Test
    void testTriggerNotification_ServiceException() {
        when(kostRepository.findById(kostId)).thenReturn(Optional.of(kost));
        RuntimeException serviceException = new RuntimeException("Notification service error");
        doThrow(serviceException)
                .when(notificationService).notifyUsers(any(Kost.class));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.triggerNotification(kostId.toString());
        });
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Error triggering notifications"));
    }
    
    @Test
    void testTriggerNotification_KostNotFound() {
        when(kostRepository.findById(kostId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            notificationController.triggerNotification(kostId.toString());
        });
    }

    @Test
    void testGetNotificationById_Success() {
        when(notificationService.getNotificationById(notificationId)).thenReturn(notification);

        ResponseEntity<InboxNotification> response = notificationController.getNotification(notificationId);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(notification, response.getBody());
    }    @Test
    void testGetNotificationById_ServiceException() {
        RuntimeException serviceException = new RuntimeException("Database error");
        when(notificationService.getNotificationById(notificationId))
                .thenThrow(serviceException);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.getNotification(notificationId);
        });
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Error retrieving notification"));
    }
      @Test
    void testGetNotificationById_NotFound() {
        IllegalArgumentException notFoundException = new IllegalArgumentException("Notification not found");
        when(notificationService.getNotificationById(notificationId)).thenThrow(notFoundException);

        assertThrows(ResponseStatusException.class, () -> {
            notificationController.getNotification(notificationId);
        });
    }
      @Test
    void testCreateNotification_Success() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("message", "New notification");
        
        when(notificationService.createNotification(eq(userId.toString()), anyString())).thenReturn(notification);

        ResponseEntity<InboxNotification> response = notificationController.createNotification(userId.toString(), requestBody);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(notification, response.getBody());
    }    @Test
    void testCreateNotification_ServiceException() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("message", "New notification");
        
        RuntimeException serviceException = new RuntimeException("Database error");
        when(notificationService.createNotification(eq(userId.toString()), anyString()))
                .thenThrow(serviceException);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.createNotification(userId.toString(), requestBody);
        });
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Error creating notification"));
    }    @Test
    void testCreateNotification_InvalidUserId() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("message", "New notification");
        
        IllegalArgumentException invalidIdException = new IllegalArgumentException("Invalid user ID");
        when(notificationService.createNotification(anyString(), anyString()))
                .thenThrow(invalidIdException);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.createNotification("invalid-id", requestBody);
        });
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }    

    @Test
    void testDeleteNotification_Success() {
        doNothing().when(notificationService).deleteNotification(notificationId);

        ResponseEntity<Map<String, String>> response = notificationController.deleteNotification(notificationId);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("Notification deleted successfully", response.getBody().get("message"));
        
        verify(notificationService, times(1)).deleteNotification(notificationId);
    }    @Test
    void testDeleteNotification_ServiceException() {
        RuntimeException serviceException = new RuntimeException("Database error");
        doThrow(serviceException)
                .when(notificationService).deleteNotification(notificationId);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.deleteNotification(notificationId);
        });
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Error deleting notification"));
    }
      @Test
    void testDeleteNotification_NotFound() {
        IllegalArgumentException notFoundException = new IllegalArgumentException("Notification not found");
        doThrow(notFoundException).when(notificationService).deleteNotification(notificationId);

        assertThrows(ResponseStatusException.class, () -> {
            notificationController.deleteNotification(notificationId);
        });
    }
    
    @Test
    void testHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid format");
        ResponseEntity<Map<String, String>> response = notificationController.handleIllegalArgumentException(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("error", response.getBody().get("status"));
        assertEquals("Invalid notification ID format", response.getBody().get("message"));
    }
      @Test
    void testBroadcastNotification_Success() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("message", "Broadcast notification to all users");
        
        when(notificationService.createNotificationForAllUsers(anyString())).thenReturn(5);

        ResponseEntity<Map<String, Object>> response = notificationController.broadcastNotification(requestBody);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("Notification broadcast sent successfully", response.getBody().get("message"));
        assertEquals(5, response.getBody().get("recipientCount"));
        
        verify(notificationService, times(1)).createNotificationForAllUsers(anyString());
    }
    
    @Test
    void testBroadcastNotification_EmptyRecipients() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("message", "Broadcast notification");
        
        when(notificationService.createNotificationForAllUsers(anyString())).thenReturn(0);

        ResponseEntity<Map<String, Object>> response = notificationController.broadcastNotification(requestBody);
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("success", response.getBody().get("status"));
        assertEquals(0, response.getBody().get("recipientCount"));
    }
    
    @Test
    void testBroadcastNotification_ServiceError() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("message", "Broadcast message");
        
        RuntimeException dbException = new RuntimeException("Database error");
        when(notificationService.createNotificationForAllUsers(anyString()))
            .thenThrow(dbException);

        assertThrows(ResponseStatusException.class, () -> {
            notificationController.broadcastNotification(requestBody);
        });
    }

    @Test
    void testBroadcastNotification_IllegalArgumentException() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("message", "Broadcast message");
        
        IllegalArgumentException invalidFormatException = new IllegalArgumentException("Invalid message format");
        when(notificationService.createNotificationForAllUsers(anyString()))
            .thenThrow(invalidFormatException);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.broadcastNotification(requestBody);
        });
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }
    
    // Parameterized test for create notification with invalid messages
    @ParameterizedTest
    @MethodSource("invalidMessageProvider")
    void testCreateNotification_InvalidMessage(Map<String, String> requestBody, String expectedErrorMessage) {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.createNotification(userId.toString(), requestBody);
        });
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains(expectedErrorMessage));
    }
    
    private static Stream<Arguments> invalidMessageProvider() {
        return Stream.of(
            // Null message
            Arguments.of(new HashMap<String, String>() {{ put("message", null); }}, "Message cannot be empty"),
            // Whitespace message
            Arguments.of(new HashMap<String, String>() {{ put("message", "   "); }}, "Message cannot be empty"),
            // Empty message
            Arguments.of(new HashMap<String, String>() {{ put("message", ""); }}, "Message cannot be empty"),
            // Missing message key
            Arguments.of(new HashMap<String, String>(), "Message cannot be empty")
        );
    }
    
    // Parameterized test for broadcast notification with invalid messages
    @ParameterizedTest
    @MethodSource("invalidMessageProvider")
    void testBroadcastNotification_InvalidMessage(Map<String, String> requestBody, String expectedErrorMessage) {
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.broadcastNotification(requestBody);
        });
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains(expectedErrorMessage));
    }
}