package id.cs.ui.advprog.inthecost.service;

import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.model.InboxNotification;
import id.cs.ui.advprog.inthecost.model.User;
import id.cs.ui.advprog.inthecost.repository.InboxRepository;
import id.cs.ui.advprog.inthecost.repository.UserRepository;
import id.cs.ui.advprog.inthecost.repository.WishlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private InboxRepository inboxRepositoryMock;
    
    @Mock
    private WishlistRepository wishlistRepositoryMock;
    
    @Mock
    private UserRepository userRepositoryMock;
    
    @InjectMocks
    private NotificationServiceImpl notificationService;

    @BeforeEach
    void setUp() {
        // The @Mock and @InjectMocks annotations handle the mocking and injection
    }    @Test
    void testNotifyUsersCreatesInboxForEachUser() {
        Kost kost = new Kost("Kos Lavender", "Jl. Lavender", "Deskripsi Kos Lavender", 1, 1000000);

        // Simulate users who have wishlisted this Kost with valid UUIDs
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        UUID userId3 = UUID.randomUUID();
        Set<String> userIds = Set.of(userId1.toString(), userId2.toString(), userId3.toString());

        when(wishlistRepositoryMock.findUserIdsByKostId(kost.getKostID())).thenReturn(userIds);

        for (String userId : userIds) {
            UUID userUUID = UUID.fromString(userId);
            User user = new User();
            user.setId(userUUID);
            when(userRepositoryMock.findById(userUUID)).thenReturn(java.util.Optional.of(user));
        }

        notificationService.notifyUsers(kost);

        // Verify that notifications are created for each user
        for (String userId : userIds) {
            UUID userUUID = UUID.fromString(userId);
            verify(inboxRepositoryMock, times(1)).save(
                    argThat(notification ->
                            notification.getUser().getId().equals(userUUID) &&
                                    notification.getMessage().contains("Kos Lavender") &&
                                    notification.getMessage().contains("kamar tersedia")
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
        UUID userUUID = UUID.randomUUID();
        user.setId(userUUID);
        user.setUsername("user1");

        List<InboxNotification> mockList = List.of(
                new InboxNotification(user, "Kamar tersedia di Kos Sakura")
        );

        when(inboxRepositoryMock.findByUserId(userUUID)).thenReturn(mockList);

        List<InboxNotification> result = notificationService.getInbox(userUUID.toString());

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