package id.cs.ui.advprog.inthecost.observer;

import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class WishlistObserverTest {

    private NotificationService notificationServiceMock;
    private WishlistObserver wishlistObserver;

    @BeforeEach
    void setUp() {
        notificationServiceMock = mock(NotificationService.class);
        wishlistObserver = new WishlistObserver(notificationServiceMock);
    }

    @Test
    void testNotifyUsersCalledWhenKostHasAvailableRooms() {
        Kost kost = new Kost("Kos Sakura", "Jl. Sakura", "Deskripsi Kos Sakura", 1, 1000000); // ada kamar
        wishlistObserver.update(kost);

        verify(notificationServiceMock, times(1)).notifyUsers(kost);
    }

    @Test
    void testNotifyUsersNotCalledWhenKostHasNoAvailableRooms() {
        Kost kost = new Kost("Kos Melati", "Jl. Melati", "Deskripsi Kos Melati", 0, 1000000); // gak ada kamar
        wishlistObserver.update(kost);

        verify(notificationServiceMock, never()).notifyUsers(any());
    }

    @Test
    void testUpdateDoesNothingIfSubjectIsNotKost() {
        Object notKost = new Object(); // bisa juga mock class lain

        wishlistObserver.update(notKost);

        // Pastikan notification gak dipanggil sama sekali
        verifyNoInteractions(notificationServiceMock);
    }
}