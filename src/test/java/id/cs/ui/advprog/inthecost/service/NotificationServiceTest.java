package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.InboxNotification;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.repository.InboxRepository;
import id.cs.ui.advprog.inthecost.repository.UserRepository;
import id.cs.ui.advprog.inthecost.repository.WishlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class NotificationServiceTest {

    private InboxRepository inboxRepositoryMock;
    private WishlistRepository wishlistRepositoryMock;
    private UserRepository userRepositoryMock;
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        inboxRepositoryMock = mock(InboxRepository.class);
        wishlistRepositoryMock = mock(WishlistRepository.class);
        userRepositoryMock = mock(UserRepository.class);
        notificationService = new NotificationService(inboxRepositoryMock, wishlistRepositoryMock, userRepositoryMock);
    }

    @Test
    void testNotifyUsersCreatesInboxForEachUser() {
        Kost kost = new Kost("Kos Lavender", "Jl. Lavender", "Deskripsi Kos Lavender", 1, 1000000);

        // Simulate users who have wishlisted this Kost
        Set<String> userIds = Set.of("user1", "user2", "user3");
        when(wishlistRepositoryMock.findUserIdsByKostId(kost.getKostID())).thenReturn(userIds);

        for (String userId : userIds) {
            User user = new User();
            user.setId(UUID.fromString(userId));
            when(userRepositoryMock.findById(UUID.fromString(userId))).thenReturn(java.util.Optional.of(user));
        }

        when(inboxRepositoryMock.existsByUserIdAndMessage(anyString(), anyString())).thenReturn(false);

        notificationService.notifyUsers(kost);

        // Verify that notifications are created for each user
        for (String userId : userIds) {
            verify(inboxRepositoryMock, times(1)).save(
                    argThat(notification ->
                            notification.getUser().getId().toString().equals(userId) &&
                                    notification.getMessage().contains("Kos Lavender")
                    )
            );
        }
    }

    @Test
    void testNotifySkipsIfKostHasNoWishlist() {
        Kost kost = new Kost("Kos Tak Diminati", "Jl. Tak Diminati", "Deskripsi Kos Tak Diminati", 1, 1000000);

        // Simulate no users wishlisting this Kost
        when(wishlistRepositoryMock.findUserIdsByKostId(kost.getKostID())).thenReturn(Set.of());

        notificationService.notifyUsers(kost);

        // Verify no interactions with the inbox repository
        verifyNoInteractions(inboxRepositoryMock);
    }

    @Test
    void testGetInboxReturnsUserNotifications() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("user1");

        List<InboxNotification> mockList = List.of(
                new InboxNotification(user, "Kamar tersedia di Kos Sakura")
        );

        when(inboxRepositoryMock.findByUserId(user.getId().toString())).thenReturn(mockList);

        List<InboxNotification> result = notificationService.getInbox(user.getId().toString());

        assertEquals(1, result.size());
        assertEquals("Kamar tersedia di Kos Sakura", result.get(0).getMessage());
    }

    @Test
    void testNotifyUsersHandlesNullWishlist() {
        Kost kost = new Kost("Kos Null Wishlist", "Jl. Null", "Deskripsi Kos Null Wishlist", 1, 1000000);

        // Simulate null wishlist
        when(wishlistRepositoryMock.findUserIdsByKostId(kost.getKostID())).thenReturn(null);

        notificationService.notifyUsers(kost);

        // Verify no interactions with the inbox repository
        verifyNoInteractions(inboxRepositoryMock);
    }
}