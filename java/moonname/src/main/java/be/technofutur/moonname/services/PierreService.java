package be.technofutur.moonname.services;

import be.technofutur.moonname.models.*;
import be.technofutur.moonname.enumss.*;
import be.technofutur.moonname.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PierreService {
    private final PierreRepository pierres;
    private final MissionRepository missions;
    private final UserRepository utilisateurs;

    public List<PierreDto> lister(Long userId) {
        return pierres.findByProprietaireIdOrderByIdDesc(userId).stream().map(PierreDto::fromEntity).toList();
    }

    public PierreDto trouver(Long id, Long userId) {
        return PierreDto.fromEntity(possedee(id, userId));
    }

    public PierreForm preparerModification(Long id, Long userId) {
        Pierre p = possedee(id, userId);
        verifierBrouillon(p);
        PierreForm f = new PierreForm();
        f.setNomInscrit(p.getNomInscrit());
        f.setMessage(p.getMessage());
        f.setMissionId(p.getMission() == null ? null : p.getMission().getId());
        f.setPublier(p.isPublier());
        return f;
    }

    @Transactional
    public Long enregistrer(Long id, PierreForm form, Long userId) {
        User u = utilisateurs.verrouiller(userId).orElseThrow();
        Pierre p = id == null ? new Pierre() : possedee(id, userId);
        verifierBrouillon(p);
        Misssion m = missions.findById(form.getMissionId()).orElseThrow(() -> new IllegalArgumentException("Mission not found"));
        if (!m.reservable()) throw new IllegalArgumentException("Mission unavailable");
        p.setNomInscrit(form.getNomInscrit().strip());
        p.setMessage(form.getMessage().strip());
        p.setMission(m);
        p.setProprietaire(u);
        p.setPublier(form.isPublier());
        return pierres.save(p).getId();
    }

    @Transactional
    public void supprimer(Long id, Long userId) {
        utilisateurs.verrouiller(userId).orElseThrow();
        Pierre p = possedee(id, userId);
        verifierBrouillon(p);
        pierres.delete(p);
    }

    public List<PierreDto> panier(Long userId) {
        return pierres.findByProprietaireIdAndDansPanierTrueOrderById(userId).stream().map(PierreDto::fromEntity).toList();
    }

    @Transactional
    public void changerPanier(Long id, Long userId, boolean ajouter) {
        utilisateurs.verrouiller(userId).orElseThrow();
        Pierre p = possedee(id, userId);
        verifierBrouillon(p);
        if (ajouter && (p.getMission() == null || !p.getMission().reservable())) throw new IllegalArgumentException("Mission unavailable");
        p.setDansPanier(ajouter);
    }

    public List<PierreDto> edifice() {
        return pierres.findByStatutAndPublierTrueOrderByIdDesc(StatutPierre.DEPOSEE).stream()
                .map(PierreDto::fromEntity).toList();
    }

    private Pierre possedee(Long id, Long userId) {
        Pierre p = pierres.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (p.getProprietaire() == null || !p.getProprietaire().getId().equals(userId)) throw new AccessDeniedException("Access denied");
        return p;
    }

    private void verifierBrouillon(Pierre p) {
        if (p.getStatut() != StatutPierre.BROUILLON) throw new IllegalArgumentException("Purchased stones cannot be changed");
    }
}