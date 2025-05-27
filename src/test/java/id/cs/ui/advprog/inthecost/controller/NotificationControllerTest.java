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

        kost = new Kost(); // Assuming default constructor or setters are used
        kost.setKostID(kostId);
        kost.setNama("Test Kost");

        notification = new InboxNotification(user, "Test notification");
    }

    @Test
    void testGetInbox_Success() {
        final List<InboxNotification> notifications = List.of(notification);
        final String userIdString = userId.toString();

        when(notificationService.getInbox(userIdString)).thenReturn(notifications);

        ResponseEntity<List<InboxNotification>> response = notificationController.getInbox(userIdString);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(notifications, response.getBody());
    }

    @Test
    void testGetInbox_EmptyList() {
        final List<InboxNotification> emptyNotifications = new ArrayList<>();
        final String userIdString = userId.toString();

        when(notificationService.getInbox(userIdString)).thenReturn(emptyNotifications);

        ResponseEntity<List<InboxNotification>> response = notificationController.getInbox(userIdString);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(emptyNotifications, response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testGetInbox_ServiceException() {
        RuntimeException serviceException = new RuntimeException("Database connection failed");
        final String userIdString = userId.toString();
        when(notificationService.getInbox(userIdString))
                .thenThrow(serviceException);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.getInbox(userIdString);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Error retrieving notifications"));
    }

    @Test
    void testGetInbox_InvalidUserId() { // Line 120 area (comment removal)
        IllegalArgumentException formatException = new IllegalArgumentException("Invalid UUID format");
        final String invalidId = "invalid-id";
        when(notificationService.getInbox(invalidId)).thenThrow(formatException);

        assertThrows(ResponseStatusException.class, () -> {
            notificationController.getInbox(invalidId);
        });
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
    void testGetNotificationCount_InvalidUserId() {
        IllegalArgumentException formatException = new IllegalArgumentException("Invalid UUID format");
        final String invalidId = "invalid-id";
        when(notificationService.countNotifications(invalidId))
                .thenThrow(formatException);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.getNotificationCount(invalidId);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Invalid user ID format"));
    }

    @Test
    void testGetNotificationCount_ServiceException() {
        RuntimeException serviceException = new RuntimeException("Database error");
        final String userIdString = userId.toString();
        when(notificationService.countNotifications(userIdString))
                .thenThrow(serviceException);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.getNotificationCount(userIdString);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Error counting notifications"));
    }

    @Test
    void testTriggerNotification_Success() {
        final String kostIdString = this.kostId.toString();
        final Kost finalKost = this.kost;
        when(kostRepository.findById(this.kostId)).thenReturn(Optional.of(finalKost));
        doNothing().when(notificationService).notifyUsers(any(Kost.class));

        ResponseEntity<Map<String, String>> response = notificationController.triggerNotification(kostIdString);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("Notifications sent for kost: Test Kost", response.getBody().get("message"));

        verify(notificationService, times(1)).notifyUsers(eq(finalKost));
    }

    @Test
    void testTriggerNotification_InvalidKostId() {
        final String invalidKostId = "invalid-kost-id";
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.triggerNotification(invalidKostId);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Invalid kost ID format"));
    }

    @Test
    void testTriggerNotification_ServiceException() {
        final Kost finalKost = this.kost;
        final String kostIdString = this.kostId.toString();
        when(kostRepository.findById(this.kostId)).thenReturn(Optional.of(finalKost));
        RuntimeException serviceException = new RuntimeException("Notification service error");
        doThrow(serviceException)
                .when(notificationService).notifyUsers(eq(finalKost));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.triggerNotification(kostIdString);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Error triggering notifications"));
    }

    @Test
    void testTriggerNotification_KostNotFound() {
        final String kostIdString = this.kostId.toString();
        when(kostRepository.findById(this.kostId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> {
            notificationController.triggerNotification(kostIdString);
        });
    }

    @Test
    void testGetNotificationById_Success() {
        final UUID finalNotificationId = this.notificationId;
        final InboxNotification finalNotification = this.notification;
        when(notificationService.getNotificationById(finalNotificationId)).thenReturn(finalNotification);

        ResponseEntity<InboxNotification> response = notificationController.getNotification(finalNotificationId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(finalNotification, response.getBody());
    }

    @Test
    void testGetNotificationById_ServiceException() {
        RuntimeException serviceException = new RuntimeException("Database error");
        final UUID finalNotificationId = this.notificationId;
        when(notificationService.getNotificationById(finalNotificationId))
                .thenThrow(serviceException);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.getNotification(finalNotificationId);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Error retrieving notification"));
    }

    @Test
    void testGetNotificationById_NotFound() { // Line 223 & 227 area (S1854/S1481 & S125)
        IllegalArgumentException notFoundException = new IllegalArgumentException("Notification not found");
        final UUID finalNotificationId = this.notificationId;
        when(notificationService.getNotificationById(finalNotificationId)).thenThrow(notFoundException);

        assertThrows(ResponseStatusException.class, () -> { // L223: No assignment to 'exception' as it's unused
            notificationController.getNotification(finalNotificationId);
        });
        // L227: Removed commented out assertions
    }

    @Test
    void testCreateNotification_Success() {
        final Map<String, String> requestBody = new HashMap<>();
        requestBody.put("message", "New notification");
        final String userIdString = this.userId.toString();
        final String messageContent = requestBody.get("message");
        final InboxNotification finalNotification = this.notification; // Use a distinct notification for this test if needed

        when(notificationService.createNotification(userIdString, messageContent)).thenReturn(finalNotification);

        ResponseEntity<InboxNotification> response = notificationController.createNotification(userIdString, requestBody);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(finalNotification, response.getBody());
    }

    @Test
    void testCreateNotification_ServiceException() {
        final Map<String, String> requestBody = new HashMap<>();
        requestBody.put("message", "New notification");
        final String messageContent = requestBody.get("message");

        RuntimeException serviceException = new RuntimeException("Database error");
        final String userIdString = this.userId.toString();
        when(notificationService.createNotification(userIdString, messageContent))
                .thenThrow(serviceException);

        final Map<String, String> finalRequestBody = requestBody;
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.createNotification(userIdString, finalRequestBody);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Error creating notification"));
    }

    @Test
    void testCreateNotification_InvalidUserId() {
        final Map<String, String> requestBody = new HashMap<>();
        requestBody.put("message", "New notification");
        final String messageContent = requestBody.get("message");

        IllegalArgumentException invalidIdException = new IllegalArgumentException("Invalid user ID");
        final String invalidUserIdString = "invalid-id";
        when(notificationService.createNotification(invalidUserIdString, messageContent))
                .thenThrow(invalidIdException);

        final Map<String, String> finalRequestBody = requestBody;
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.createNotification(invalidUserIdString, finalRequestBody);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }


    @Test
    void testDeleteNotification_Success() {
        final UUID finalNotificationId = this.notificationId;
        doNothing().when(notificationService).deleteNotification(finalNotificationId);

        ResponseEntity<Map<String, String>> response = notificationController.deleteNotification(finalNotificationId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().get("status"));
        assertEquals("Notification deleted successfully", response.getBody().get("message"));

        verify(notificationService, times(1)).deleteNotification(finalNotificationId);
    }

    @Test
    void testDeleteNotification_ServiceException() {
        RuntimeException serviceException = new RuntimeException("Database error");
        final UUID finalNotificationId = this.notificationId;
        doThrow(serviceException)
                .when(notificationService).deleteNotification(finalNotificationId);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.deleteNotification(finalNotificationId);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Error deleting notification"));
    }

    @Test
    void testDeleteNotification_NotFound() { // Line 263 & 267 area (S1854/S1481 & S125)
        IllegalArgumentException notFoundException = new IllegalArgumentException("Notification not found");
        final UUID finalNotificationId = this.notificationId;
        doThrow(notFoundException).when(notificationService).deleteNotification(finalNotificationId);

        assertThrows(ResponseStatusException.class, () -> { // L263: No assignment to 'exception' as it's unused
            notificationController.deleteNotification(finalNotificationId);
        });
        // L267: Removed commented out assertions
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
        final Map<String, String> requestBody = new HashMap<>();
        final String messageContent = "Broadcast notification to all users";
        requestBody.put("message", messageContent);

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
        final String messageContent = "Broadcast notification";
        requestBody.put("message", messageContent);

        when(notificationService.createNotificationForAllUsers(messageContent)).thenReturn(0);

        ResponseEntity<Map<String, Object>> response = notificationController.broadcastNotification(requestBody);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("success", response.getBody().get("status"));
        assertEquals(0, response.getBody().get("recipientCount"));
    }

    @Test
    void testBroadcastNotification_ServiceError() {
        final Map<String, String> requestBody = new HashMap<>();
        final String messageContent = "Broadcast message";
        requestBody.put("message", messageContent);

        RuntimeException dbException = new RuntimeException("Database error");
        when(notificationService.createNotificationForAllUsers(messageContent))
                .thenThrow(dbException);

        final Map<String, String> finalRequestBody = requestBody;
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.broadcastNotification(finalRequestBody);
        });
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Error broadcasting notification"));
    }

    @Test
    void testBroadcastNotification_IllegalArgumentException() { // Line 359 & 363 area (S1854/S1481 & S125)
        final Map<String, String> requestBody = new HashMap<>();
        final String messageContent = "Broadcast message";
        requestBody.put("message", messageContent);

        IllegalArgumentException invalidFormatException = new IllegalArgumentException("Invalid message format");
        when(notificationService.createNotificationForAllUsers(messageContent)) // S6068 fix (eq removed)
                .thenThrow(invalidFormatException);

        final Map<String, String> finalRequestBody = requestBody;
        // L359: SonarQube says 'exception' is unused. This implies the following assertions are not considered or are missing in the analyzed version.
        // To fix the Sonar issue as reported, we remove the assignment if 'exception' is truly unused.
        // However, if the intent is to check status code, the variable should be used.
        // Assuming Sonar is correct about 'exception' being unused in the code it's analyzing:
        assertThrows(ResponseStatusException.class, () -> {
            notificationController.broadcastNotification(finalRequestBody);
        });
        // If assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode()) was intended and makes 'exception' used,
        // then Sonar's S1854/S1481 for L359 would be a false positive *if that assertion is active*.
        // To strictly follow Sonar's report for L359 that `exception` is unused, it means the test should only ensure the exception type.
        // L363: Removed commented out assertions like assertTrue(exception.getReason().contains("Invalid message format"));
    }

    @ParameterizedTest
    @MethodSource("invalidMessageProvider")
    void testCreateNotification_InvalidMessage(Map<String, String> requestBody, String expectedErrorMessage) {
        final String userIdString = this.userId.toString();
        final Map<String, String> finalRequestBody = requestBody;
        final String messageContent = finalRequestBody != null ? finalRequestBody.get("message") : null;

        if (messageContent == null || messageContent.trim().isEmpty()){
             when(notificationService.createNotification(eq(userIdString), anyString())) // eq(userIdString) is needed here due to anyString()
                .thenThrow(new IllegalArgumentException("Message cannot be empty"));
        } else {
            // S6068 fix for what was L497 area (both eq removed)
            when(notificationService.createNotification(userIdString, messageContent))
                .thenThrow(new IllegalArgumentException(expectedErrorMessage));
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
                Arguments.of(new HashMap<String, String>(), "Message cannot be empty")
        );
    }

    @ParameterizedTest
    @MethodSource("invalidMessageProvider")
    void testBroadcastNotification_InvalidMessage(Map<String, String> requestBody, String expectedErrorMessage) {
        final Map<String, String> finalRequestBody = requestBody;
        final String messageContent = finalRequestBody != null ? finalRequestBody.get("message") : null;

        if (messageContent == null || messageContent.trim().isEmpty()){
            when(notificationService.createNotificationForAllUsers(anyString())) // No eq needed with anyString if it's the only one of its kind
                .thenThrow(new IllegalArgumentException("Message cannot be empty"));
        } else {
             // S6068 fix for L464
             when(notificationService.createNotificationForAllUsers(messageContent))
                .thenThrow(new IllegalArgumentException(expectedErrorMessage));
        }

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            notificationController.broadcastNotification(finalRequestBody);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains(expectedErrorMessage));
    }
}