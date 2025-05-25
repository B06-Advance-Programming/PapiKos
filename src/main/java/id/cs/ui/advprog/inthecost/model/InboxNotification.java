package id.cs.ui.advprog.inthecost.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "id")
@ToString
@Entity
@Table(name = "inbox") // Matches the database table name
public class InboxNotification {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id = UUID.randomUUID(); // Generate random UUID by default

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Foreign key to the pengguna table
    private User user;

    private String message;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();    // Constructor with user, message, and createdAt
    public InboxNotification(User user, String message, LocalDateTime createdAt) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.message = message;
        this.createdAt = createdAt;
    }

    // Constructor with user and message (default createdAt)
    public InboxNotification(User user, String message) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }
}