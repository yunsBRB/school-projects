package be.technofutur.moonname.services;

import be.technofutur.moonname.models.*;
import be.technofutur.moonname.enumss.*;
import be.technofutur.moonname.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MissionService {

    private final MissionRepository missions;
    private final FuseeRepositories fusees;
    private final UserRepository utilisateurs;
    private final PierreRepository pierres;

    public List<MissionDto> lister() {
        return missions.findAllByOrderByDateDepart()
                .stream()
                .map(MissionDto::fromEntity)
                .toList();
    }

    public List<MissionDto> disponibles() {
        return missions.findAllByOrderByDateDepart()
                .stream()
                .filter(Misssion::reservable)
                .map(MissionDto::fromEntity)
                .toList();
    }

    public MissionDto trouver(Long id) {
        return MissionDto.fromEntity(entite(id));
    }

    public MissionForm preparerModification(Long id) {
        Misssion mission = entite(id);
        MissionForm form = new MissionForm();

        form.setNom(mission.getNom());
        form.setDateDepart(mission.getDateDepart());
        form.setPrix(mission.getPrix());
        form.setPlacesDisponibles(mission.getPlacesDisponibles());
        form.setFuseeId(mission.getFusee().getId());
        form.setAstronauteId(
                mission.getAstronaute() == null
                        ? null
                        : mission.getAstronaute().getId()
        );

        return form;
    }

    @Transactional
    public void enregistrer(Long id, MissionForm form) {
        Misssion mission = id == null
                ? new Misssion()
                : missions.verrouiller(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        if (mission.getStatut() != StatutMission.PLANIFIEE) {
            throw new IllegalArgumentException("Mission already closed or launched");
        }

        mission.setFusee(
                fusees.findById(form.getFuseeId()).orElseThrow(
                        () -> new IllegalArgumentException("Rocket not found")
                )
        );

        mission.setAstronaute(
                utilisateurs.findById(form.getAstronauteId())
                        .filter(user -> user.getRole() == Role.ASTRONAUTE)
                        .orElseThrow(
                                () -> new IllegalArgumentException("Astronaut not found")
                        )
        );

        mission.setNom(form.getNom().strip());
        mission.setDateDepart(form.getDateDepart());
        mission.setPrix(form.getPrix());
        mission.setPlacesDisponibles(form.getPlacesDisponibles());

        missions.save(mission);
    }

    @Transactional
    public void supprimer(Long id) {
        Misssion mission = missions.verrouiller(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        if (pierres.existsByMissionId(id)) {
            throw new IllegalArgumentException("Mission already linked to stones");
        }

        if (mission.getStatut() != StatutMission.PLANIFIEE) {
            throw new IllegalArgumentException("Mission already launched or closed");
        }

        missions.delete(mission);
    }

    public List<MissionDto> pourAstronaute(Long userId) {
        return missions.findByAstronauteIdOrderByDateDepart(userId)
                .stream()
                .map(MissionDto::fromEntity)
                .toList();
    }

    public List<PierreDto> pierres(Long id, Long userId) {
        verifierAstronaute(entite(id), userId);

        return pierres.findByMissionIdOrderById(id)
                .stream()
                .filter(pierre -> pierre.getStatut() != StatutPierre.BROUILLON)
                .map(PierreDto::fromEntity)
                .toList();
    }

    @Transactional
    public void avancer(Long id, Long userId) {
        Misssion mission = missions.verrouiller(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        verifierAstronaute(mission, userId);

        List<Pierre> charge = pierres.findByMissionIdOrderById(id)
                .stream()
                .filter(pierre -> pierre.getStatut() != StatutPierre.BROUILLON)
                .toList();

        if (mission.getStatut() == StatutMission.PLANIFIEE) {
            mission.setStatut(StatutMission.EN_VOL);
            charge.forEach(pierre -> pierre.setStatut(StatutPierre.ENVOYEE));

        } else if (mission.getStatut() == StatutMission.EN_VOL) {
            boolean resteDesPierres = charge.stream()
                    .anyMatch(pierre -> pierre.getStatut() != StatutPierre.DEPOSEE);

            if (resteDesPierres) {
                throw new IllegalArgumentException("Deposit all stones first");
            }

            mission.setStatut(StatutMission.TERMINEE);

        } else {
            throw new IllegalArgumentException("Mission already closed");
        }
    }

    @Transactional
    public void deposer(Long missionId, Long pierreId, Long userId) {
        Misssion mission = missions.verrouiller(missionId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        verifierAstronaute(mission, userId);

        Pierre pierre = pierres.findById(pierreId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        if (pierre.getMission() == null
                || !pierre.getMission().getId().equals(missionId)) {
            throw new AccessDeniedException("Access denied");
        }

        if (mission.getStatut() != StatutMission.EN_VOL
                || pierre.getStatut() != StatutPierre.ENVOYEE) {
            throw new IllegalArgumentException("Stone must be in flight");
        }

        pierre.setStatut(StatutPierre.DEPOSEE);
    }

    private void verifierAstronaute(Misssion mission, Long userId) {
        if (mission.getAstronaute() == null
                || !mission.getAstronaute().getId().equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }
    }

    private Misssion entite(Long id) {
        return missions.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
    }
}