package id.cs.ui.advprog.inthecost.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;

@Entity
@Table(name = "role")
public class Role implements GrantedAuthority {

    @Id
    @Column(name = "name", nullable = false, unique = true)
    private String name;

    // Konstruktor kosong diperlukan oleh JPA
    public Role() {}

    public Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getAuthority() {
        return this.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        Role role = (Role) o;
        return Objects.equals(name, role.name); // gunakan field unik
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
