package be.technofutur.moonname.models;

import be.technofutur.moonname.enumss.StatutPierre;
//pour transporter les données de la pierre vers le HTML
import java.math.BigDecimal;

public record PierreDto(Long id, String nomInscrit, String message, StatutPierre statut,
                        String missionNom, BigDecimal prix, boolean dansPanier, boolean publier) {
    public static PierreDto fromEntity(Pierre p) {
        return new PierreDto(p.getId(), p.getNomInscrit(), p.getMessage(), p.getStatut(),
                p.getMission() == null ? "No mission" : p.getMission().getNom(),
                p.getMission() == null ? BigDecimal.ZERO : p.getMission().getPrix(),
                p.isDansPanier(), p.isPublier());
    }
}