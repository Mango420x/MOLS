package com.mls.logistics.security.config;

import com.mls.logistics.security.filter.JwtAuthFilter;
import com.mls.logistics.security.service.AppUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.core.annotation.Order;
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
 * - Stateless REST API security (JWT)
 * - Stateful UI security (form login + session)
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
        @Order(1)
        public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http)
            throws Exception {
        http
            .securityMatcher(
                "/api/**",
                "/swagger-ui.html", "/swagger-ui/**",
                "/v3/api-docs/**",
                "/actuator/**")
            // Disable CSRF — not needed for stateless REST APIs
            .csrf(AbstractHttpConfigurer::disable)
            // Stateless sessions — JWT handles auth, no server sessions
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints — no token required
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**",
                    "/v3/api-docs/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                // Read operations — any authenticated user (ADMIN or OPERATOR)
                .requestMatchers(HttpMethod.GET, "/api/**").authenticated()
                // Write operations — ADMIN only
                .requestMatchers(HttpMethod.POST, "/api/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
                .anyRequest().permitAll())
            // Register JWT filter before Spring's default auth filter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

        @Bean
        @Order(2)
        public SecurityFilterChain uiSecurityFilterChain(HttpSecurity http)
            throws Exception {
        http
            .securityMatcher(
                "/",
                "/ui/**",
                "/css/**",
                "/js/**",
                "/images/**",
                "/webjars/**",
                "/favicon.ico")
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/ui/login").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()
                .requestMatchers("/").permitAll()
                .requestMatchers("/ui/**").authenticated()
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/ui/login")
                .loginProcessingUrl("/ui/login")
                .defaultSuccessUrl("/ui", true)
                .failureUrl("/ui/login?error"))
            .logout(logout -> logout
                .logoutUrl("/ui/logout")
                .logoutSuccessUrl("/ui/login?logout"));

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