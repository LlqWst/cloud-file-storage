package dev.lqwd.cloudfilestorage;

import com.jayway.jsonpath.JsonPath;
import dev.lqwd.cloudfilestorage.entity.User;
import dev.lqwd.cloudfilestorage.repository.UserRepository;
import dev.lqwd.cloudfilestorage.infrastructure.storage.minio.MinioBucketStorage;
import io.minio.MinioClient;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static dev.lqwd.cloudfilestorage.util.RepeatableErrorMessage.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfiguration.class, TestRedisProvider.class})
@ActiveProfiles("test")
public class RegistrationControllerTest {

    public static final String SIGN_UP_URL = "/api/auth/sign-up";
    public static final String DELIMITER = "; ";

    @MockitoBean
    private MinioClient minioClient;

    @MockitoBean
    private MinioBucketStorage minioBucketStorage;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestRedisProvider redisProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldRegister_With_AppropriateUsername() throws Exception {
        String username = "test_username";
        Cookie[] cookies = registerWithAppropriateUsername(username);
        String sessionId = redisProvider.getSessionId(cookies);
        redisProvider.validateRedisSavedSession(sessionId);
        redisProvider.clearBd();
    }

    @Test
    void shouldThrowException_When_DuplicateUserName() throws Exception {
        String username = "username124";
        String password = "test_password";
        String jsonPath = "$.message";
        registerWithAppropriateUsername(username);

        doSignUpWithCookie(username, password, HttpStatus.CONFLICT.value(), jsonPath, USER_ALREADY_EXISTS_ERROR_MESSAGE);
    }

    @Test
    void shouldReturnJsonErrorMessage_With_InappropriateUsername() throws Exception {
        String username = "test";
        signUpWithInappropriateUsername(username, USERNAME_IS_INCORRECT_ERROR_MESSAGE);
    }

    @Test
    void shouldReturnJsonErrorMessage_With_NullUsername() throws Exception {
        String username = null;
        signUpWithInappropriateUsername(username, USERNAME_IS_INCORRECT_ERROR_MESSAGE);
    }

    @Test
    void shouldReturnJsonErrorMessage_With_EmptyUsername() throws Exception {
        String username = "";
        signUpWithInappropriateUsername(username, USERNAME_IS_BLANK_ERROR_MESSAGE +
                                                  DELIMITER +
                                                  USERNAME_IS_INCORRECT_ERROR_MESSAGE +
                                                  DELIMITER +
                                                  USERNAME_HAS_INVALID_CHARS_ERROR_MESSAGE);
    }

    @Test
    void shouldReturnJsonErrorMessage_With_BlankUsername() throws Exception {
        String username = "             ";
        signUpWithInappropriateUsername(username, USERNAME_IS_BLANK_ERROR_MESSAGE);
    }

    @Test
    void shouldReturnJsonErrorMessage_With_InappropriateEmail() throws Exception {
        String username = "test@gmail...com";
        signUpWithInappropriateUsername(username, USERNAME_HAS_INVALID_CHARS_ERROR_MESSAGE);
    }

    @Test
    void shouldReturnJsonErrorMessage_With_InappropriatePassword() throws Exception {
        String password = "test";
        signUpWithInappropriatePassword(password, PASSWORD_IS_INCORRECT_ERROR_MESSAGE);
    }

    @Test
    void shouldReturnJsonErrorMessage_With_NullPassword() throws Exception {
        String password = null;
        signUpWithInappropriatePassword(password, PASSWORD_IS_INCORRECT_ERROR_MESSAGE);
    }

    @Test
    void shouldReturnJsonErrorMessage_With_EmptyPassword() throws Exception {
        String password = "";
        signUpWithInappropriatePassword(password, PASSWORD_HAS_SPACES_ERROR_MESSAGE +
                                                  DELIMITER +
                                                  PASSWORD_IS_INCORRECT_ERROR_MESSAGE +
                                                  DELIMITER +
                                                  PASSWORD_IS_BLANK_ERROR_MESSAGE);
    }

    @Test
    void shouldReturnJsonErrorMessage_With_BlankPassword() throws Exception {
        String password = "          ";
        signUpWithInappropriatePassword(password, PASSWORD_HAS_SPACES_ERROR_MESSAGE +
                                                  DELIMITER +
                                                  PASSWORD_IS_BLANK_ERROR_MESSAGE);
    }

    @Test
    void shouldReturnJsonErrorMessage_With_SpacesPassword() throws Exception {
        String password = "       f   ";
        signUpWithInappropriatePassword(password, PASSWORD_HAS_SPACES_ERROR_MESSAGE);
    }

    private Cookie[] registerWithAppropriateUsername(String username) throws Exception {
        String password = "test_password";
        String jsonPath = "$.username";

        Cookie[] cookies = doSignUpWithCookie(username, password, HttpStatus.CREATED.value(), jsonPath, username);
        User savedUser = userRepository.findByUsername(username).orElseThrow();

        Assertions.assertEquals(username, savedUser.getUsername());
        Assertions.assertTrue(passwordEncoder.matches(password, savedUser.getPassword()));
        return cookies;
    }

    private void signUpWithInappropriateUsername(String username, String errorMessage) throws Exception {
        String password = "test_password";
        String jsonPath = "$.message";

        doSignUpWithMessageCheck(username, password, HttpStatus.BAD_REQUEST.value(),
                jsonPath, errorMessage);

        Optional<User> user = userRepository.findByUsername(username);
        Assertions.assertTrue(user.isEmpty());
    }

    private void signUpWithInappropriatePassword(String password, String errorMessage) throws Exception {
        String username = "test_user";
        String jsonPath = "$.message";

        doSignUpWithMessageCheck(username, password, HttpStatus.BAD_REQUEST.value(),
                jsonPath, errorMessage);

        Optional<User> user = userRepository.findByUsername(username);
        Assertions.assertTrue(user.isEmpty());
    }

    private Cookie[] doSignUpWithCookie(String username,
                                        String password,
                                        int status,
                                        String jsonPath,
                                        String jsonPathValue) throws Exception {

        return doSignUp(username, password, status)
                .andExpect(jsonPath(jsonPath).value(jsonPathValue))
                .andReturn().getResponse().getCookies();
    }

    private void doSignUpWithMessageCheck(String username,
                                          String password,
                                          int status,
                                          String jsonPath,
                                          String jsonPathValue) throws Exception {

        String result = doSignUp(username, password, status)
                .andReturn().getResponse().getContentAsString();

        Set<String> resValue = Arrays.stream(JsonPath.read(result, jsonPath).toString()
                .split(DELIMITER))
                .collect(Collectors.toSet());
        Set<String> expValue = Arrays.stream(jsonPathValue.split(DELIMITER))
                .collect(Collectors.toSet());

        Assertions.assertEquals(resValue.size(), expValue.size());
        resValue.forEach(x -> Assertions.assertTrue(expValue.contains(x)));

    }

    private ResultActions doSignUp(String username,
                                   String password,
                                   int status) throws Exception {

        return mockMvc.perform(post(SIGN_UP_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "username": "%s",
                                    "password": "%s"
                                }
                                """.formatted(username, password)))
                .andDo(print())
                .andExpect(status().is(status));
    }

}
