package be.technofutur.moonname.controllers;

import be.technofutur.moonname.models.*;
import be.technofutur.moonname.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/astronaute")
@PreAuthorize("hasAuthority('ASTRONAUTE')")
public class AstronauteController {
    private final MissionService missions;

    @GetMapping
    public String liste(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("missions", missions.pourAstronaute(user.getId()));
        return "astronaute-missions";
    }

    @GetMapping("/missions/{id}")
    public String detail(@PathVariable Long id, @AuthenticationPrincipal User user, Model model) {
        model.addAttribute("pierres", missions.pierres(id, user.getId()));
        model.addAttribute("mission", missions.trouver(id));
        return "astronaute-detail";
    }

    @PostMapping("/missions/{id}/avancer")
    public String avancer(@PathVariable Long id, @AuthenticationPrincipal User user) {
        missions.avancer(id, user.getId());
        return "redirect:/astronaute/missions/" + id;
    }

    @PostMapping("/missions/{id}/pierres/{pierreId}/deposer")
    public String deposer(@PathVariable Long id, @PathVariable Long pierreId,
                          @AuthenticationPrincipal User user) {
        missions.deposer(id, pierreId, user.getId());
        return "redirect:/astronaute/missions/" + id;
    }
}