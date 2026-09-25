package be.technofutur.moonname.models;

import java.math.BigDecimal;
import be.technofutur.moonname.enumss.StatutPierre;

public record BilletDto(Long id, String nomInscrit, String message, BigDecimal prix,
                        String missionNom, StatutPierre statut) {
    public static BilletDto fromEntity(Billet b) {
        return new BilletDto
                (b.getId(), b.getNomInscrit(), b.getMessage(), b.getPrix(),
                b.getPierre().getMission().getNom(), b.getPierre().getStatut());
    }
}