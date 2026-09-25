package be.technofutur.moonname.models;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FuseeForm {
    @NotBlank(message = "Country required")
    private String pays;
    @NotBlank(message = "Name required")
    @Size(max = 50, message = "Name must not exceed 50 characters")
    private String nom;

    public Fusee toEntity() {
        Fusee f = new Fusee();
        f.setPays(pays.strip());
        f.setNom(nom.strip());
        return f;
    }
}
    //toEntity() c'est pour transform les données recues en entité. l'identifiant est null car la base va le generer
