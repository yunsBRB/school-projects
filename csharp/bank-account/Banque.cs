namespace ExoCompteCourant
{
    public class Banque
    {
        public string Nom { get; set; } = null;

        
        public Dictionary<string, Courant> Comptes = new Dictionary<string, Courant>();

      
        public Courant this[string numero]
        {
            get
            {
                Courant c;
                Comptes.TryGetValue(numero, out c);
                return c;


            }
            set { Comptes[numero] = value; }
        }


        public void Ajouter(Courant compte)
        {
            Comptes.Add(compte.Numero, compte);
            Console.WriteLine($"Compte {compte.Numero} ajouté.");
        }

        public void Supprimer(string numero)
        {
            if (Comptes.ContainsKey(numero))
            {
                Comptes.Remove(numero);
                Console.WriteLine($"Compte {numero} supprimé.");
            }
            else
            {
                Console.WriteLine($"Compte {numero} introuvable.");
            }
        }
    }
}

