package mediatheque.models;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Emprunt {

    private final Media media;
    private final Membre membre;
    private final LocalDate dateEmprunt;
    private final LocalDate dateRetourPrevue;
    private LocalDate dateRetourReelle;

    public Emprunt(Media media, Membre membre) {
        this.media = media;
        this.membre = membre;
        this.dateEmprunt = LocalDate.now();
        this.dateRetourPrevue = dateEmprunt.plusDays(media.dureeEmpruntJours());media.marquerEmprunte();
    }

    public Media getMedia() { return media; }
    public Membre getMembre() { return membre; }
    public LocalDate getDateEmprunt() { return dateEmprunt; }
    public LocalDate getDateRetourPrevue() { return dateRetourPrevue; }
    public LocalDate getDateRetourReelle() { return dateRetourReelle; }

    public boolean estEnCours() {
        return dateRetourReelle == null;
    }

    public boolean estEnRetard() {
        return estEnCours() && LocalDate.now().isAfter(dateRetourPrevue);
    }

    public long joursDeRetard() {
        if (!estEnRetard()) return 0;
        return ChronoUnit.DAYS.between(dateRetourPrevue, LocalDate.now());
    }

    public long cloturer() {
        long retard = estEnRetard() ? joursDeRetard() : 0;
        this.dateRetourReelle = LocalDate.now();
        media.marquerDisponible();
        return retard;
    }

    @Override
    public String toString() {
        return media.getTitre() + " emprunté par " + membre.getPrenom() + " " + membre.getNom()
                + " le " + dateEmprunt + " - retour prévu " + dateRetourPrevue
                + (estEnCours() ? " (en cours)" : " (rendu le " + dateRetourReelle + ")");
    }
}