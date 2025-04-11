package id.cs.ui.advprog.inthecost.observer;

import id.ac.ui.cs.papikos.model.Kos;
import id.ac.ui.cs.papikos.service.NotificationService;

public class WishlistObserver implements Observer {

    private NotificationService notificationService;

    public WishlistObserver(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void update(Object subject) {
        if (subject instanceof Kos kos) {
            // Trigger notifikasi hanya jika kos tersedia dan ada di wishlist user
            if (kos.getAvailableRooms() > 0) {
                notificationService.notifyUsers(kos);
            }
        }
    }
}
