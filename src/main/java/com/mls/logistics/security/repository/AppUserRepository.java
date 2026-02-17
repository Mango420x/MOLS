package com.mls.logistics.security.repository;

import com.mls.logistics.security.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for AppUser persistence.
 */
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    /**
     * Finds a user by username.
     * Used by Spring Security during authentication.
     *
     * @param username the username to look up
     * @return the user if found
     */
    Optional<AppUser> findByUsername(String username);

    /**
     * Checks if a username is already registered.
     * Used during registration to prevent duplicates.
     *
     * @param username the username to check
     * @return true if username exists
     */
    boolean existsByUsername(String username);
}