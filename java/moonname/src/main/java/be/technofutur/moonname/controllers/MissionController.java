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

public class MissionController {
    private final MissionService missions;
    private final FuseeService fusees;
    private final AuthService auth;

    @GetMapping("/missions")
    public String liste(Model model) {
        model.addAttribute("missions", missions.lister());
        return "missions";
    }

    @GetMapping("/missions/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("mission", missions.trouver(id));
        return "mission-detail";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping({"/missions/ajouter", "/missions/{id}/modifier"})
    public String formulaire(@PathVariable(required = false) Long id, Model model) {
        model.addAttribute("form", id == null ? new MissionForm() : missions.preparerModification(id));
        preparer(id, model);
        return "mission-form";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping({"/missions/ajouter", "/missions/{id}/modifier"})
    public String enregistrer(@PathVariable(required = false) Long id,
                              @Valid @ModelAttribute("form") MissionForm form, BindingResult result, Model model) {
        if (!result.hasErrors()) {
            try {
                missions.enregistrer(id, form);
                return "redirect:/missions";
            } catch (IllegalArgumentException e) {
                result.reject("mission", e.getMessage());
            }
        }
        preparer(id, model);
        return "mission-form";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/missions/{id}/supprimer")
    public String supprimer(@PathVariable Long id) {
        missions.supprimer(id);
        return "redirect:/missions";
    }

    private void preparer(Long id, Model model) {
        model.addAttribute("id", id);
        model.addAttribute("fusees", fusees.lister());
        model.addAttribute("astronautes", auth.astronautes());
    }
}