package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.model.InboxNotification;
import id.cs.ui.advprog.inthecost.model.Kost;

import java.util.List;
import java.util.UUID;

/**
 * Interface for notification-related services
 * Handles notifications for users about kost availability and inbox messaging
 */
public interface NotificationService {

    /**
     * Notify users who have a specific kost in their wishlist when it becomes available
     * @param kost The kost that has become available
     */
    void notifyUsers(Kost kost);

    /**
     * Retrieve inbox notifications for a specific user
     * @param userId The ID of the user as a string
     * @return List of inbox notifications for the user
     */
    List<InboxNotification> getInbox(String userId);
    
    /**
     * Count the number of notifications for a specific user
     * @param userId The ID of the user as a string
     * @return The count of notifications for the user
     */
    long countNotifications(String userId);
    
    /**
     * Create a custom notification for a specific user
     * @param userId The ID of the user as a string
     * @param message The notification message
     * @return The created notification
     */
    InboxNotification createNotification(String userId, String message);
      /**
     * Delete a notification by its ID
     * @param notificationId The ID of the notification to delete
     */
    void deleteNotification(UUID notificationId);
    
    /**
     * Get a notification by its ID
     * @param notificationId The ID of the notification
     * @return The notification, if found
     */
    InboxNotification getNotificationById(UUID notificationId);
}