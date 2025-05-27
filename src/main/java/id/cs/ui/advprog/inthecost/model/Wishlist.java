package id.cs.ui.advprog.inthecost.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "wishlist") // Matches the database table name
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Use IDENTITY for consistency
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Foreign key to the pengguna table
    private User user;

    @ManyToOne
    @JoinColumn(name = "kost_id", nullable = false) // Foreign key to the kost table
    private Kost kos;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
