package be.technofutur.moonname.repositories;

import be.technofutur.moonname.models.Misssion;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;

import java.util.List;
import java.util.Optional;

public interface MissionRepository extends JpaRepository<Misssion, Long> {

    boolean existsByFuseeId(Long id);

    List<Misssion> findAllByOrderByDateDepart();

    List<Misssion> findByAstronauteIdOrderByDateDepart(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Misssion m where m.id = :id")
    Optional<Misssion> verrouiller(Long id);
}