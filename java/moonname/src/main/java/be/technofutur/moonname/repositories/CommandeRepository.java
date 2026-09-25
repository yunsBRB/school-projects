package be.technofutur.moonname.repositories;

import be.technofutur.moonname.models.Commande;
import org.springframework.data.jpa.repository.*;
import java.util.List;

public interface CommandeRepository extends JpaRepository<Commande, Long> {
    List<Commande> findByUtilisateurIdOrderByIdDesc(Long id);
}

