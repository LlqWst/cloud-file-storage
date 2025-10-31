package dev.lqwd.cloudfilestorage.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;


@Builder
@AllArgsConstructor
@Data
public class CustomUserDetails implements UserDetails {

    @Getter
    private final Long id;

    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean isEnabled;
    private final boolean isAccountNonLocked;
    private final boolean isAccountNonExpired;
    private final boolean isCredentialsNonExpired;

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }
}
