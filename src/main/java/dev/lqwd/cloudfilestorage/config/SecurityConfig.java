package dev.lqwd.cloudfilestorage.config;


import dev.lqwd.cloudfilestorage.service.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@AllArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/api/auth/sign-in")
                        .loginProcessingUrl("/api/auth/sign-in")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/")
                        .failureUrl("/api/auth/sign-in")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessUrl("/api/auth/sign-in")
                        .permitAll()
                )
                .userDetailsService(userDetailsServiceImpl)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
