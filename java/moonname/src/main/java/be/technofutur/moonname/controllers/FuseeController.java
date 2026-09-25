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

public class FuseeController {
    private final FuseeService fuseeService;

    @GetMapping("/fusees")
    public String liste(Model model) {
        model.addAttribute("fusees", fuseeService.lister());
        return "fusees";
    }

    @GetMapping("/fusees/{id}")
    public String detail(@PathVariable Long id, Model model) {
        FuseeDto f = fuseeService.trouver(id);
        if (f == null) return "redirect:/fusees";
        model.addAttribute("fusee", f);
        return "fusee-detail";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping({"/fusees/ajouter", "/fusees/{id}/modifier"})
    public String formulaire(@PathVariable(required = false) Long id, Model model) {
        model.addAttribute("id", id);
        model.addAttribute("form", id == null ? new FuseeForm() : fuseeService.preparerModification(id));
        return id == null ? "fusee-form" : "fusee-modifier";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping({"/fusees/ajouter", "/fusees/{id}/modifier"})
    public String enregistrer(@PathVariable(required = false) Long id,
                              @Valid @ModelAttribute("form") FuseeForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("id", id);
            return id == null ? "fusee-form" : "fusee-modifier";
        }
        if (id == null) fuseeService.ajouter(form);
        else fuseeService.modifier(id, form);
        return "redirect:/fusees";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/fusees/{id}/supprimer")
    public String supprimer(@PathVariable Long id) {
        fuseeService.supprimer(id);
        return "redirect:/fusees";
    }
}