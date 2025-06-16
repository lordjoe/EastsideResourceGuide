package com.lordjoe.resource_guide.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // ✅ exact match, no trailing slash
                        .requestMatchers("/sandhurst").permitAll()
                        // ✅ allow everything public before auth rules
                        .requestMatchers(
                                "/", "/main", "/category", "/subcategory", "/resource",
                                "/login", "/logout", "/error",
                                "/sandhurst",
                                "/sandhurst/house/**",
                                "/Cover.png", "/favicon.ico",
                                "/css/**", "/js/**", "/images/**", "/**/*.css", "/**/*.js", "/**/*.png"
                        ).permitAll()
                        // ✅ restrict edit/admin routes
                        .requestMatchers("/admin/**", "/edit/**").authenticated()
                        // ✅ fallback for unknown paths
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/main", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .csrf().disable(); // only disable if no CSRF tokens are used in forms

        return http.build();
    }

    @Bean
    @SuppressWarnings("deprecation")
    public static PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance(); // for testing only
    }
}
