package be.technofutur.moonname.controllers;

import be.technofutur.moonname.models.*;
import be.technofutur.moonname.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.math.BigDecimal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/panier")
@PreAuthorize("hasAuthority('CLIENT')")
public class PanierController {
    private final PierreService pierres;

    @GetMapping
    public String detail(@AuthenticationPrincipal User user, Model model) {
        var lignes = pierres.panier(user.getId());
        model.addAttribute("pierres", lignes);
        model.addAttribute("total", lignes.stream().map(PierreDto::prix).reduce(BigDecimal.ZERO, BigDecimal::add));
        return "panier";
    }

    @PostMapping("/ajouter/{id}")
    public String ajouter(@PathVariable Long id, @AuthenticationPrincipal User user) {
        pierres.changerPanier(id, user.getId(), true);
        return "redirect:/panier";
    }

    @PostMapping("/retirer/{id}")
    public String retirer(@PathVariable Long id, @AuthenticationPrincipal User user) {
        pierres.changerPanier(id, user.getId(), false);
        return "redirect:/panier";
    }
}