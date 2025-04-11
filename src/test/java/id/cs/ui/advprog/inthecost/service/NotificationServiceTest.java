package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.model.Kos;
import id.cs.ui.advprog.inthecost.model.InboxNotification;
import id.cs.ui.advprog.inthecost.repository.InboxRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;

public class NotificationServiceTest {

    private InboxRepository inboxRepositoryMock;
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        inboxRepositoryMock = mock(InboxRepository.class);
        notificationService = new NotificationService(inboxRepositoryMock);
    }

    @Test
    void testNotifyUsersCreatesInboxForEachUser() {
        Kos kos = mock(Kos.class);
        when(kos.getKosName()).thenReturn("Kos Lavender");

        // Simulasi user yang wishlist kos ini
        Set<String> userIds = Set.of("user1", "user2", "user3");
        when(kos.getWishlistedBy()).thenReturn(userIds);

        notificationService.notifyUsers(kos);

        // Verifikasi bahwa notifikasi dibuat untuk setiap user
        for (String userId : userIds) {
            verify(inboxRepositoryMock, times(1)).save(
                    argThat(notification ->
                            notification.getUserId().equals(userId) &&
                                    notification.getMessage().contains("Kos Lavender")
                    )
            );
        }
    }

    @Test
    void testNotifySkipsIfKosHasNoWishlist() {
        Kos kos = mock(Kos.class);
        when(kos.getKosName()).thenReturn("Kos Tak Diminati");
        when(kos.getWishlistedBy()).thenReturn(Set.of());

        notificationService.notifyUsers(kos);

        verifyNoInteractions(inboxRepositoryMock);
    }

    @Test
    void testGetInboxReturnsUserNotifications() {
        String userId = "user1";
        List<InboxNotification> mockList = List.of(
                new InboxNotification(userId, "Kamar tersedia di Kos Sakura")
        );

        when(inboxRepositoryMock.findByUserId(userId)).thenReturn(mockList);

        List<InboxNotification> result = notificationService.getInbox(userId);

        assert result.size() == 1;
        assert result.get(0).getMessage().contains("Sakura");
    }
}
