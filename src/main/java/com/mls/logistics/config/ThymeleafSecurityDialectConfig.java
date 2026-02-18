package com.mls.logistics.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;

/**
 * Registers the Thymeleaf Spring Security dialect so templates can use {@code sec:*} attributes.
 */
@Configuration
public class ThymeleafSecurityDialectConfig {

    @Bean
    public SpringSecurityDialect springSecurityDialect() {
        return new SpringSecurityDialect();
    }
}
