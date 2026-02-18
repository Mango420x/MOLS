package com.mls.logistics.security.config;

import com.mls.logistics.security.domain.AppUser;
import com.mls.logistics.security.domain.Role;
import com.mls.logistics.security.repository.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Bootstraps an initial ADMIN user for local development.
 *
 * <p>On startup, logs whether an ADMIN user already exists. If no ADMIN user exists, it
 * creates one using the configured username/password.</p>
 */
@Component
public class BootstrapAdminUserRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(BootstrapAdminUserRunner.class);

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    private final boolean enabled;
    private final String username;
    private final String password;

    public BootstrapAdminUserRunner(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            @Value("${security.bootstrap.admin.enabled:true}") boolean enabled,
            @Value("${security.bootstrap.admin.username:admin}") String username,
            @Value("${security.bootstrap.admin.password:admin}") String password) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.enabled = enabled;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!enabled) {
            log.info("Bootstrap admin user is disabled (security.bootstrap.admin.enabled=false).");
            return;
        }

        boolean adminExists = appUserRepository.existsByRole(Role.ADMIN);
        if (adminExists) {
            log.info("At least one ADMIN user already exists in the database.");
        } else {
            log.warn("No ADMIN user found in the database.");
        }

        var existingUsername = appUserRepository.findByUsername(username);
        if (existingUsername.isPresent()) {
            log.info("Bootstrap user '{}' already exists with role {}. Skipping creation.", username, existingUsername.get().getRole());
            return;
        }

        if (adminExists) {
            log.info("Skipping bootstrap admin creation for '{}' because an ADMIN user already exists.", username);
            return;
        }

        AppUser user = new AppUser(username, passwordEncoder.encode(password), Role.ADMIN);
        appUserRepository.save(user);

        log.warn("Created bootstrap ADMIN user '{}' (password configured via security.bootstrap.admin.password / env). Change it immediately.", username);
    }
}
