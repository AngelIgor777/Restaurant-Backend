package org.test.restaurant_service.entity;


import lombok.*;

import javax.persistence.Table;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "users", schema = "restaurant_service")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "uuid", updatable = false, nullable = false)
    private UUID uuid;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            schema = "restaurant_service",
            joinColumns = @JoinColumn(name = "user_uuid"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chat_id", referencedColumnName = "chat_id", unique = true)
    private TelegramUserEntity telegramUserEntity;

    @OneToOne(mappedBy = "user")
    private Address address;


    public boolean hasAddress() {
        return address != null;
    }

    public boolean isAdminOrModerator() {
        List<Role> userRoles = this.getRoles();
        return userRoles.stream()
                .anyMatch(role -> role.getRoleName().name().equals("ROLE_ADMIN") || role.getRoleName().name().equals("ROLE_MODERATOR"));
    }

    public boolean isAdmin() {
        List<Role> userRoles = this.getRoles();
        return userRoles.stream()
                .anyMatch(role -> role.getRoleName().name().equals("ROLE_ADMIN"));
    }

    public boolean isModerator() {
        List<Role> userRoles = this.getRoles();
        return userRoles.stream()
                .anyMatch(role -> role.getRoleName().name().equals("ROLE_MODERATOR"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(uuid, user.uuid) && Objects.equals(createdAt, user.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, createdAt);
    }
}

