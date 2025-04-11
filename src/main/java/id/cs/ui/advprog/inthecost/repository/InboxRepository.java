package id.cs.ui.advprog.inthecost.repository;

import id.cs.ui.advprog.inthecost.model.InboxNotification;

import java.util.List;

public interface InboxRepository {
    void save(InboxNotification notification);
    List<InboxNotification> findByUserId(String userId);
}