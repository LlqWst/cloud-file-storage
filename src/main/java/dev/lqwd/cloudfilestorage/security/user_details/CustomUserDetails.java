package dev.lqwd.cloudfilestorage.security.user_details;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CustomUserDetails implements UserDetails {

    @Getter
    private Long id;

    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    @JsonProperty("enabled")
    private boolean isEnabled;

    @JsonProperty("accountNonLocked")
    private boolean isAccountNonLocked;

    @JsonProperty("accountNonExpired")
    private boolean isAccountNonExpired;

    @JsonProperty("credentialsNonExpired")
    private boolean isCredentialsNonExpired;


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
        return isAccountNonLocked;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }
}
