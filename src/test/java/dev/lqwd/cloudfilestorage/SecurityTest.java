package dev.lqwd.cloudfilestorage;


import dev.lqwd.cloudfilestorage.model.Role;
import dev.lqwd.cloudfilestorage.entity.User;
import dev.lqwd.cloudfilestorage.entity.UserRole;
import dev.lqwd.cloudfilestorage.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
public class SecurityTest {

    private static final String SIGN_IN_URL = "/api/auth/sign-in";
    private static final String SIGN_OUT_URL = "/api/auth/sign-out";
    private static final String GET_ME_URL = "/api/user/me";
    private static final String APPROPRIATE_USERNAME = "user123";
    private static final String APPROPRIATE_PASSWORD = "password123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @BeforeEach
    public void registerUser() {
        userRepository.deleteAll();

        User user = User.builder()
                .username(APPROPRIATE_USERNAME)
                .password(passwordEncoder.encode(APPROPRIATE_PASSWORD))
                .build();

        UserRole role = UserRole.builder()
                .user(user)
                .role(Role.ROLE_USER)
                .build();

        user.getUserRoles().add(role);
        userRepository.saveAndFlush(user);
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void shouldSignIn_With_AppropriateCredentials() throws Exception {
        String jsonPath = "$.username";
        doSignIn(APPROPRIATE_USERNAME, APPROPRIATE_PASSWORD, HttpStatus.OK.value(), jsonPath, APPROPRIATE_USERNAME);
    }

    @Test
    public void shouldThrowBadRequest_When_UsernameLessThan6Char() throws Exception {
        String username = "123";
        String password = "password123";
        String jsonPath = "$.message";
        String jsonPathValue = "Bad credentials";

        doSignIn(username, password, HttpStatus.BAD_REQUEST.value(), jsonPath, jsonPathValue);
    }

    @Test
    public void shouldThrowBadRequest_When_PasswordLessThan6Char() throws Exception {
        String username = "username123";
        String password = "pass";
        String jsonPath = "$.message";
        String jsonPathValue = "Bad credentials";

        doSignIn(username, password, HttpStatus.BAD_REQUEST.value(), jsonPath, jsonPathValue);
    }

    @Test
    public void shouldThrowUnauthorized_With_InappropriateCredentials() throws Exception {
        String username = "username124";
        String password = "password123";
        String jsonPath = "$.message";
        String jsonPathValue = "Bad credentials";

        doSignIn(username, password, HttpStatus.UNAUTHORIZED.value(), jsonPath, jsonPathValue);
    }

    @Test
    @WithAnonymousUser
    public void shouldThrowUnauthorized_When_GetRestrictedUrlUnauthorized() throws Exception {

        mockMvc.perform(get(GET_ME_URL))
                .andDo(print())
                .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    @WithMockUser
    public void shouldSendOk_When_GetRestrictedUrlAuthorized() throws Exception {
        String jsonPath = "$.username";

        mockMvc.perform(get(GET_ME_URL))
                .andDo(print())
                .andExpect(status().is((HttpStatus.OK.value())))
                .andExpect(jsonPath(jsonPath).value("user"));
    }

    @Test
    @WithAnonymousUser
    public void shouldThrowUnauthorized_When_UnauthorizedLogout() throws Exception {

        mockMvc.perform(post(SIGN_OUT_URL))
                .andDo(print())
                .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
    }

    @Test
    @WithMockUser
    public void shouldThrowUnauthorized_When_AuthorizedLogout() throws Exception {

        mockMvc.perform(post(SIGN_OUT_URL))
                .andDo(print())
                .andExpect(status().is(HttpStatus.NO_CONTENT.value()));
    }

    private void doSignIn(String username,
                          String password,
                          int status,
                          String jsonPath,
                          String jsonPathValue) throws Exception {

        mockMvc.perform(post(SIGN_IN_URL)
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
