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
                        .requestMatchers(
                                "/", "/main", "/category", "/subcategory", "/resource",
                                "/login", "/logout", "/error",
                                "/Cover.png", "/favicon.ico",
                                "/css/**", "/js/**", "/images/**", "/**/*.css", "/**/*.js", "/**/*.png"
                        ).permitAll()
                        .requestMatchers("/admin/**", "/edit/**").authenticated()
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
                .sessionManagement(session -> session
                        .invalidSessionUrl(null) // ❗ disables forced redirect on invalid session
                )
                .csrf().disable();

        return http.build();
    }

    @Bean
    @SuppressWarnings("deprecation")
    public static PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance(); // only for dev/test
    }
}
