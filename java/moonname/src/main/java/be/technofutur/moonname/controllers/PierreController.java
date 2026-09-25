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
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pierres")
@PreAuthorize("hasAuthority('CLIENT')")
public class PierreController {
    private final PierreService pierres;
    private final MissionService missions;

    @GetMapping
    public String liste(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("pierres", pierres.lister(user.getId()));
        return "pierres";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, @AuthenticationPrincipal User user, Model model) {
        model.addAttribute("pierre", pierres.trouver(id, user.getId()));
        return "pierre-detail";
    }

    @GetMapping({"/ajouter", "/{id}/modifier"})
    public String formulaire(@PathVariable(required = false) Long id,
                             @AuthenticationPrincipal User user, Model model) {
        model.addAttribute("form", id == null ? new PierreForm() : pierres.preparerModification(id, user.getId()));
        preparer(id, model);
        return "pierre-form";
    }

    @PostMapping({"/ajouter", "/{id}/modifier"})
    public String enregistrer(@PathVariable(required = false) Long id,
                              @AuthenticationPrincipal User user,
                              @Valid @ModelAttribute("form") PierreForm form, BindingResult result, Model model) {
        if (!result.hasErrors()) {
            try {
                return "redirect:/pierres/" + pierres.enregistrer(id, form, user.getId());
            } catch (IllegalArgumentException e) {
                result.reject("pierre", e.getMessage());
            }
        }
        preparer(id, model);
        return "pierre-form";
    }

    @PostMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id, @AuthenticationPrincipal User user) {
        pierres.supprimer(id, user.getId());
        return "redirect:/pierres";
    }

    private void preparer(Long id, Model model) {
        model.addAttribute("id", id);
        model.addAttribute("missions", missions.disponibles());
    }
}