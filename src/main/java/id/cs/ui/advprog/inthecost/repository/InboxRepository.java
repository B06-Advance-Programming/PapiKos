package id.cs.ui.advprog.inthecost.repository;

import id.cs.ui.advprog.inthecost.model.InboxNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InboxRepository extends JpaRepository<InboxNotification, Long> {
    // Find all notifications for a specific user
    List<InboxNotification> findByUserId(UUID userId);

    // Check if a notification with the same userId and message already exists
    boolean existsByUserIdAndMessage(UUID userId, String message);
}