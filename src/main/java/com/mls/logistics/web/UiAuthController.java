package com.mls.logistics.web;

import com.mls.logistics.security.repository.AppUserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * UI authentication endpoints.
 */
@Controller
public class UiAuthController {

    private final AppUserRepository appUserRepository;

    public UiAuthController(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    /**
     * Login page used by Spring Security form login.
     */
    @GetMapping("/ui/login")
    public String login() {
        if (appUserRepository.count() == 0) {
            return "redirect:/ui/setup";
        }
        return "ui/login";
    }
}
