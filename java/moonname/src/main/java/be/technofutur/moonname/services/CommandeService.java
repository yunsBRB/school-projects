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
import java.util.Map;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommandeService {
    private final CommandeRepository commandes;
    private final PierreRepository pierres;
    private final MissionRepository missions;
    private final UserRepository utilisateurs;

    @Transactional
    public Long acheter(Long userId) {
        User u = utilisateurs.verrouiller(userId).orElseThrow();
        List<Pierre> panier = pierres.findByProprietaireIdAndDansPanierTrueOrderById(userId);
        if (panier.isEmpty()) throw new IllegalArgumentException("Cart is empty");
        Map<Long, Integer> quantites = new TreeMap<>();
        for (Pierre p : panier) {
            if (p.getStatut() != StatutPierre.BROUILLON || p.getMission() == null) throw new IllegalArgumentException("Invalid stone");
            quantites.merge(p.getMission().getId(), 1, Integer::sum);
        }
        Map<Long, Misssion> verrouillees = new TreeMap<>();
        for (var q : quantites.entrySet()) {
            Misssion m = missions.verrouiller(q.getKey()).orElseThrow();
            entityManager.refresh(m);
            if (!m.reservable() || m.getPlacesDisponibles() < q.getValue()) throw new IllegalArgumentException("Not enough places: " + m.getNom());
            m.setPlacesDisponibles(m.getPlacesDisponibles() - q.getValue());
            verrouillees.put(m.getId(), m);
        }
        Commande c = new Commande();
        c.setUtilisateur(u);
        for (Pierre p : panier) {
            Billet b = new Billet();
            b.setCommande(c);
            b.setPierre(p);
            b.setNomInscrit(p.getNomInscrit());
            b.setMessage(p.getMessage());
            b.setPrix(verrouillees.get(p.getMission().getId()).getPrix());
            c.getBillets().add(b);
            p.setStatut(StatutPierre.EN_PREPARATION);
            p.setDansPanier(false);
        }
        return commandes.save(c).getId();
    }

    public List<CommandeDto> lister(Long userId) {
        return commandes.findByUtilisateurIdOrderByIdDesc(userId).stream().map(CommandeDto::fromEntity).toList();
    }

    public CommandeDto trouver(Long id, Long userId) {
        Commande c = commandes.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!c.getUtilisateur().getId().equals(userId)) throw new AccessDeniedException("Access denied");
        return CommandeDto.fromEntity(c);
    }

    private final jakarta.persistence.EntityManager entityManager;
}