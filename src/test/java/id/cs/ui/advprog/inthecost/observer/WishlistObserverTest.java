package id.cs.ui.advprog.inthecost.observer;

import id.cs.ui.advprog.inthecost.model.Kos;
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
    void testNotifyUsersCalledWhenKosHasAvailableRooms() {
        Kos kos = new Kos("Kos Sakura", 1); // ada kamar
        wishlistObserver.update(kos);

        verify(notificationServiceMock, times(1)).notifyUsers(kos);
    }

    @Test
    void testNotifyUsersNotCalledWhenKosHasNoAvailableRooms() {
        Kos kos = new Kos("Kos Melati", 0); // gak ada kamar
        wishlistObserver.update(kos);

        verify(notificationServiceMock, never()).notifyUsers(any());
    }

    @Test
    void testUpdateDoesNothingIfSubjectIsNotKos() {
        Object notKos = new Object(); // bisa juga mock class lain

        wishlistObserver.update(notKos);

        // Pastikan notification gak dipanggil sama sekali
        verifyNoInteractions(notificationServiceMock);
    }
}