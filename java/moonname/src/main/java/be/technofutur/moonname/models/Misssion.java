package be.technofutur.moonname.models;

import be.technofutur.moonname.enumss.StatutMission;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "mission")
public class Misssion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private LocalDate dateDepart;

    @Column(precision = 10, scale = 2)
    private BigDecimal prix;

    private int placesDisponibles;

    @ManyToOne(optional = false)
    @JoinColumn(name = "fusee_id")
    private Fusee fusee;

    @ManyToOne
    private User astronaute;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(30) default 'PLANIFIEE'")
    private StatutMission statut = StatutMission.PLANIFIEE;

    public boolean reservable() {
        return statut == StatutMission.PLANIFIEE
                && placesDisponibles > 0
                && dateDepart != null
                && !dateDepart.isBefore(LocalDate.now());
    }
}