package id.cs.ui.advprog.inthecost.controller;

import id.cs.ui.advprog.inthecost.model.InboxNotification;
import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.repository.KostRepository;
import id.cs.ui.advprog.inthecost.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

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
    }

    @Test
    void testGetInbox_ServiceException() {
        when(notificationService.getInbox(anyString()))
                .thenThrow(new RuntimeException("Database connection failed"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.getInbox(userId.toString());
        });
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Error retrieving notifications"));
    }
    
    @Test
    void testGetInbox_InvalidUserId() {
        when(notificationService.getInbox(anyString())).thenThrow(new IllegalArgumentException("Invalid UUID format"));

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
    }

    @Test
    void testGetNotificationCount_InvalidUserId() {
        when(notificationService.countNotifications(anyString()))
                .thenThrow(new IllegalArgumentException("Invalid UUID format"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.getNotificationCount("invalid-id");
        });
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Invalid user ID format"));
    }

    @Test
    void testGetNotificationCount_ServiceException() {
        when(notificationService.countNotifications(anyString()))
                .thenThrow(new RuntimeException("Database error"));

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
    }

    @Test
    void testTriggerNotification_ServiceException() {
        when(kostRepository.findById(kostId)).thenReturn(Optional.of(kost));
        doThrow(new RuntimeException("Notification service error"))
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
    }

    @Test
    void testGetNotificationById_ServiceException() {
        when(notificationService.getNotificationById(notificationId))
                .thenThrow(new RuntimeException("Database error"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.getNotification(notificationId);
        });
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Error retrieving notification"));
    }
    
    @Test
    void testGetNotificationById_NotFound() {
        when(notificationService.getNotificationById(notificationId)).thenThrow(new IllegalArgumentException("Notification not found"));

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
    }

    @Test
    void testCreateNotification_NullMessage() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("message", null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.createNotification(userId.toString(), requestBody);
        });
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Message cannot be empty"));
    }

    @Test
    void testCreateNotification_WhitespaceMessage() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("message", "   ");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.createNotification(userId.toString(), requestBody);
        });
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Message cannot be empty"));
    }

    @Test
    void testCreateNotification_MissingMessageKey() {
        Map<String, String> requestBody = new HashMap<>();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.createNotification(userId.toString(), requestBody);
        });
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Message cannot be empty"));
    }

    @Test
    void testCreateNotification_ServiceException() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("message", "New notification");
        
        when(notificationService.createNotification(eq(userId.toString()), anyString()))
                .thenThrow(new RuntimeException("Database error"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.createNotification(userId.toString(), requestBody);
        });
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Error creating notification"));
    }

    @Test
    void testCreateNotification_InvalidUserId() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("message", "New notification");
        
        when(notificationService.createNotification(anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("Invalid user ID"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.createNotification("invalid-id", requestBody);
        });
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }
    
    @Test
    void testCreateNotification_EmptyMessage() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("message", "");

        assertThrows(ResponseStatusException.class, () -> {
            notificationController.createNotification(userId.toString(), requestBody);
        });
    }

    @Test
    void testDeleteNotification_Success() {
        doNothing().when(notificationService).deleteNotification(notificationId);

        ResponseEntity<Map<String, String>> response = notificationController.deleteNotification(notificationId);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("Notification deleted successfully", response.getBody().get("message"));
        
        verify(notificationService, times(1)).deleteNotification(notificationId);
    }

    @Test
    void testDeleteNotification_ServiceException() {
        doThrow(new RuntimeException("Database error"))
                .when(notificationService).deleteNotification(notificationId);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.deleteNotification(notificationId);
        });
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Error deleting notification"));
    }
    
    @Test
    void testDeleteNotification_NotFound() {
        doThrow(new IllegalArgumentException("Notification not found")).when(notificationService).deleteNotification(notificationId);

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
    void testBroadcastNotification_NullMessage() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("message", null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.broadcastNotification(requestBody);
        });
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Message cannot be empty"));
    }

    @Test
    void testBroadcastNotification_WhitespaceMessage() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("message", "   ");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.broadcastNotification(requestBody);
        });
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Message cannot be empty"));
    }    @Test
    void testBroadcastNotification_MissingMessageKey() {
        Map<String, String> requestBody = new HashMap<>();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.broadcastNotification(requestBody);
        });
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Message cannot be empty"));
    }
    
    @Test
    void testBroadcastNotification_EmptyMessage() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("message", "");

        assertThrows(ResponseStatusException.class, () -> {
            notificationController.broadcastNotification(requestBody);
        });
    }
    
    @Test
    void testBroadcastNotification_ServiceError() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("message", "Broadcast message");
        
        when(notificationService.createNotificationForAllUsers(anyString()))
            .thenThrow(new RuntimeException("Database error"));

        assertThrows(ResponseStatusException.class, () -> {
            notificationController.broadcastNotification(requestBody);
        });
    }

    @Test
    void testBroadcastNotification_IllegalArgumentException() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("message", "Broadcast message");
        
        when(notificationService.createNotificationForAllUsers(anyString()))
            .thenThrow(new IllegalArgumentException("Invalid message format"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.broadcastNotification(requestBody);
        });
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }
}