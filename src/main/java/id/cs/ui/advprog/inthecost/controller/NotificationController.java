package id.cs.ui.advprog.inthecost.controller;

import id.cs.ui.advprog.inthecost.model.InboxNotification;
import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.repository.KostRepository;
import id.cs.ui.advprog.inthecost.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller for managing notifications and user inbox
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;
    private final KostRepository kostRepository;

    /**
     * Get all inbox notifications for a specific user
     * 
     * @param userId The ID of the user
     * @return List of inbox notifications
     */
    @GetMapping("/inbox/{userId}")
    public ResponseEntity<List<InboxNotification>> getInbox(@PathVariable String userId) {
        try {
            List<InboxNotification> inbox = notificationService.getInbox(userId);
            return ResponseEntity.ok(inbox);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user ID format: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving notifications: " + e.getMessage(), e);
        }
    }
    
    /**
     * Count notifications for a user
     * 
     * @param userId The ID of the user
     * @return Count of notifications
     */
    @GetMapping("/count/{userId}")
    public ResponseEntity<Map<String, Long>> getNotificationCount(@PathVariable String userId) {
        try {
            long count = notificationService.countNotifications(userId);
            
            Map<String, Long> response = new HashMap<>();
            response.put("count", count);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user ID format: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error counting notifications: " + e.getMessage(), e);
        }
    }
      /**
     * Manually trigger notifications for a kost
     * Useful for testing or administrative purposes
     * 
     * @param kostId The ID of the kost to notify about
     * @return Success message
     */    
    @PostMapping("/trigger/{kostId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Map<String, String>> triggerNotification(@PathVariable String kostId) {
        try {
            UUID kostUUID = UUID.fromString(kostId);
            Kost kost = kostRepository.findById(kostUUID)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kost not found with ID: " + kostId));
            
            notificationService.notifyUsers(kost);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Notifications sent for kost: " + kost.getNama());
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid kost ID format: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error triggering notifications: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get a specific notification by ID
     * 
     * @param notificationId The ID of the notification
     * @return The notification
     */
    @GetMapping("/{notificationId}")
    public ResponseEntity<InboxNotification> getNotification(@PathVariable Long notificationId) {
        try {
            InboxNotification notification = notificationService.getNotificationById(notificationId);
            return ResponseEntity.ok(notification);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving notification: " + e.getMessage(), e);
        }
    }
      /**
     * Create a custom notification for a user
     * 
     * @param userId The ID of the user
     * @param request The request body containing the notification message
     * @return The created notification
     */
    @PostMapping("/create/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<InboxNotification> createNotification(
            @PathVariable String userId, 
            @RequestBody Map<String, String> request) {
        try {
            String message = request.get("message");
            if (message == null || message.trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message cannot be empty");
            }
            
            InboxNotification notification = notificationService.createNotification(userId, message);
            return ResponseEntity.status(HttpStatus.CREATED).body(notification);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating notification: " + e.getMessage(), e);
        }
    }
      /**
     * Delete a notification by ID
     * 
     * @param notificationId The ID of the notification to delete
     * @return Success message
     */
    @DeleteMapping("/{notificationId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteNotification(@PathVariable Long notificationId) {
        try {
            notificationService.deleteNotification(notificationId);
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Notification deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting notification: " + e.getMessage(), e);
        }
    }
    
    /**
     * Handle invalid notification ID format
     */
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<Map<String, String>> handleNumberFormatException(NumberFormatException e) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", "Invalid notification ID format");
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}