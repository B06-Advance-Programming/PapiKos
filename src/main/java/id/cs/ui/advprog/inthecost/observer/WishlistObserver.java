package id.cs.ui.advprog.inthecost.observer;

import id.cs.ui.advprog.inthecost.model.Kos;
import id.cs.ui.advprog.inthecost.service.NotificationService;

public class WishlistObserver implements Observer {

    private final NotificationService notificationService;

    public WishlistObserver(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void update(Object subject) {
        if (subject instanceof Kos kos && kos.getAvailableRooms() > 0) {
            notificationService.notifyUsers(kos);
        }
    }
}
