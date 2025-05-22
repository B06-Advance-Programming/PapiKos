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
    private User user;
    private Kost kost;
    private InboxNotification notification;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        userId = UUID.randomUUID();
        kostId = UUID.randomUUID();
        
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
    void testTriggerNotification_KostNotFound() {
        when(kostRepository.findById(kostId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            notificationController.triggerNotification(kostId.toString());
        });
    }
    
    @Test
    void testGetNotificationById_Success() {
        when(notificationService.getNotificationById(1L)).thenReturn(notification);

        ResponseEntity<InboxNotification> response = notificationController.getNotification(1L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(notification, response.getBody());
    }
    
    @Test
    void testGetNotificationById_NotFound() {
        when(notificationService.getNotificationById(1L)).thenThrow(new IllegalArgumentException("Notification not found"));

        assertThrows(ResponseStatusException.class, () -> {
            notificationController.getNotification(1L);
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
    void testCreateNotification_EmptyMessage() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("message", "");

        assertThrows(ResponseStatusException.class, () -> {
            notificationController.createNotification(userId.toString(), requestBody);
        });
    }
    
    @Test
    void testDeleteNotification_Success() {
        doNothing().when(notificationService).deleteNotification(1L);

        ResponseEntity<Map<String, String>> response = notificationController.deleteNotification(1L);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("Notification deleted successfully", response.getBody().get("message"));
        
        verify(notificationService, times(1)).deleteNotification(1L);
    }
    
    @Test
    void testDeleteNotification_NotFound() {
        doThrow(new IllegalArgumentException("Notification not found")).when(notificationService).deleteNotification(1L);

        assertThrows(ResponseStatusException.class, () -> {
            notificationController.deleteNotification(1L);
        });
    }
    
    @Test
    void testHandleNumberFormatException() {
        NumberFormatException ex = new NumberFormatException("Invalid format");
        ResponseEntity<Map<String, String>> response = notificationController.handleNumberFormatException(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("error", response.getBody().get("status"));
        assertEquals("Invalid notification ID format", response.getBody().get("message"));
    }
}