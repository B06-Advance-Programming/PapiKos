package id.cs.ui.advprog.inthecost.observer;

import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.service.NotificationService;

public class WishlistObserver implements Observer {

    private final NotificationService notificationService;

    public WishlistObserver(NotificationService notificationService) {
        this.notificationService = notificationService;
    }    @Override
    public void update(Object subject) {
        System.out.println("🔔 WISHLIST OBSERVER UPDATE CALLED with subject: " + (subject != null ? subject.getClass().getSimpleName() : "null"));
        
        if (subject instanceof Kost kost) {
            System.out.println("📋 Kost details: '" + kost.getNama() + "' - Rooms: " + kost.getJumlahKamar());
            
            if (kost.getJumlahKamar() > 0) {
                System.out.println("📧 WISHLIST OBSERVER: Sending notifications for kost '" + kost.getNama() + "' (available rooms: " + kost.getJumlahKamar() + ")");
                notificationService.notifyUsers(kost);
                System.out.println("✅ NotificationService.notifyUsers() completed");
            } else {
                System.out.println("❌ No rooms available - skipping notification");
            }
        } else {
            System.out.println("❌ Subject is not a Kost object - skipping");
        }
    }
}
