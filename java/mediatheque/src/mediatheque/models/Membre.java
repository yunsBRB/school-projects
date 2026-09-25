package mediatheque.models;

import java.time.LocalDate;
import java.util.Objects;

public class Membre {

    private static int nextId = 1;

    private final int id;
    private String nom;
    private String prenom;
    private String email;
    private final LocalDate dateInscription;

    public Membre(String nom, String prenom, String email) {
        this.id = nextId++;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.dateInscription = LocalDate.now();
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getEmail() { return email; }
    public LocalDate getDateInscription() { return dateInscription; }

    @Override
    public String toString() {
        return "[" + id + "] " + prenom + " " + nom + " (" + email + ") - inscrit le " + dateInscription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Membre)) return false;
        return email.equals(((Membre) o).email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}