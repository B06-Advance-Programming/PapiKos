package id.cs.ui.advprog.inthecost.observer;

import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.service.NotificationService;

public class WishlistObserver implements Observer {

    private final NotificationService notificationService;

    public WishlistObserver(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void update(Object subject) {
        if (subject instanceof Kost kost) {
            // Only notify users if the Kost has available rooms
            if (kost.getJumlahKamar() > 0) {
                notificationService.notifyUsers(kost);
            }
        }
    }
}
