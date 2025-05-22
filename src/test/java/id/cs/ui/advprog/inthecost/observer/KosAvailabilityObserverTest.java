package id.cs.ui.advprog.inthecost.observer;

import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.mockito.Mockito.*;

public class KosAvailabilityObserverTest {

    private Kost kost;
    private WishlistObserver observer;
    private NotificationService notificationService;

    @BeforeEach
    void setup() {
        notificationService = mock(NotificationService.class);
        observer = new WishlistObserver(notificationService);

        UUID ownerId = UUID.randomUUID(); // Add ownerId for the updated constructor
        kost = new Kost("Kos Asri", "Jl. Asri", "Deskripsi Kos Asri", 2, 1000000, ownerId); // Updated constructor
        kost.addObserver(observer);
    }

    @Test
    void testNotificationSentWhenRoomAvailable() {
        // Kos penuh dulu
        kost.setJumlahKamar(0);

        // Lalu ada kamar kosong 1
        kost.setJumlahKamar(1);

        // Notifikasi seharusnya dikirim
        verify(notificationService, times(1)).notifyUsers(kost);
    }

    @Test
    void testNoNotificationWhenRoomStillUnavailable() {
        kost.setJumlahKamar(0);
        kost.setJumlahKamar(0);

        // Tidak ada perubahan, jadi tidak ada notifikasi
        verify(notificationService, never()).notifyUsers(kost);
    }

    @Test
    void testNoNotificationIfNotOnWishlist() {
        // Kos diubah jadi 1 kamar, tapi tidak ada user wishlisting
        kost.setJumlahKamar(1);

        // Seharusnya tidak ada notifikasi
        verify(notificationService, never()).notifyUsers(any());
    }
}