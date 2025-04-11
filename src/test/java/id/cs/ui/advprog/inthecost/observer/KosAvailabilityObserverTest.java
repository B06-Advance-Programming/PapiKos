package id.cs.ui.advprog.inthecost.observer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

public class KosAvailabilityObserverTest {

    private Kos kos;
    private WishlistObserver observer;
    private NotificationService notificationService;

    @BeforeEach
    void setup() {
        notificationService = mock(NotificationService.class);
        observer = new WishlistObserver(notificationService);

        kos = new Kos("Kos Asri", 2); // 2 kamar awal
        kos.addObserver(observer);
    }

    @Test
    void testNotificationSentWhenRoomAvailable() {
        // Kos penuh dulu
        kos.setAvailableRooms(0);

        // Lalu ada kamar kosong 1
        kos.setAvailableRooms(1);

        // Notifikasi seharusnya dikirim
        verify(notificationService, times(1)).notifyUsers(kos);
    }

    @Test
    void testNoNotificationWhenRoomStillUnavailable() {
        kos.setAvailableRooms(0);
        kos.setAvailableRooms(0);

        verify(notificationService, never()).notifyUsers(kos);
    }

    @Test
    void testNoNotificationIfNotOnWishlist() {
        // Kos diubah jadi 1 kamar, tapi tidak ada user wishlisting → bisa kamu sesuaikan di sistem kamu
        kos.setAvailableRooms(1);

        // Seharusnya tidak ada notifikasi
        verify(notificationService, never()).notifyUsers(any());
    }
}