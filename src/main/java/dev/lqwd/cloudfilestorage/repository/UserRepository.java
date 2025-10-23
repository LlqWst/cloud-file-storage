package dev.lqwd.cloudfilestorage.repository;

import dev.lqwd.cloudfilestorage.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"userRoles"})
    @NonNull
    Optional<User> findById(@NonNull Long id);

    @EntityGraph(attributePaths = {"userRoles"})
    @NonNull
    Optional<User> findByUsername(@NonNull String username);

}
