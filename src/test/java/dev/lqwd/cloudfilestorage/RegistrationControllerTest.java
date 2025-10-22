package dev.lqwd.cloudfilestorage;

import dev.lqwd.cloudfilestorage.entity.Role;
import dev.lqwd.cloudfilestorage.entity.User;
import dev.lqwd.cloudfilestorage.entity.UserRole;
import dev.lqwd.cloudfilestorage.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@Import(TestcontainersConfiguration.class)
public class RegistrationControllerTest {

    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void shouldRegisterUser() {

        User user = User.builder()
                .username("test_username")
                .password(passwordEncoder.encode("test_password"))
                .build();

        UserRole role = UserRole.builder()
                .user(user)
                .role(Role.ROLE_USER)
                .build();

        user.getUserRoles().add(role);

        User savedUser = userRepository.save(user);
        Assertions.assertEquals("test_username", savedUser.getUsername());
        Assertions.assertTrue(passwordEncoder.matches("test_password", savedUser.getPassword()));

    }

}
