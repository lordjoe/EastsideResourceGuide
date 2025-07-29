package com.lordjoe.resource_guide.security;

import com.lordjoe.resource_guide.Guide;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Loads user info from the Guide (not from JPA or LDAP)
 */
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Guide g = Guide.Instance;
        g.guaranteeLoaded();
        GuideUser  appUser = g.getUserByUsername(username);
        if (appUser == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        return User.builder()
                .username(appUser.username())
                .password(appUser.password()) // already encrypted
                .roles(appUser.role() )
                .build();
    }
}


