package id.cs.ui.advprog.inthecost.model;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class InboxNotification {

    private String userId;
    private String message;
    private LocalDateTime createdAt = LocalDateTime.now();

    public InboxNotification(String userId, String message) {
        this.userId = userId;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }
}

