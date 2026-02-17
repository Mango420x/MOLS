package com.mls.logistics.security.dto;

/**
 * DTO returned after successful authentication.
 * Contains the JWT token and basic user info.
 */
public class AuthResponse {

    private String token;
    private String username;
    private String role;

    public AuthResponse(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }

    public String getToken() { return token; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
}