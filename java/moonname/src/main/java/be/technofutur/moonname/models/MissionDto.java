package be.technofutur.moonname.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import be.technofutur.moonname.enumss.StatutMission;

public record MissionDto(
        Long id,
        String nom,
        LocalDate dateDepart,
        BigDecimal prix,
        int placesDisponibles,
        String fuseeNom,
        String astronauteNom,
        StatutMission statut) {

    public static MissionDto fromEntity(Misssion m) {
        return new MissionDto(
                m.getId(),
                m.getNom(),
                m.getDateDepart(),
                m.getPrix(),
                m.getPlacesDisponibles(),
                m.getFusee().getNom(),
                m.getAstronaute() == null ? "Unassigned" : m.getAstronaute().getUsername(),
                m.getStatut()
        );
    }
}
