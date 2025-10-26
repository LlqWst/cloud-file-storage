package dev.lqwd.cloudfilestorage.service;

import dev.lqwd.cloudfilestorage.model.CustomUserDetails;
import dev.lqwd.cloudfilestorage.entity.User;
import dev.lqwd.cloudfilestorage.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return getUserDetails(user);
    }

    private static CustomUserDetails getUserDetails(User user) {
        return CustomUserDetails.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .isAccountNonLocked(!user.isLocked())
                .isEnabled(!user.isDisabled())
                .isAccountNonExpired(isAccountNonExpired(user))
                .isCredentialsNonExpired(isCredentialsNonExpired(user))
                .authorities(getAuthorities(user))
                .build();
    }

    private static List<SimpleGrantedAuthority> getAuthorities(User user) {
        return user.getUserRoles().stream()
                .map(userRole -> new SimpleGrantedAuthority(userRole.getRole().name()))
                .collect(Collectors.toList());
    }

    private static boolean isAccountNonExpired(User user) {
        return !user.getAccountExpiresAt().isBefore(LocalDateTime.now());
    }

    private static boolean isCredentialsNonExpired(User user) {
        return !user.getCredentialsExpireAt().isBefore(LocalDateTime.now());
    }

}
