package dev.lqwd.cloudfilestorage.security;

import dev.lqwd.cloudfilestorage.security.auth.json.JsonAuthenticationFilter;
import dev.lqwd.cloudfilestorage.security.userdetails.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.List;


@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    public static final String POST = "POST";
    public static final String SIGN_OUT_URL = "/api/auth/sign-out";
    public static final int MAX_SESSIONS = 1;
    public static final String UNAUTHORIZED_MESSAGE = "{\"message\":\"Unauthorized user\"}";
    private static final String[] SWAGGER_WHITELIST = {
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/api-docs/**",
            "/swagger-resources/**",
            "/swagger-resources",
            "/webjars/**"
    };
    private static final String[] PUBLIC_ENDPOINTS = {
            "/api/auth/**"
    };

    @Value("${app.cors.allowed-origins}")
    private final List<String> ALLOWED_ORIGINS;

    private static final List<String> ALLOWED_METHODS = List.of(
            "GET", "POST", "PUT", "DELETE", "OPTIONS");
    private static final List<String> ALLOWED_HEADERS = List.of("*");
    public static final String SESSION_COOKIE_NAME = "SESSION";

    private final CustomUserDetailsService customUserDetailsService;
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;
    private final RequestMatcher logoutMatcher = PathPatternRequestMatcher
            .withDefaults()
            .matcher(SIGN_OUT_URL);

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
        return builder.build();
    }

    /*
    TODO: добавить csrf. При включении фронт не передает заголовок, только cookie.
     */

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JsonAuthenticationFilter jsonAuthenticationFilter,
            TomcatErrorFilter tomcatErrorFilter) throws Exception {

        return http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(MAX_SESSIONS)
                        .maxSessionsPreventsLogin(false)
                )
                .cors(cors -> cors
                        .configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .requestCache(RequestCacheConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers(SWAGGER_WHITELIST).permitAll()
                        .anyRequest().authenticated()
                )
                .securityContext(securityContext -> securityContext
                        .securityContextRepository(securityContextRepository())
                        .requireExplicitSave(false)
                )
                .exceptionHandling(conf ->
                        conf.authenticationEntryPoint((req,
                                                       res,
                                                       authException) ->
                                UnauthorizedResponse(res)
                        )
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(conf -> conf
                        .logoutRequestMatcher(req ->
                                logoutMatcher.matches(req) &&
                                POST.equalsIgnoreCase(req.getMethod())
                        )
                        .logoutSuccessHandler(customLogoutSuccessHandler)
                        .deleteCookies(SESSION_COOKIE_NAME)
                        .invalidateHttpSession(true)
                )
                .addFilterBefore(tomcatErrorFilter, WebAsyncManagerIntegrationFilter.class)
                .addFilterBefore(jsonAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    private static void UnauthorizedResponse(HttpServletResponse res) throws IOException {
        res.setStatus(HttpStatus.UNAUTHORIZED.value());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.getWriter().write(UNAUTHORIZED_MESSAGE);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(ALLOWED_ORIGINS);
        configuration.setAllowedMethods(ALLOWED_METHODS);
        configuration.setAllowedHeaders(ALLOWED_HEADERS);
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
