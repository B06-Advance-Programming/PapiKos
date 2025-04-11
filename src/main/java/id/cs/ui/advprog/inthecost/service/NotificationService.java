package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.model.InboxNotification;
import id.cs.ui.advprog.inthecost.model.Kos;
import id.cs.ui.advprog.inthecost.repository.InboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final InboxRepository inboxRepository;

    public void notifyUsers(Kos kos) {
        Set<String> wishlistedUsers = kos.getWishlistedBy(); // ← Asumsikan Kos punya ini

        if (wishlistedUsers == null || wishlistedUsers.isEmpty()) {
            return;
        }

        for (String userId : wishlistedUsers) {
            String message = "Kamar tersedia di " + kos.getKosName();
            InboxNotification notif = new InboxNotification(userId, message);
            inboxRepository.save(notif);
        }
    }

    public List<InboxNotification> getInbox(String userId) {
        return inboxRepository.findByUserId(userId);
    }
}