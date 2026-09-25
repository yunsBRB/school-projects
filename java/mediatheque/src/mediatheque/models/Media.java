package mediatheque.models;

import mediatheque.enums.Genre;
import java.time.LocalDate;

public abstract class Media {

    private static int nextId = 1;

    private final int id;
    private String titre;
    private int anneeSortie;
    private Genre genre;
    private boolean disponible;

    public Media(String titre, int anneeSortie, Genre genre) {
        setTitre(titre);
        setAnneeSortie(anneeSortie);
        setGenre(genre);
        this.id = nextId++;
        this.disponible = true;
    }

    public final int getId() { return id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) {
        if (titre == null || titre.isBlank()) {
            throw new IllegalArgumentException("Le titre peut pas être vide");
        }
        this.titre = titre;
    }

    public int getAnneeSortie() { return anneeSortie;
    }
    public void setAnneeSortie(int anneeSortie) {
        int anneeCourante = LocalDate.now().getYear();
        if (anneeSortie < 1900 || anneeSortie > anneeCourante) {
            throw new IllegalArgumentException("Année invalide.");
        }
        this.anneeSortie = anneeSortie;
    }

    public Genre getGenre() { return genre; }
    public void setGenre(Genre genre) {
        if (genre == null) {
            throw new IllegalArgumentException("Le genre ne peut pas être null.");
        }
        this.genre = genre;
    }

    public boolean isDisponible() { return disponible; }

    public void marquerEmprunte() { this.disponible = false; }
    void marquerDisponible() { this.disponible = true; }

    public abstract int dureeEmpruntJours();
    public abstract String typeLibelle();

    @Override
    public String toString() {
        return "[" + id + "] " + typeLibelle() + " - " + titre + " (" + anneeSortie + ") - "
                + genre + " - " + (disponible ? "dispo" : "emprunté");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Media)) return false;
        return id == ((Media) o).id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}