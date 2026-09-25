package mediatheque.models;

import mediatheque.enums.Genre;

public class Livre extends Media {

    private String auteur;
    private int nbPages;
    private String isbn;

    public Livre(String titre, int anneeSortie, Genre genre, String auteur, int nbPages, String isbn) {
        super(titre, anneeSortie, genre);
        setAuteur(auteur);
        setNbPages(nbPages);
        this.isbn = isbn;
    }

    public String getAuteur() { return auteur; }
    public void setAuteur(String auteur) { this.auteur = auteur; }

    public int getNbPages() { return nbPages; }
    public void setNbPages(int nbPages) {
        if (nbPages <= 0) throw new IllegalArgumentException("nbPages doit être > 0.");
        this.nbPages = nbPages;
    }

    public String getIsbn() { return isbn; }

    @Override
    public int dureeEmpruntJours() { return 21; }

    @Override
    public String typeLibelle() { return "Livre"; }

    @Override
    public String toString() {
        return super.toString() + " - " + auteur + " (" + nbPages + " pages)";
    }

    @Override
    public boolean equals(Object o) { return super.equals(o); }

    @Override
    public int hashCode() { return super.hashCode(); }
}