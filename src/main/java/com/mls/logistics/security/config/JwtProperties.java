package com.mls.logistics.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {

    /**
     * Secret key used to sign JWT tokens.
     *
     * Note: JwtService currently expects this value to be Base64 encoded.
     */
    private String secretKey;

    /** Token expiration duration in milliseconds. */
    private long expirationMs;

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    public void setExpirationMs(long expirationMs) {
        this.expirationMs = expirationMs;
    }
}
