package be.technofutur.moonname.repositories;

import be.technofutur.moonname.models.Pierre;
import be.technofutur.moonname.enumss.StatutPierre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PierreRepository extends JpaRepository<Pierre, Long> {

    List<Pierre> findByProprietaireIdOrderByIdDesc(Long id);

    List<Pierre> findByProprietaireIdAndDansPanierTrueOrderById(Long id);

    List<Pierre> findByMissionIdOrderById(Long id);

    List<Pierre> findByStatutAndPublierTrueOrderByIdDesc(StatutPierre statut);

    boolean existsByMissionId(Long id);
}