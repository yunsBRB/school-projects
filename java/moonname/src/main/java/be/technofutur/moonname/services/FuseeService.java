package be.technofutur.moonname.services;

import be.technofutur.moonname.models.*;
import be.technofutur.moonname.enumss.*;
import be.technofutur.moonname.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FuseeService {
    private final FuseeRepositories fuseeRepository;
    private final MissionRepository missions;

    public List<FuseeDto> lister() {
        return fuseeRepository.findAll().stream().map(FuseeDto::fromEntity).toList();
    }

    public FuseeDto trouver(Long id) {
        return fuseeRepository.findById(id).map(FuseeDto::fromEntity).orElse(null);
    }

    public FuseeForm preparerModification(Long id) {
        Fusee f = entite(id);
        FuseeForm form = new FuseeForm();
        form.setNom(f.getNom());
        form.setPays(f.getPays());
        return form;
    }

    @Transactional
    public void ajouter(FuseeForm form) {
        fuseeRepository.save(form.toEntity());
    }

    @Transactional
    public void modifier(Long id, FuseeForm form) {
        Fusee f = entite(id);
        f.setNom(form.getNom().strip());
        f.setPays(form.getPays().strip());
    }

    @Transactional
    public void supprimer(Long id) {
        Fusee f = entite(id);
        if (missions.existsByFuseeId(id)) throw new IllegalArgumentException("Rocket used by a mission");
        fuseeRepository.delete(f);
    }

    private Fusee entite(Long id) {
        return fuseeRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}