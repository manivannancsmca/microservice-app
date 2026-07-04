package com.user_service.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_email", columnNames = "email")
    }
)
@Getter                 // Safe: Only generates getters
@Setter                 // Safe: Only generates setters
@NoArgsConstructor      // Required by JPA
@AllArgsConstructor     // Useful for builders/testing
public class User extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;
    
    private String shippingAddress;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}