package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.model.InboxNotification;
import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.repository.InboxRepository;
import id.cs.ui.advprog.inthecost.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final InboxRepository inboxRepository;
    private final WishlistRepository wishlistRepository;

    public void notifyUsers(Kost kost) {
        // Retrieve the list of users who have wishlisted this Kost
        Set<String> wishlistedUsers = wishlistRepository.findUserIdsByKostId(kost.getKostID());

        if (wishlistedUsers == null || wishlistedUsers.isEmpty()) {
            return;
        }

        for (String userId : wishlistedUsers) {
            // Check if a similar notification already exists to avoid duplicates
            String message = "Kamar tersedia di " + kost.getNama();
            boolean exists = inboxRepository.existsByUserIdAndMessage(userId, message);

            if (!exists) {
                InboxNotification notif = new InboxNotification(userId, message);
                inboxRepository.save(notif);
            }
        }
    }

    public List<InboxNotification> getInbox(String userId) {
        return inboxRepository.findByUserId(userId);
    }
}