package mediatheque.utils;

import java.util.List;
import java.util.Scanner;

public class ConsoleUtils {

    private static final Scanner sc = new Scanner(System.in);

    private ConsoleUtils() {}

    public static int lireEntier(String message) {
        while (true) {
            System.out.print(message);
            String saisie = sc.nextLine();
            try {
                return Integer.parseInt(saisie.trim());
            } catch (NumberFormatException e) {
                System.out.println("Nombre invalide, réessayez.");
            }
        }
    }

    public static String lireTexte(String message) {
        System.out.print(message);
        return sc.nextLine().trim();
    }

    public static boolean lireOuiNon(String message) {
        while (true) {
            System.out.print(message + " (o/n) ");
            String saisie = sc.nextLine().trim().toLowerCase();
            if (saisie.equals("o")) return true;
            if (saisie.equals("n")) return false;
            System.out.println("Répondez par o ou n.");
        }
    }

    public static void afficherTitre(String titre) {
        String ligne = "=".repeat(40);
        System.out.println(ligne);
        System.out.println(titre);
        System.out.println(ligne);
    }

    public static void afficherListe(List<?> elements) {
        if (elements.isEmpty()) {
            System.out.println("(aucun élément)");
            return;
        }
        for (Object e : elements) {
            System.out.println(e);
        }
    }
}