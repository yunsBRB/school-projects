package be.technofutur.moonname.controllers;

import be.technofutur.moonname.models.*;
import be.technofutur.moonname.services.*;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@Controller
@RequiredArgsConstructor

public class AuthController {
    private final AuthService auth;

    @GetMapping("/login")
    public String login() { return "login"; }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/inscription")
    public String formulaire(Model model) {
        model.addAttribute("form", new InscriptionForm());
        return "inscription";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/inscription")
    public String inscrire(@Valid @ModelAttribute("form") InscriptionForm form, BindingResult result) {
        if (!result.hasErrors()) {
            try {
                auth.inscrire(form);
                return "redirect:/login?inscrit";
            } catch (IllegalArgumentException e) {
                result.reject("compte", e.getMessage());
            } catch (org.springframework.dao.DataIntegrityViolationException e) {
                result.reject("compte", "Username already used");
            }
        }
        return "inscription";
    }
}