package dev.lqwd.cloudfilestorage.repository;

import dev.lqwd.cloudfilestorage.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @EntityGraph(attributePaths = {"userRoles"})
    Optional<User> findWithRolesById(Long id);

    @EntityGraph(attributePaths = {"userRoles"})
    Optional<User> findWithRolesByUsername(String username);

}
