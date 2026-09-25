package mediatheque.services;

import mediatheque.models.Media;
import mediatheque.models.Membre;
import mediatheque.enums.Genre;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Mediatheque {

    private final String nom;
    private final Map<Integer, Media> catalogue = new HashMap<>();
    private final Map<Integer, Membre> membres = new HashMap<>();
    private final List<mediatheque.models.Emprunt> emprunts = new ArrayList<>();

    public Mediatheque(String nom) {
        this.nom = nom;
    }

    public String getNom() { return nom; }

    // ---------- B.1 CREATE ----------

    public boolean ajouterMedia(Media media) {
        if (media == null || catalogue.containsKey(media.getId())) return false;
        catalogue.put(media.getId(), media);
        return true;
    }

    public void ajouterMedias(Media... medias) {
        for (Media m : medias) {
            ajouterMedia(m);
        }
    }

    public boolean inscrireMembre(Membre membre) {
        if (membre == null) return false;
        boolean emailExiste = membres.values().stream()
                .anyMatch(m -> m.getEmail().equalsIgnoreCase(membre.getEmail()));
        if (emailExiste) return false;
        membres.put(membre.getId(), membre);
        return true;
    }

    // ---------- B.2 READ ----------

    public Media rechercherMediaParId(int id) {
        return catalogue.get(id);
    }

    public List<Media> listerTous() {
        return new ArrayList<>(catalogue.values());
    }

    public List<Media> rechercher(String motCle) {
        String mc = motCle.toLowerCase(Locale.ROOT);
        List<Media> resultat = new ArrayList<>();
        for (Media m : catalogue.values()) {
            if (m.getTitre().toLowerCase(Locale.ROOT).contains(mc)) resultat.add(m);
        }
        return resultat;
    }

    public List<Media> rechercher(Genre genre) {
        List<Media> resultat = new ArrayList<>();
        for (Media m : catalogue.values()) {
            if (m.getGenre() == genre) resultat.add(m);
        }
        return resultat;
    }

    public List<Media> listerDisponibles() {
        List<Media> resultat = new ArrayList<>();
        for (Media m : catalogue.values()) {
            if (m.isDisponible()) resultat.add(m);
        }
        return resultat;
    }

    public List<Media> listerParType(Class<?> type) {
        List<Media> resultat = new ArrayList<>();
        for (Media m : catalogue.values()) {
            if (type.isInstance(m)) resultat.add(m);
        }
        return resultat;
    }

    // ---------- B.3 UPDATE ----------

    public boolean modifierMedia(int id, String nouveauTitre, int nouvelleAnnee, Genre nouveauGenre) {
        Media m = catalogue.get(id);
        if (m == null) return false;
        m.setTitre(nouveauTitre);
        m.setAnneeSortie(nouvelleAnnee);
        m.setGenre(nouveauGenre);
        return true;
    }

    // ---------- B.4 DELETE ----------

    public boolean supprimerMedia(int id) {
        Media m = catalogue.get(id);
        if (m == null) return false;
        if (!m.isDisponible()) return false;
        catalogue.remove(id);
        return true;
    }

    public List<Membre> listerMembres() {
        return new ArrayList<>(membres.values());
    }

    public Membre rechercherMembreParId(int id) {
        return membres.get(id);
    }
    // ---------- C.1 Emprunter ----------

    public boolean emprunter(int idMedia, int idMembre) {
        Media media = catalogue.get(idMedia);
        if (media == null) return false;
        Membre membre = membres.get(idMembre);
        if (membre == null) return false;
        if (!media.isDisponible()) return false;
        if (empruntsEnCours(membre).size() >= mediatheque.configs.Config.MAX_EMPRUNTS_PAR_MEMBRE) return false;

        mediatheque.models.Emprunt emprunt = new mediatheque.models.Emprunt(media, membre);
        emprunts.add(emprunt);
        media.marquerEmprunte();
        return true;
    }

    // ---------- C.2 Rendre ----------

    public double rendre(int idMedia) {
        for (mediatheque.models.Emprunt e : emprunts) {
            if (e.getMedia().getId() == idMedia && e.estEnCours()) {
                return e.cloturer();
            }
        }
        return -1;
    }

    // ---------- C.3 Consulter et statistiques ----------

    public List<mediatheque.models.Emprunt> empruntsEnCours() {
        List<mediatheque.models.Emprunt> resultat = new ArrayList<>();
        for (mediatheque.models.Emprunt e : emprunts) {
            if (e.estEnCours()) resultat.add(e);
        }
        return resultat;
    }

    public List<mediatheque.models.Emprunt> empruntsEnCours(Membre membre) {
        List<mediatheque.models.Emprunt> resultat = new ArrayList<>();
        for (mediatheque.models.Emprunt e : emprunts) {
            if (e.estEnCours() && e.getMembre().equals(membre)) resultat.add(e);
        }
        return resultat;
    }

    public List<mediatheque.models.Emprunt> listerRetards() {
        List<mediatheque.models.Emprunt> resultat = new ArrayList<>();
        for (mediatheque.models.Emprunt e : emprunts) {
            if (e.estEnRetard()) resultat.add(e);
        }
        return resultat;
    }

    public String genererStatistiques() {
        int nbLivres = listerParType(mediatheque.models.Livre.class).size();
        int nbDvd = listerParType(mediatheque.models.Dvd.class).size();
        int nbJeux = listerParType(mediatheque.models.JeuVideo.class).size();
        int total = catalogue.size();
        int disponibles = listerDisponibles().size();
        int empruntes = total - disponibles;

        Map<Genre, Integer> compteurGenres = new HashMap<>();
        for (Media m : catalogue.values()) {
            compteurGenres.merge(m.getGenre(), 1, Integer::sum);
        }
        Genre genreTop = null;
        int maxCount = 0;
        for (Map.Entry<Genre, Integer> entry : compteurGenres.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                genreTop = entry.getKey();
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== Statistiques - ").append(nom).append(" ===\n");
        sb.append("Médias au catalogue : ").append(total)
                .append(" (").append(nbLivres).append(" livres, ")
                .append(nbDvd).append(" DVD, ").append(nbJeux).append(" jeux video)\n");
        sb.append("Disponibles : ").append(disponibles).append("\n");
        sb.append("Empruntés : ").append(empruntes).append("\n");
        sb.append("Membres inscrits : ").append(membres.size()).append("\n");
        if (genreTop != null) {
            sb.append("Genre le plus représenté : ").append(genreTop)
                    .append(" (").append(maxCount).append(" médias)");
        }
        return sb.toString();
    }
}