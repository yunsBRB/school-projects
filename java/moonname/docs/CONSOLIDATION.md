# Consolidation depuis catalogue-fusees

MoonName est la destination proposée. L'ancien dépôt est conservé jusqu'à validation de la migration.

Source : [catalogue-fusees au commit 0fae26e2715485c84578d3849e27050efbe594ef](https://github.com/yunsBRB/catalogue-fusees/tree/0fae26e2715485c84578d3849e27050efbe594ef).

| Ancienne version | MoonName |
|---|---|
| Utilisateur et son email | User, sans email |
| Mission | Misssion |
| Lignes de panier dédiées | Pierres et indicateur dansPanier |
| CommandeService.confirmer | CommandeService.acheter |
| Dépôt via CommandeService | Dépôt via MissionService |
| Routes /admin/... | Routes d'ajout/modification protégées par SecurityConfig |

Ne pas remplacer les sources en bloc et ne pas importer le fichier H2 local de l'ancien dépôt.

Les deux scénarios de TransactionsTest sont adaptés : deux clients pour une dernière place ; rollback d'un achat avec une mission indisponible. Les fixtures sont propres à chaque test.

Les neuf scénarios de ParcoursTest sont adaptés : pages et formulaires, conservation du prix/vidange du panier, double confirmation, stock insuffisant, rôles/propriété/CSRF, validation et rôle à l'inscription, achat via contrôleur, astronaute affecté, CRUD administrateur.

Les pages missions et pierres ont été corrigées et MissionDto expose les champs déjà attendus par ses vues. Le filtre de recherche de l'ancien catalogue n'est pas réintroduit : le contrôleur actuel ne le propose pas.

Avant archivage de catalogue-fusees : exécuter les tests adaptés, valider les verrous avec PostgreSQL et tester le parcours depuis un clone propre. Un run H2 réussi ne suffit pas à valider PostgreSQL.
