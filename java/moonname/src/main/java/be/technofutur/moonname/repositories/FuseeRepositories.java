package be.technofutur.moonname.repositories;

import be.technofutur.moonname.models.Fusee;
import be.technofutur.moonname.enumss.*;
import org.springframework.data.jpa.repository.*;

public interface FuseeRepositories extends JpaRepository<Fusee, Long> {

}

// fusee = entité gérée;
// Long = type de l'identifiant //
// JpaRepository fournit findAll(), findbyId(), save(), deleteById()