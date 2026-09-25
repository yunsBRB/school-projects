package be.technofutur.moonname.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Fusee {

    @Id // id unique
    @GeneratedValue (strategy = GenerationType.IDENTITY) // generé par la base

    private Long id;
    private String pays;
    private String nom;


}
