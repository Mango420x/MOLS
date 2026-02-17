package com.mls.logistics.security.domain;

/**
 * Roles available in the MOLS system.
 *
 * ADMIN    - Full access: read, create, update, delete all entities
 * OPERATOR - Read access + order creation only
 */
public enum Role {
    ADMIN,
    OPERATOR
}