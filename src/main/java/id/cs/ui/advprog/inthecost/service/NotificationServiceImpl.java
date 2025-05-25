package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.model.InboxNotification;
import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.repository.InboxRepository;
import id.cs.ui.advprog.inthecost.repository.UserRepository;
import id.cs.ui.advprog.inthecost.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.List;
import java.util.UUID;

/**
 * Implementation of the NotificationService interface
 * Handles notifications for users about kost availability and inbox messaging
 */
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final InboxRepository inboxRepository;
    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void notifyUsers(Kost kost) {
        // Retrieve the list of users who have wishlisted this Kost
        Set<String> wishlistedUsers = wishlistRepository.findUserIdsByKostId(kost.getKostID());

        if (wishlistedUsers == null || wishlistedUsers.isEmpty()) {
            return;
        }

        for (String userId : wishlistedUsers) {
            // Convert userId to UUID
            UUID userUUID = UUID.fromString(userId);

            // Check if a similar notification already exists to avoid duplicates
            String message = "Kamar tersedia di " + kost.getNama();
            boolean exists = inboxRepository.existsByUserIdAndMessage(userUUID, message);

            if (!exists) {
                // Fetch the User object using the userId
                User user = userRepository.findById(userUUID)
                        .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

                // Create the InboxNotification with the User object
                InboxNotification notif = new InboxNotification(user, message);
                inboxRepository.save(notif);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<InboxNotification> getInbox(String userId) {
        // Convert userId to UUID
        UUID userUUID = UUID.fromString(userId);
        return inboxRepository.findByUserId(userUUID);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public long countNotifications(String userId) {
        UUID userUUID = UUID.fromString(userId);
        return inboxRepository.findByUserId(userUUID).size();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public InboxNotification createNotification(String userId, String message) {
        if (userId == null || message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID and message cannot be null or empty");
        }
        
        UUID userUUID = UUID.fromString(userId);
        User user = userRepository.findById(userUUID)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        
        InboxNotification notification = new InboxNotification(user, message);
        return inboxRepository.save(notification);
    }
      /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteNotification(UUID notificationId) {
        if (notificationId == null) {
            throw new IllegalArgumentException("Notification ID cannot be null");
        }
        
        if (!inboxRepository.existsById(notificationId)) {
            throw new IllegalArgumentException("Notification not found with ID: " + notificationId);
        }
        
        inboxRepository.deleteById(notificationId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public InboxNotification getNotificationById(UUID notificationId) {
        if (notificationId == null) {
            throw new IllegalArgumentException("Notification ID cannot be null");
        }
        
        return inboxRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found with ID: " + notificationId));
    }
}
