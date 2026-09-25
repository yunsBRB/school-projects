package be.technofutur.moonname.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import be.technofutur.moonname.enumss.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class Billet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Commande commande;
    @OneToOne(optional = false)
    @JoinColumn(unique = true)
    private Pierre pierre;
    private String nomInscrit;
    @Column(length = 200)
    private String message;
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal prix;
}