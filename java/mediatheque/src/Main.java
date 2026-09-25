import mediatheque.configs.Config;
import mediatheque.enums.Genre;
import mediatheque.enums.Plateforme;
import mediatheque.models.*;
import mediatheque.services.Mediatheque;
import mediatheque.utils.ConsoleUtils;

import java.util.List;

public class Main {

    private static final Mediatheque mediatheque = new Mediatheque(Config.NOM_MEDIATHEQUE);

    public static void main(String[] args) {
        chargerDonneesDemo();

        int choix;
        do {
            ConsoleUtils.afficherTitre("MÉDIATHÈQUE " + Config.NOM_MEDIATHEQUE.toUpperCase());
            System.out.println("""
                    1. Ajouter un média
                    2. Lister le catalogue
                    3. Rechercher un média
                    4. Modifier un média
                    5. Supprimer un média
                    ----------------------------------------
                    6. Inscrire un membre
                    7. Emprunter
                    8. Rendre
                    9. Emprunts en cours
                    ----------------------------------------
                    10. Statistiques
                    0. Quitter""");

            choix = ConsoleUtils.lireEntier("Votre choix : ");

            switch (choix) {
                case 1 -> ajouterMedia();
                case 2 -> listerCatalogue();
                case 3 -> rechercherMedia();
                case 4 -> modifierMedia();
                case 5 -> supprimerMedia();
                case 6 -> inscrireMembre();
                case 7 -> emprunter();
                case 8 -> rendre();
                case 9 -> empruntsEnCours();
                case 10 -> statistiques();
                case 0 -> System.out.println("Au revoir !");
                default -> System.out.println("Choix invalide.");
            }
        } while (choix != 0);
    }

    // ---------- Options du menu ----------

    private static void ajouterMedia() {
        String type = ConsoleUtils.lireTexte("Type (livre/dvd/jeu) : ");
        String titre = ConsoleUtils.lireTexte("Titre : ");
        int annee = ConsoleUtils.lireEntier("Année : ");
        Genre genre = Genre.valueOf(ConsoleUtils.lireTexte("Genre " + List.of(Genre.values()) + " : ").toUpperCase());

        Media media = switch (type.toLowerCase()) {
            case "livre" -> new Livre(titre, annee, genre,
                    ConsoleUtils.lireTexte("Auteur : "),
                    ConsoleUtils.lireEntier("Nb pages : "),
                    ConsoleUtils.lireTexte("ISBN : "));
            case "dvd" -> new Dvd(titre, annee, genre,
                    ConsoleUtils.lireTexte("Réalisateur : "),
                    ConsoleUtils.lireEntier("Durée (min) : "));
            case "jeu" -> new JeuVideo(titre, annee, genre,
                    Plateforme.valueOf(ConsoleUtils.lireTexte("Plateforme " + List.of(Plateforme.values()) + " : ").toUpperCase()),
                    ConsoleUtils.lireEntier("PEGI : "));
            default -> null;
        };

        if (media == null) {
            System.out.println("Type inconnu.");
            return;
        }
        System.out.println(mediatheque.ajouterMedia(media) ? "Ajouté." : "Échec de l'ajout.");
    }

    private static void listerCatalogue() {
        ConsoleUtils.afficherListe(mediatheque.listerTous());
    }

    private static void rechercherMedia() {
        String motCle = ConsoleUtils.lireTexte("Mot-clé : ");
        ConsoleUtils.afficherListe(mediatheque.rechercher(motCle));
    }

    private static void modifierMedia() {
        int id = ConsoleUtils.lireEntier("ID du média : ");
        String titre = ConsoleUtils.lireTexte("Nouveau titre : ");
        int annee = ConsoleUtils.lireEntier("Nouvelle année : ");
        Genre genre = Genre.valueOf(ConsoleUtils.lireTexte("Nouveau genre : ").toUpperCase());
        System.out.println(mediatheque.modifierMedia(id, titre, annee, genre) ? "Modifié." : "ID introuvable.");
    }

    private static void supprimerMedia() {
        int id = ConsoleUtils.lireEntier("ID du média : ");
        System.out.println(mediatheque.supprimerMedia(id) ? "Supprimé." : "Suppression impossible.");
    }

    private static void inscrireMembre() {
        String nom = ConsoleUtils.lireTexte("Nom : ");
        String prenom = ConsoleUtils.lireTexte("Prénom : ");
        String email = ConsoleUtils.lireTexte("Email : ");
        System.out.println(mediatheque.inscrireMembre(new Membre(nom, prenom, email)) ? "Inscrit." : "Email déjà utilisé.");
    }

    private static void emprunter() {
        int idMedia = ConsoleUtils.lireEntier("ID du media : ");
        int idMembre = ConsoleUtils.lireEntier("ID du membre : ");
        boolean ok = mediatheque.emprunter(idMedia, idMembre);
        System.out.println(ok ? "OK - Emprunt enregistré." : "REFUS - Emprunt impossible.");
    }

    private static void rendre() {
        int idMedia = ConsoleUtils.lireEntier("ID du media : ");
        double retard = mediatheque.rendre(idMedia);
        if (retard < 0) {
            System.out.println("Aucun emprunt en cours pour ce média.");
        } else {
            System.out.println("Retour enregistré. Retard : " + (int) retard + " jours.");
        }
    }

    private static void empruntsEnCours() {
        ConsoleUtils.afficherListe(mediatheque.empruntsEnCours());
    }

    private static void statistiques() {
        System.out.println(mediatheque.genererStatistiques());
    }

    // ---------- Jeu de données démo ----------

    private static void chargerDonneesDemo() {
        mediatheque.ajouterMedias(
                new Livre("Le Nom de la Rose", 1980, Genre.POLICIER, "Umberto Eco", 512, "isbn1"),
                new Livre("Dune", 1965, Genre.SCIENCE_FICTION, "Frank Herbert", 688, "isbn2"),
                new Livre("Le Petit Prince", 1943, Genre.JEUNESSE, "A. de Saint-Exupéry", 96, "isbn3"),
                new Livre("Ils étaient dix", 1939, Genre.POLICIER, "Agatha Christie", 256, "isbn4"),
                new Livre("Fondation", 1951, Genre.SCIENCE_FICTION, "Isaac Asimov", 320, "isbn5"),
                new Livre("Le Chien des Baskerville", 1902, Genre.POLICIER, "A. Conan Doyle", 307, "isbn6"),
                new Livre("L'Aventure ambiguë", 1961, Genre.AVENTURE, "C. Hamidou Kane", 192, "isbn7"),
                new Dvd("Blade Runner", 1982, Genre.SCIENCE_FICTION, "Ridley Scott", 117),
                new Dvd("Seven", 1995, Genre.POLICIER, "David Fincher", 127),
                new Dvd("Indiana Jones", 1981, Genre.AVENTURE, "Steven Spielberg", 115),
                new JeuVideo("Zelda", 2017, Genre.AVENTURE, Plateforme.SWITCH, 12),
                new JeuVideo("FIFA 24", 2023, Genre.ACTION, Plateforme.PS5, 3)
        );

        mediatheque.inscrireMembre(new Membre("Dupont", "Alice", "alice.dupont@mail.com"));
        mediatheque.inscrireMembre(new Membre("Martin", "Bob", "bob.martin@mail.com"));
        mediatheque.inscrireMembre(new Membre("Durand", "Chloé", "chloe.durand@mail.com"));
        mediatheque.inscrireMembre(new Membre("Bernard", "David", "david.bernard@mail.com"));
    }
}