package com.mls.logistics.security.config;

import com.mls.logistics.security.domain.AppUser;
import com.mls.logistics.security.domain.Role;
import com.mls.logistics.security.repository.AppUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Safety runner for upgrading existing databases.
 *
 * When the `enabled` flag was introduced, pre-existing rows could end up disabled
 * depending on how the schema update was applied. This runner prevents lock-outs
 * by ensuring there is at least one enabled user (preferably an ADMIN).
 */
@Component
public class UserEnabledBackfillRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(UserEnabledBackfillRunner.class);

    private final AppUserRepository appUserRepository;

    public UserEnabledBackfillRunner(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (appUserRepository.count() == 0) {
            return;
        }

        // Ensure at least one enabled ADMIN exists. Otherwise nobody can manage users.
        if (appUserRepository.countByRoleAndEnabledTrue(Role.ADMIN) == 0) {
            List<AppUser> admins = appUserRepository.findAllByRole(Role.ADMIN);
            if (!admins.isEmpty()) {
                admins.forEach(a -> a.setEnabledFlag(true));
                appUserRepository.saveAll(admins);
                log.warn("No enabled ADMIN users were found. Enabled {} ADMIN account(s) to prevent lock-out.", admins.size());
                return;
            }
        }

        // If there are absolutely no enabled users, enable everyone as a last resort.
        if (appUserRepository.countByEnabledTrue() == 0) {
            List<AppUser> all = appUserRepository.findAll();
            all.forEach(u -> u.setEnabledFlag(true));
            appUserRepository.saveAll(all);
            log.warn("No enabled users were found. Enabled all {} user account(s) to prevent lock-out.", all.size());
        }
    }
}
