package com.mls.logistics.security.config;

import com.mls.logistics.security.domain.AppUser;
import com.mls.logistics.security.domain.Role;
import com.mls.logistics.security.repository.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final BootstrapAdminProperties bootstrapAdminProperties;

    public BootstrapAdminUserRunner(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            BootstrapAdminProperties bootstrapAdminProperties) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.bootstrapAdminProperties = bootstrapAdminProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!bootstrapAdminProperties.isEnabled()) {
            log.info("Bootstrap admin user is disabled (security.bootstrap.admin.enabled=false).");
            return;
        }

        if (appUserRepository.count() > 0) {
            log.info("Skipping bootstrap admin creation because at least one user already exists.");
            return;
        }

        boolean adminExists = appUserRepository.existsByRole(Role.ADMIN);
        if (adminExists) {
            log.info("At least one ADMIN user already exists in the database.");
        } else {
            log.warn("No ADMIN user found in the database.");
        }

        var username = bootstrapAdminProperties.getUsername();
        var existingUsername = appUserRepository.findByUsername(username);
        if (existingUsername.isPresent()) {
            log.info("Bootstrap user '{}' already exists with role {}. Skipping creation.", username, existingUsername.get().getRole());
            return;
        }

        if (adminExists) {
            log.info("Skipping bootstrap admin creation for '{}' because an ADMIN user already exists.", username);
            return;
        }

        AppUser user = new AppUser(username, passwordEncoder.encode(bootstrapAdminProperties.getPassword()), Role.ADMIN);
        appUserRepository.save(user);

        log.warn("Created bootstrap ADMIN user '{}' (password configured via security.bootstrap.admin.password / env). Change it immediately.", username);
    }
}
