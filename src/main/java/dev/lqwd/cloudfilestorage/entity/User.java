package dev.lqwd.cloudfilestorage.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username",
            nullable = false,
            unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "is_disabled", nullable = false)
    @Builder.Default
    private boolean isDisabled = false;

    @Column(name = "is_locked", nullable = false)
    @Builder.Default
    private boolean isLocked = false;

    @Column(name = "credentials_expire_at", nullable = false)
    @Builder.Default
    private LocalDateTime credentialsExpireAt = LocalDateTime.now().plusMinutes(5);

    @Column(name = "account_expires_at", nullable = false)
    @Builder.Default
    private LocalDateTime accountExpiresAt = LocalDateTime.now().plusMinutes(5);

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<UserRole> userRoles = new HashSet<>();
}
