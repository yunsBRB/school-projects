# Médiathèque Java

Application console pédagogique pour gérer des médias, membres et emprunts. Les données sont stockées dans des collections en mémoire et réinitialisées à chaque lancement. Aucune base SQL n'est utilisée.

## Lancer

Installer un JDK 17 ou supérieur, puis depuis le dossier `java/mediatheque` :

```powershell
javac -encoding UTF-8 -d build "@sources.txt"
java -cp build Main
```

Les mêmes commandes fonctionnent sous Linux/macOS. Le programme charge des données de démonstration. Choisir 0 pour quitter.

## Organisation

- `src/Main.java` : menu console.
- `src/mediatheque/models/` : médias, membres et emprunts.
- `src/mediatheque/services/` : règles métier et stockage en mémoire.
- `src/mediatheque/utils/` : saisie console.
- `sources.txt` : liste des sources à compiler ; la mettre à jour lors d'un ajout.

Les fichiers compilés et paramètres IDE locaux sont exclus de Git. Le workflow GitHub Actions compile le projet et vérifie le démarrage puis la sortie du menu ; ce contrôle n'est pas une suite complète de tests métier.

## À compléter

Tests d'emprunt, retour, indisponibilité, retard et validation des saisies. Une persistance SQL serait une évolution distincte.
