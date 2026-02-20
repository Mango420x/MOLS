package com.mls.logistics.security.domain;

/**
 * Roles available in the MOLS system.
 *
 * ADMIN    - Full access: read, create, update, delete all entities
 * OPERATOR - Operational access: can work on Orders/Shipments, but cannot administer master data
 * AUDITOR  - Read-only access with strong audit visibility
 */
public enum Role {
    ADMIN,
    OPERATOR,
    AUDITOR
}