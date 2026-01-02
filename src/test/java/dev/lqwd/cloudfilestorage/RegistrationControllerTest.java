package dev.lqwd.cloudfilestorage;

import dev.lqwd.cloudfilestorage.entity.User;
import dev.lqwd.cloudfilestorage.repository.UserRepository;
import dev.lqwd.cloudfilestorage.repository.storage.minio.MinioBucketStorage;
import io.minio.MinioClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
public class RegistrationControllerTest {

    public static final String SIGN_UP_URL = "/api/auth/sign-up";
    public static final String PASSWORD_DEFAULT_MESSAGE = "Password must be 5-20 characters long";
    public static final String USERNAME_DEFAULT_MESSAGE = "Please provide username 5-20 characters long (valid chars: a-zA-Z0-9 ~!#$%^&*()_=+/'\\\".-)";

    @MockitoBean
    private MinioClient minioClient;

    @MockitoBean
    private MinioBucketStorage minioBucketStorage;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Test
    void shouldThrowException_When_DuplicateUserName() throws Exception {

        String username = "username124";
        String password = "test_password";
        String jsonPath = "$.message";
        String jsonPathValue = "User already exists";
        registerWithAppropriateUsername(username);
        doSignUp(username, password, HttpStatus.CONFLICT.value(), jsonPath, jsonPathValue);
    }

    @Test
    void shouldRegister_With_AppropriateUsername() throws Exception {

        String username = "test_username";
        registerWithAppropriateUsername(username);
    }

    @Test
    void shouldReturnJsonErrorMessage_With_InappropriateUsername() throws Exception {

        String username = "test";
        signUpWithInappropriateUsername(username);
    }

    @Test
    void shouldReturnJsonErrorMessage_With_NullUsername() throws Exception {

        String username = null;
        signUpWithInappropriateUsername(username);
    }

    @Test
    void shouldReturnJsonErrorMessage_With_EmptyUsername() throws Exception {

        String username = "";
        signUpWithInappropriateUsername(username);
    }

    @Test
    void shouldReturnJsonErrorMessage_With_BlankUsername() throws Exception {

        String username = "             ";
        signUpWithInappropriateUsername(username);
    }

    @Test
    void shouldReturnJsonErrorMessage_With_InappropriateEmail() throws Exception {

        String username = "test@gmail...com";
        signUpWithInappropriateUsername(username);
    }

    @Test
    void shouldReturnJsonErrorMessage_With_InappropriatePassword() throws Exception {

        String password = "test";
        signUpWithInappropriatePassword(password);
    }

    @Test
    void shouldReturnJsonErrorMessage_With_NullPassword() throws Exception {

        String password = null;
        signUpWithInappropriatePassword(password);
    }

    @Test
    void shouldReturnJsonErrorMessage_With_EmptyPassword() throws Exception {

        String password = "";
        signUpWithInappropriatePassword(password);
    }

    @Test
    void shouldReturnJsonErrorMessage_With_BlankPassword() throws Exception {

        String password = "          ";
        signUpWithInappropriatePassword(password);
    }

    private void registerWithAppropriateUsername(String username) throws Exception {
        String password = "test_password";
        String jsonPath = "$.username";

        doSignUp(username, password, HttpStatus.CREATED.value(), jsonPath, username);

        User savedUser = userRepository.findByUsername(username).orElseThrow();

        Assertions.assertEquals(username, savedUser.getUsername());
        Assertions.assertTrue(passwordEncoder.matches(password, savedUser.getPassword()));
    }

    private void signUpWithInappropriateUsername(String username) throws Exception {
        String password = "test_password";
        String jsonPath = "$.message";

        doSignUp(username, password, HttpStatus.BAD_REQUEST.value(), jsonPath, USERNAME_DEFAULT_MESSAGE);

        Optional<User> user = userRepository.findByUsername(username);
        Assertions.assertTrue(user.isEmpty());
    }

    private void signUpWithInappropriatePassword(String password) throws Exception {
        String username = "test_user";
        String jsonPath = "$.message";

        doSignUp(username, password, HttpStatus.BAD_REQUEST.value(), jsonPath, PASSWORD_DEFAULT_MESSAGE);

        Optional<User> user = userRepository.findByUsername(username);
        Assertions.assertTrue(user.isEmpty());
    }

    private void doSignUp(String username,
                          String password,
                          int status,
                          String jsonPath,
                          String jsonPathValue) throws Exception {

        mockMvc.perform(post(SIGN_UP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "%s",
                                    "password": "%s"
                                }
                                """.formatted(username, password)))
                .andDo(print())
                .andExpect(status().is(status))
                .andExpect(jsonPath(jsonPath).value(jsonPathValue));
    }

}
