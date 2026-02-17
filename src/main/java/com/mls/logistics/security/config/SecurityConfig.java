package com.mls.logistics.security.config;

import com.mls.logistics.security.filter.JwtAuthFilter;
import com.mls.logistics.security.service.AppUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Central Spring Security configuration.
 *
 * Defines:
 * - Which endpoints are public vs protected
 * - Role-based access rules per HTTP method
 * - Stateless session (JWT, no server-side sessions)
 * - JWT filter registration
 * - Password encoding with BCrypt
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AppUserService appUserService;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter,
                          AppUserService appUserService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.appUserService = appUserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
        http
            // Disable CSRF — not needed for stateless REST APIs
            .csrf(AbstractHttpConfigurer::disable)

            // Stateless sessions — JWT handles auth, no server sessions
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Authorization rules
            .authorizeHttpRequests(auth -> auth

                // Public endpoints — no token required
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**",
                                 "/v3/api-docs/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()

                // Read operations — any authenticated user (ADMIN or OPERATOR)
                .requestMatchers(HttpMethod.GET, "/api/**").authenticated()

                // Write operations — ADMIN only
                .requestMatchers(HttpMethod.POST, "/api/**")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/**")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/**")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/**")
                    .hasRole("ADMIN")

                // Everything else requires authentication
                .anyRequest().authenticated())

            // Register JWT filter before Spring's default auth filter
            .addFilterBefore(jwtAuthFilter,
                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(
                appUserService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}