package com.lordjoe.resource_guide.config;

import com.lordjoe.resource_guide.security.CustomUserDetailsService;
import com.lordjoe.resource_guide.security.EncryptPasswordEncoder;
import jakarta.servlet.http.Cookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new EncryptPasswordEncoder();
    }

    @Bean
    public CustomUserDetailsService userDetailsService() {
        return new CustomUserDetailsService(); // will use Guide.getInstance().getUsers()
    }

    @Bean
    public AuthenticationManager authenticationManager(CustomUserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/main", "/category", "/subcategory", "/resource",
                                "/login", "/logout", "/error", "/sandhurst/editInhabitant",
                                "/sandhurst", "/sandhurst/house/**",
                                "/Cover.png", "/favicon.ico",
                                "/css/**", "/js/**", "/images/**"
                        ).permitAll()
                        .requestMatchers("/admin/**", "/edit/**").authenticated()
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/main", true)
                        .failureUrl("/login?error=true")
                        .successHandler((request, response, authentication) -> {
                            Cookie loginCookie = new Cookie("EASTSIDE_LOGIN_TOKEN", "valid");
                            loginCookie.setPath("/");
                            loginCookie.setMaxAge(2 * 60 * 60); // 2 hours
                            response.addCookie(loginCookie);
                            response.sendRedirect("/main");
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/main")
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID","EASTSIDE_LOGIN_TOKEN")
                        .permitAll()
                );

        return http.build();
    }
}
