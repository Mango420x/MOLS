package com.mls.logistics.web;

import com.mls.logistics.security.domain.Role;
import jakarta.validation.constraints.NotNull;

/**
 * UI form for changing a user's role (admin-only).
 */
public class UserRoleForm {

    @NotNull(message = "Role is required")
    private Role role;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
