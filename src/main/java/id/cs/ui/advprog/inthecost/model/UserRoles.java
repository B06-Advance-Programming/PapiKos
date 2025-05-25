package id.cs.ui.advprog.inthecost.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "user_roles")
public class UserRoles implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "role", referencedColumnName = "name", nullable = false)
    private Role role;

    public UserRoles() {}

    public UserRoles(User user, Role role) {
        this.user = user;
        this.role = role;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRoles that)) return false;
        return Objects.equals(user.getId(), that.user.getId()) &&
                Objects.equals(role.getName(), that.role.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(user.getId(), role.getName());
    }
}
