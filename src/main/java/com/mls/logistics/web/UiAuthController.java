package com.mls.logistics.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * UI authentication endpoints.
 */
@Controller
public class UiAuthController {

    /**
     * Login page used by Spring Security form login.
     */
    @GetMapping("/ui/login")
    public String login() {
        return "ui/login";
    }
}
