package be.technofutur.moonname.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.dao.DataIntegrityViolationException;

@ControllerAdvice
public class ErreurController {
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String metier(IllegalArgumentException e, Model model) {
        model.addAttribute("erreur", e.getMessage());
        return "erreur";
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String conflit(Model model) {
        model.addAttribute("erreur", "Data already used. Reload the page.");
        return "erreur";
    }
}