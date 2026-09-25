package mediatheque.models;

import mediatheque.enums.Genre;

public class Dvd extends Media {

    private String realisateur;
    private int dureeMinutes;

    public Dvd(String titre, int anneeSortie, Genre genre, String realisateur, int dureeMinutes) {
        super(titre, anneeSortie, genre);
        this.realisateur = realisateur;
        setDureeMinutes(dureeMinutes);
    }

    public String getRealisateur() { return realisateur; }

    public int getDureeMinutes() { return dureeMinutes; }
    public void setDureeMinutes(int dureeMinutes) {
        if (dureeMinutes <= 0) throw new IllegalArgumentException("dureeMinutes doit être > 0.");
        this.dureeMinutes = dureeMinutes;
    }

    @Override
    public int dureeEmpruntJours() { return 7; }

    @Override
    public String typeLibelle() { return "DVD"; }

    @Override
    public String toString() {
        return super.toString() + " - réalisé par " + realisateur + " (" + dureeMinutes + " min)";
    }

    @Override
    public boolean equals(Object o) { return super.equals(o); }

    @Override
    public int hashCode() { return super.hashCode(); }
}