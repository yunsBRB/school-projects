package be.technofutur.moonname.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import be.technofutur.moonname.enumss.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Getter
@Setter
public class Commande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User utilisateur;
    private LocalDateTime dateCreation = LocalDateTime.now();
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL)
    @OrderBy("id")
    private List<Billet> billets = new ArrayList<>();
}