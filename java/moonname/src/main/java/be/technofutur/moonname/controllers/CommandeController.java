package be.technofutur.moonname.controllers;

import be.technofutur.moonname.models.*;
import be.technofutur.moonname.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/commandes")
@PreAuthorize("hasAuthority('CLIENT')")
public class CommandeController {
    private final CommandeService commandes;

    @GetMapping
    public String liste(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("commandes", commandes.lister(user.getId()));
        return "commandes";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, @AuthenticationPrincipal User user, Model model) {
        model.addAttribute("commande", commandes.trouver(id, user.getId()));
        return "commande-detail";
    }

    @PostMapping
    public String acheter(@AuthenticationPrincipal User user, RedirectAttributes redirect) {
        try {
            return "redirect:/commandes/" + commandes.acheter(user.getId());
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/panier";
        }
    }
}