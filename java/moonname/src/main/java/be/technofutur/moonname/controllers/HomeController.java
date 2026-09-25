package be.technofutur.moonname.controllers;

import be.technofutur.moonname.models.*;
import be.technofutur.moonname.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor

public class HomeController {
    private final PierreService pierres;

    @GetMapping("/")
    public String accueil() { return "index"; }

    @GetMapping("/edifice")
    public String edifice(Model model) {
        model.addAttribute("pierres", pierres.edifice());
        return "edifice";
    }
}