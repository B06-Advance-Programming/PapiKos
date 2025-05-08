package id.cs.ui.advprog.inthecost.observer;

import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.service.NotificationService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WishlistObserver implements Observer {

    private final NotificationService notificationService;

    @Override
    public void update(Object subject) {
        if (subject instanceof Kost) {
            Kost kost = (Kost) subject;
            notificationService.notifyUsers(kost);
        }
    }
}
