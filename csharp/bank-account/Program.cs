using ExoCompteCourant;


Console.Write("Nom : ");
string nom = Console.ReadLine();

Console.Write("Prénom : ");
string prenom = Console.ReadLine();

Console.Write("Date de naissance (ex: 15/05/1990) : ");
DateTime dateNaiss = DateTime.Parse(Console.ReadLine());

Personne p = new Personne
{
    Nom = nom,
    Prenom = prenom,
    DateNaiss = dateNaiss
};


Console.Write("Numéro de compte : ");
string numero = Console.ReadLine();

Console.Write("Ligne de crédit : ");
double ligneDeCredit = double.Parse(Console.ReadLine());

Courant compte = new Courant
{
    Numero = numero,
    Titulaire = p,
    LigneDeCredit = ligneDeCredit
};


Console.Write("\nMontant à déposer : ");
double depot = double.Parse(Console.ReadLine());
compte.Depot(depot);

Console.Write("Montant à retirer : ");
double retrait = double.Parse(Console.ReadLine());
compte.Retrait(retrait);

Console.WriteLine($"\nTitulaire : {compte.Titulaire.Prenom} {compte.Titulaire.Nom}");
Console.WriteLine($"Solde final : {compte.Solde}€");



Banque banque = new Banque();
banque.Nom = "BNP Paribas";

banque.Ajouter(compte);  // on ajoute le compte créé avant

// accès par numéro via l'indexeur
Courant c = banque[compte.Numero];
Console.WriteLine($"Compte trouvé : {c.Titulaire.Prenom} — Solde : {c.Solde}€");

// supprimer
banque.Supprimer(compte.Numero);

Console.WriteLine("Coucou");
Console.WriteLine("TU T'APPELLE COMMENT?????");