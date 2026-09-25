# MoonName

Application pédagogique Spring Boot MVC : réserver une inscription destinée à être déposée sur une pierre lunaire lors d'une mission fictive.

Le projet comprend des fusées, missions, utilisateurs, pierres, paniers et commandes. Les rôles CLIENT, ASTRONAUTE et ADMIN contrôlent les parcours. Il est en développement ; aucun paiement réel n'est intégré.

## Technologies

Java 17 minimum selon le `pom.xml`, Spring Boot 4.1.1, Thymeleaf, Spring Security, JPA/Hibernate et PostgreSQL. Le wrapper Maven est fourni.

## Lancement local

1. Préparer PostgreSQL sur `localhost:5434`, avec une base `moonname` et l'utilisateur `postgres`, conformément à `src/main/resources/application.yaml`.
2. Définir `DB_PASSWORD` avec le mot de passe local de cette base. Ne pas le versionner.
3. Depuis le dossier `java/moonname`, lancer sous PowerShell :

```powershell
$env:DB_PASSWORD = "VOTRE_MOT_DE_PASSE_LOCAL"
.\mvnw.cmd spring-boot:run
```

Sous Linux/macOS, définir la même variable dans l'environnement puis lancer `sh mvnw spring-boot:run`.

4. Ouvrir http://localhost:8080. L'inscription crée un CLIENT. Aucun compte ADMIN ou ASTRONAUTE de démonstration n'est fourni ; les préparer dans une base de développement dédiée.

La configuration utilise `ddl-auto: update` pour le développement. Elle ne remplace pas des migrations de production. Ne jamais utiliser une base contenant des données à conserver pour les tests.

## Architecture

Les contrôleurs MVC appellent les services, les services utilisent les repositories JPA et les vues consomment les DTO. Les entités sont dans `models`. Les noms `enumss` et `Misssion` sont conservés.

L'achat est transactionnel : missions verrouillées dans un ordre déterminé, stock décrémenté, prix copié dans les billets et panier vidé.

## Tests et consolidation

Consulter [le plan de consolidation](docs/CONSOLIDATION.md). Les anciens tests de `catalogue-fusees` utilisent d'autres entités et routes ; leur migration est progressive.

```powershell
.\mvnw.cmd test
```

Les tests utilisent une configuration H2 en mémoire isolée, définie uniquement dans src/test/resources. Ils ne remplacent pas une validation PostgreSQL des verrous.

## Limites connues

- Les onze scénarios métier de l'ancien projet sont adaptés au modèle actuel ; validation PostgreSQL encore nécessaire.
- Données de démonstration, migrations SQL et provisionnement des rôles à compléter.
- Les tests H2 valident les scénarios automatisés ; les verrous PostgreSQL nécessitent une validation spécifique.
