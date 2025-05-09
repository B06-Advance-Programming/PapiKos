package id.cs.ui.advprog.inthecost.controller;

import id.cs.ui.advprog.inthecost.model.InboxNotification;
import id.cs.ui.advprog.inthecost.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inbox")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<InboxNotification>> getInbox(@PathVariable String userId) {
        List<InboxNotification> inbox = notificationService.getInbox(userId);
        return ResponseEntity.ok(inbox);
    }
}