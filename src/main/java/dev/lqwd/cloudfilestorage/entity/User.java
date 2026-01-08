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

    private static final int CREDENTIALS_EXPIRE_MONTHS = 30;
    private static final int ACCOUNT_EXPIRE_DAYS = 30;

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
    private LocalDateTime credentialsExpireAt = LocalDateTime.now().plusMonths(CREDENTIALS_EXPIRE_MONTHS);

    @Column(name = "account_expires_at", nullable = false)
    @Builder.Default
    private LocalDateTime accountExpiresAt = LocalDateTime.now().plusDays(ACCOUNT_EXPIRE_DAYS);

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<UserRole> userRoles = new HashSet<>();
}
