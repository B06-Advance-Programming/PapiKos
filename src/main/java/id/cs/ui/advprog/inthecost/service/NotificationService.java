package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.model.InboxNotification;
import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.repository.InboxRepository;
import id.cs.ui.advprog.inthecost.repository.UserRepository;
import id.cs.ui.advprog.inthecost.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final InboxRepository inboxRepository;
    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;

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

    public List<InboxNotification> getInbox(String userId) {
        // Convert userId to UUID
        UUID userUUID = UUID.fromString(userId);
        return inboxRepository.findByUserId(userUUID);
    }
}