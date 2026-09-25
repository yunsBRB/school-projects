package be.technofutur.moonname.models;
import be.technofutur.moonname.enumss.StatutPierre;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;



@Entity
@Getter
@Setter
public class Pierre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nomInscrit;
    @Column(length = 200)
    private String message;
    @Enumerated(EnumType.STRING)
    private StatutPierre statut = StatutPierre.BROUILLON;
    @ManyToOne
    private User proprietaire;
    @ManyToOne
    private Misssion mission;
    @Column(columnDefinition = "boolean default false")
    private boolean dansPanier;
    @Column(columnDefinition = "boolean default false")
    private boolean publier;
}