package dev.lqwd.cloudfilestorage.config.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final JsonAuthenticationSuccessHandler successHandler;
    private final JsonAuthenticationFailureHandler failureHandler;
    private final ObjectMapper objectMapper;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {

        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder
                .userDetailsService(userDetailsServiceImpl)
                .passwordEncoder(passwordEncoder());

        return builder.build();
    }

    @Bean
    public JsonAuthenticationFilter jsonAuthenticationFilter(
            AuthenticationManager authenticationManager) {

        JsonAuthenticationFilter filter = new JsonAuthenticationFilter(objectMapper);
        filter.setAuthenticationManager(authenticationManager);
        filter.setFilterProcessesUrl("/api/auth/sign-in");
        filter.setAuthenticationSuccessHandler(successHandler);
        filter.setAuthenticationFailureHandler(failureHandler);
        return filter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JsonAuthenticationFilter jsonAuthenticationFilter) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jsonAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
