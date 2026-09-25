package mediatheque.models;

import mediatheque.enums.Genre;
import mediatheque.enums.Plateforme;

public class JeuVideo extends Media {

    private Plateforme plateforme;
    private int pegi;

    public JeuVideo(String titre, int anneeSortie, Genre genre, Plateforme plateforme, int pegi) {
        super(titre, anneeSortie, genre);
        this.plateforme = plateforme;
        setPegi(pegi);
    }

    public Plateforme getPlateforme() { return plateforme; }

    public int getPegi() { return pegi; }
    public void setPegi(int pegi) {
        if (pegi != 3 && pegi != 7 && pegi != 12 && pegi != 16 && pegi != 18) {
            throw new IllegalArgumentException("PEGI invalide");
        }
        this.pegi = pegi;
    }

    @Override
    public int dureeEmpruntJours() { return 14; }

    @Override
    public String typeLibelle() { return "Jeu video"; }

    @Override
    public String toString() {
        return super.toString() + " - " + plateforme + " - PEGI " + pegi;
    }

    @Override
    public boolean equals(Object o) { return super.equals(o); }

    @Override
    public int hashCode() { return super.hashCode(); }
}