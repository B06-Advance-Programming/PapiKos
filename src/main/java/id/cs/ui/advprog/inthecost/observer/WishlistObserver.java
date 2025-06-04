package id.cs.ui.advprog.inthecost.observer;

import id.cs.ui.advprog.inthecost.model.Kost;
import id.cs.ui.advprog.inthecost.service.NotificationService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WishlistObserver implements Observer {

    private final NotificationService notificationService;

    public WishlistObserver(NotificationService notificationService) {
        this.notificationService = notificationService;
    }    @Override
    public void update(Object subject) {
        log.debug("🔔 WISHLIST OBSERVER UPDATE CALLED with subject: {}", (subject != null ? subject.getClass().getSimpleName() : "null"));
        
        if (subject instanceof Kost kost) {
            log.info("📋 Kost details: '{}' - Rooms: {}", kost.getNama(), kost.getJumlahKamar());
            
            if (kost.getJumlahKamar() > 0) {
                log.info("📧 WISHLIST OBSERVER: Sending notifications for kost '{}' (available rooms: {})", kost.getNama(), kost.getJumlahKamar());
                notificationService.notifyUsers(kost);
                log.info("✅ NotificationService.notifyUsers() completed");
            } else {
                log.debug("❌ No rooms available - skipping notification");
            }
        } else {
            log.warn("❌ Subject is not a Kost object - skipping");
        }
    }
}
