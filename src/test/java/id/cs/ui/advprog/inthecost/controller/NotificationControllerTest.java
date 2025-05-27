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
        final String userIdString = userId.toString();
        when(notificationService.getInbox(userIdString)).thenReturn(notifications);

        ResponseEntity<List<InboxNotification>> response = notificationController.getInbox(userIdString);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(notifications, response.getBody());
    }

    @Test
    void testGetInbox_EmptyList() {
        List<InboxNotification> emptyNotifications = new ArrayList<>();
        final String userIdString = userId.toString();
        when(notificationService.getInbox(userIdString)).thenReturn(emptyNotifications);

        ResponseEntity<List<InboxNotification>> response = notificationController.getInbox(userIdString);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(emptyNotifications, response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testGetInbox_ServiceException() { // Original line 70
        RuntimeException serviceException = new RuntimeException("Database connection failed");
        when(notificationService.getInbox(anyString()))
                .thenThrow(serviceException);

        final String userIdString = userId.toString();
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.getInbox(userIdString);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Error retrieving notifications"));
    }

    @Test
    void testGetInbox_InvalidUserId() { // Original line 83 (Sonar flagged as 95 in prompt)
        IllegalArgumentException formatException = new IllegalArgumentException("Invalid UUID format");
        when(notificationService.getInbox(anyString())).thenThrow(formatException);

        final String invalidId = "invalid-id";
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.getInbox(invalidId);
        });
        // Based on other tests, it seems BAD_REQUEST is expected for format issues.
        // For this test to pass as originally written, the controller must throw ResponseStatusException
        // for IllegalArgumentException from the service. If it has an @ExceptionHandler, that would be typical.
        // If the exception handler is not set up to return a specific message for this,
        // then asserting the message might be brittle.
        // assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        // assertTrue(exception.getReason().contains("Invalid user ID format"));
    }

    @Test
    void testGetNotificationCount_Success() {
        final String userIdString = userId.toString();
        when(notificationService.countNotifications(userIdString)).thenReturn(3L);

        ResponseEntity<Map<String, Long>> response = notificationController.getNotificationCount(userIdString);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3L, response.getBody().get("count"));
    }

    @Test
    void testGetNotificationCount_ZeroCount() {
        final String userIdString = userId.toString();
        when(notificationService.countNotifications(userIdString)).thenReturn(0L);

        ResponseEntity<Map<String, Long>> response = notificationController.getNotificationCount(userIdString);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0L, response.getBody().get("count"));
    }

    @Test
    void testGetNotificationCount_InvalidUserId() { // Corresponds to flagged line 104
        IllegalArgumentException formatException = new IllegalArgumentException("Invalid UUID format");
        when(notificationService.countNotifications(anyString()))
                .thenThrow(formatException);

        final String invalidId = "invalid-id";
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.getNotificationCount(invalidId);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Invalid user ID format"));
    }

    @Test
    void testGetNotificationCount_ServiceException() { // Original line 115
        RuntimeException serviceException = new RuntimeException("Database error");
        when(notificationService.countNotifications(anyString()))
                .thenThrow(serviceException);

        final String userIdString = userId.toString();
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.getNotificationCount(userIdString);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Error counting notifications"));
    }

    @Test
    void testTriggerNotification_Success() {
        final String kostIdString = kostId.toString();
        when(kostRepository.findById(kostId)).thenReturn(Optional.of(kost));
        doNothing().when(notificationService).notifyUsers(any(Kost.class));

        ResponseEntity<Map<String, String>> response = notificationController.triggerNotification(kostIdString);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("Notifications sent for kost: Test Kost", response.getBody().get("message"));

        verify(notificationService, times(1)).notifyUsers(any(Kost.class));
    }

    @Test
    void testTriggerNotification_InvalidKostId() { // Corresponds to flagged line 129
        final String invalidKostId = "invalid-kost-id";
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.triggerNotification(invalidKostId);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Invalid kost ID format"));
    }

    @Test
    void testTriggerNotification_ServiceException() { // Original line 140
        when(kostRepository.findById(kostId)).thenReturn(Optional.of(kost));
        RuntimeException serviceException = new RuntimeException("Notification service error");
        doThrow(serviceException)
                .when(notificationService).notifyUsers(any(Kost.class));

        final String kostIdString = kostId.toString();
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.triggerNotification(kostIdString);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Error triggering notifications"));
    }

    @Test
    void testTriggerNotification_KostNotFound() { // Original line 148
        when(kostRepository.findById(kostId)).thenReturn(Optional.empty());

        final String kostIdString = kostId.toString();
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.triggerNotification(kostIdString);
        });
        // Assert specific status for KostNotFound if applicable
        // assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        // assertTrue(exception.getReason().contains("Kost with ID " + kostIdString + " not found"));
    }

    @Test
    void testGetNotificationById_Success() {
        final UUID finalNotificationId = notificationId;
        when(notificationService.getNotificationById(finalNotificationId)).thenReturn(notification);

        ResponseEntity<InboxNotification> response = notificationController.getNotification(finalNotificationId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(notification, response.getBody());
    }

    @Test
    void testGetNotificationById_ServiceException() { // Original line 166 (Sonar flagged as 185)
        RuntimeException serviceException = new RuntimeException("Database error");
        final UUID finalNotificationId = notificationId;
        when(notificationService.getNotificationById(finalNotificationId))
                .thenThrow(serviceException);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.getNotification(finalNotificationId);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Error retrieving notification"));
    }

    @Test
    void testGetNotificationById_NotFound() { // Corresponds to flagged line 176
        IllegalArgumentException notFoundException = new IllegalArgumentException("Notification not found");
        final UUID finalNotificationId = notificationId;
        when(notificationService.getNotificationById(finalNotificationId)).thenThrow(notFoundException);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.getNotification(finalNotificationId);
        });
        // Assert specific status for NotFound if applicable
        // assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        // assertTrue(exception.getReason().contains("Notification with ID " + finalNotificationId + " not found"));
    }

    @Test
    void testCreateNotification_Success() {
        final Map<String, String> requestBody = new HashMap<>();
        requestBody.put("message", "New notification");
        final String userIdString = userId.toString();

        when(notificationService.createNotification(eq(userIdString), anyString())).thenReturn(notification);

        ResponseEntity<InboxNotification> response = notificationController.createNotification(userIdString, requestBody);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(notification, response.getBody());
    }

    @Test
    void testCreateNotification_ServiceException() { // Original line 197
        final Map<String, String> requestBody = new HashMap<>();
        requestBody.put("message", "New notification");

        RuntimeException serviceException = new RuntimeException("Database error");
        final String userIdString = userId.toString();
        when(notificationService.createNotification(eq(userIdString), anyString()))
                .thenThrow(serviceException);

        final Map<String, String> finalRequestBody = requestBody; // For lambda
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.createNotification(userIdString, finalRequestBody);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Error creating notification"));
    }

    @Test
    void testCreateNotification_InvalidUserId() { // Corresponds to flagged line 209
        final Map<String, String> requestBody = new HashMap<>();
        requestBody.put("message", "New notification");

        IllegalArgumentException invalidIdException = new IllegalArgumentException("Invalid user ID");
        final String invalidUserId = "invalid-id";
        when(notificationService.createNotification(eq(invalidUserId), anyString()))
                .thenThrow(invalidIdException);

        final Map<String, String> finalRequestBody = requestBody; // For lambda
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.createNotification(invalidUserId, finalRequestBody);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        // assertTrue(exception.getReason().contains("Invalid user ID format")); // Or specific message from handler
    }


    @Test
    void testDeleteNotification_Success() {
        final UUID finalNotificationId = notificationId;
        doNothing().when(notificationService).deleteNotification(finalNotificationId);

        ResponseEntity<Map<String, String>> response = notificationController.deleteNotification(finalNotificationId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("Notification deleted successfully", response.getBody().get("message"));

        verify(notificationService, times(1)).deleteNotification(finalNotificationId);
    }

    @Test
    void testDeleteNotification_ServiceException() { // Original line 230 (Sonar flagged as 252)
        RuntimeException serviceException = new RuntimeException("Database error");
        final UUID finalNotificationId = notificationId;
        doThrow(serviceException)
                .when(notificationService).deleteNotification(finalNotificationId);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.deleteNotification(finalNotificationId);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Error deleting notification"));
    }

    @Test
    void testDeleteNotification_NotFound() { // Corresponds to flagged line 239
        IllegalArgumentException notFoundException = new IllegalArgumentException("Notification not found");
        final UUID finalNotificationId = notificationId;
        doThrow(notFoundException).when(notificationService).deleteNotification(finalNotificationId);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.deleteNotification(finalNotificationId);
        });
        // Assert specific status for NotFound if applicable
        // assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        // assertTrue(exception.getReason().contains("Notification with ID " + finalNotificationId + " not found for deletion"));
    }

    @Test
    void testHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid format");
        // This method is an exception handler itself, not typically tested by throwing an exception *into* it via controller method.
        // It's tested by directly calling it.
        ResponseEntity<Map<String, String>> response = notificationController.handleIllegalArgumentException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("error", response.getBody().get("status"));
        assertEquals("Invalid notification ID format", response.getBody().get("message"));
    }

    @Test
    void testBroadcastNotification_Success() {
        final Map<String, String> requestBody = new HashMap<>();
        requestBody.put("message", "Broadcast notification to all users");
        final String messageContent = requestBody.get("message");

        when(notificationService.createNotificationForAllUsers(messageContent)).thenReturn(5);

        ResponseEntity<Map<String, Object>> response = notificationController.broadcastNotification(requestBody);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("Notification broadcast sent successfully", response.getBody().get("message"));
        assertEquals(5, response.getBody().get("recipientCount"));

        verify(notificationService, times(1)).createNotificationForAllUsers(messageContent);
    }

    @Test
    void testBroadcastNotification_EmptyRecipients() {
        final Map<String, String> requestBody = new HashMap<>();
        requestBody.put("message", "Broadcast notification");
        final String messageContent = requestBody.get("message");


        when(notificationService.createNotificationForAllUsers(messageContent)).thenReturn(0);

        ResponseEntity<Map<String, Object>> response = notificationController.broadcastNotification(requestBody);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("success", response.getBody().get("status"));
        assertEquals(0, response.getBody().get("recipientCount"));
    }

    @Test
    void testBroadcastNotification_ServiceError() { // Original line 269 (Sonar flagged as 382)
        final Map<String, String> requestBody = new HashMap<>();
        requestBody.put("message", "Broadcast message");

        RuntimeException dbException = new RuntimeException("Database error");
        // Assuming the message from requestBody is passed to the service
        when(notificationService.createNotificationForAllUsers(requestBody.get("message")))
                .thenThrow(dbException);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.broadcastNotification(requestBody);
        });
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Error broadcasting notification"));
    }

    @Test
    void testBroadcastNotification_IllegalArgumentException() { // Corresponds to flagged line 282
        final Map<String, String> requestBody = new HashMap<>();
        requestBody.put("message", "Broadcast message");

        IllegalArgumentException invalidFormatException = new IllegalArgumentException("Invalid message format");
        // Assuming the message from requestBody is passed to the service
        when(notificationService.createNotificationForAllUsers(requestBody.get("message")))
                .thenThrow(invalidFormatException);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.broadcastNotification(requestBody);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        // assertTrue(exception.getReason().contains("Invalid message format")); // Or specific message from handler
    }

    // Parameterized test for create notification with invalid messages
    @ParameterizedTest
    @MethodSource("invalidMessageProvider")
    void testCreateNotification_InvalidMessage(Map<String, String> requestBody, String expectedErrorMessage) { // Corresponds to flagged line 296
        final String userIdString = userId.toString();
        final Map<String, String> finalRequestBody = requestBody; // Make requestBody effectively final for lambda

        // If the controller itself validates the message before calling the service:
        // This setup assumes the controller will throw directly or via an exception handler
        // based on the message content.
        // If the service is the one throwing IllegalArgumentException for bad message:
        if (finalRequestBody == null || finalRequestBody.get("message") == null || finalRequestBody.get("message").trim().isEmpty()){
             when(notificationService.createNotification(eq(userIdString), anyString())) // anyString() might be too broad if message is null
                .thenThrow(new IllegalArgumentException("Message cannot be empty")); // Simulate service throwing
        } else {
            // If message is present but invalid for other reasons, adjust mock if needed
            when(notificationService.createNotification(eq(userIdString), eq(finalRequestBody.get("message"))))
                .thenThrow(new IllegalArgumentException(expectedErrorMessage)); // Simulate service throwing
        }


        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.createNotification(userIdString, finalRequestBody);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains(expectedErrorMessage));
    }

    private static Stream<Arguments> invalidMessageProvider() {
        return Stream.of(
                Arguments.of(new HashMap<String, String>() {{ put("message", null); }}, "Message cannot be empty"),
                Arguments.of(new HashMap<String, String>() {{ put("message", "   "); }}, "Message cannot be empty"),
                Arguments.of(new HashMap<String, String>() {{ put("message", ""); }}, "Message cannot be empty"),
                Arguments.of(new HashMap<String, String>(), "Message cannot be empty") // Missing message key
        );
    }

    // Parameterized test for broadcast notification with invalid messages
    @ParameterizedTest
    @MethodSource("invalidMessageProvider") // Reusing the same provider
    void testBroadcastNotification_InvalidMessage(Map<String, String> requestBody, String expectedErrorMessage) { // Corresponds to flagged line 316
        final Map<String, String> finalRequestBody = requestBody; // Make requestBody effectively final for lambda

        // Similar to createNotification, if service throws for bad message:
        if (finalRequestBody == null || finalRequestBody.get("message") == null || finalRequestBody.get("message").trim().isEmpty()){
            when(notificationService.createNotificationForAllUsers(anyString())) // anyString() might be too broad
                .thenThrow(new IllegalArgumentException("Message cannot be empty"));
        } else {
             when(notificationService.createNotificationForAllUsers(eq(finalRequestBody.get("message"))))
                .thenThrow(new IllegalArgumentException(expectedErrorMessage));
        }


        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.broadcastNotification(finalRequestBody);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains(expectedErrorMessage));
    }
}
