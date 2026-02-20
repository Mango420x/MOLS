package com.mls.logistics.web;

import com.mls.logistics.exception.InvalidRequestException;
import com.mls.logistics.exception.ResourceNotFoundException;
import com.mls.logistics.security.domain.Role;
import com.mls.logistics.security.service.AppUserAdminService;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

import java.util.List;

/**
 * Admin-only users management UI.
 */
@Controller
@RequestMapping("/ui/users")
public class UiUsersController {

    private final AppUserAdminService appUserAdminService;

    public UiUsersController(AppUserAdminService appUserAdminService) {
        this.appUserAdminService = appUserAdminService;
    }

    @GetMapping
    public String users(Model model) {
        populate(model);
        if (!model.containsAttribute("createForm")) {
            model.addAttribute("createForm", new UserCreateForm());
        }
        if (!model.containsAttribute("roleForm")) {
            model.addAttribute("roleForm", new UserRoleForm());
        }
        if (!model.containsAttribute("passwordForm")) {
            model.addAttribute("passwordForm", new UserPasswordForm());
        }
        return "ui/users";
    }

    @PostMapping
    public String createUser(
            @Valid @ModelAttribute("createForm") UserCreateForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            populate(model);
            return "ui/users";
        }

        try {
            appUserAdminService.createUser(form.getUsername(), form.getPassword(), form.getRole());
            redirectAttributes.addFlashAttribute("successMessage", "User created successfully.");
            return "redirect:/ui/users";
        } catch (InvalidRequestException ex) {
            populate(model);
            model.addAttribute("errorMessage", ex.getMessage());
            return "ui/users";
        } catch (DataAccessException ex) {
            populate(model);
            model.addAttribute("errorMessage", "We couldn't save your changes right now. Please try again.");
            return "ui/users";
        }
    }

    @PostMapping("/{id}/role")
    public String updateRole(
            @PathVariable Long id,
            @Valid @ModelAttribute("roleForm") UserRoleForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            populate(model);
            model.addAttribute("errorMessage", "Please verify the selected role.");
            return "ui/users";
        }

        try {
            appUserAdminService.updateRole(id, form.getRole());
            redirectAttributes.addFlashAttribute("successMessage", "Role updated successfully.");
            return "redirect:/ui/users";
        } catch (InvalidRequestException | ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/ui/users";
        } catch (DataAccessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "We couldn't save your changes right now. Please try again.");
            return "redirect:/ui/users";
        }
    }

    @PostMapping("/{id}/password")
    public String resetPassword(
            @PathVariable Long id,
            @Valid @ModelAttribute("passwordForm") UserPasswordForm form,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            populate(model);
            model.addAttribute("errorMessage", "Please verify the password requirements.");
            return "ui/users";
        }

        try {
            appUserAdminService.resetPassword(id, form.getPassword());
            redirectAttributes.addFlashAttribute("successMessage", "Password reset successfully.");
            return "redirect:/ui/users";
        } catch (InvalidRequestException | ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/ui/users";
        } catch (DataAccessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "We couldn't save your changes right now. Please try again.");
            return "redirect:/ui/users";
        }
    }

    @PostMapping("/{id}/enabled")
    public String setEnabled(
            @PathVariable Long id,
            @RequestParam("enabled") boolean enabled,
            RedirectAttributes redirectAttributes) {

        try {
            appUserAdminService.setEnabled(id, enabled);
            redirectAttributes.addFlashAttribute("successMessage", enabled ? "User enabled." : "User disabled.");
        } catch (InvalidRequestException | ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        } catch (DataAccessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "We couldn't save your changes right now. Please try again.");
        }

        return "redirect:/ui/users";
    }

    private void populate(Model model) {
        model.addAttribute("active", "users");
        model.addAttribute("roles", List.of(Role.ADMIN, Role.OPERATOR, Role.AUDITOR));
        model.addAttribute("users", appUserAdminService.getAllUsers(Sort.by(Sort.Direction.ASC, "id")));
    }
}
