using System;
using System.Threading;

namespace HvM_2._0
{
    class Program
    {
        static void Main(string[] args)
        {
            Console.CursorVisible = false;
            Console.OutputEncoding = System.Text.Encoding.UTF8;

            AfficherIntroductionAnimee();

            while (Console.KeyAvailable) Console.ReadKey(true);
            Console.Clear();

            CentrerEcriture("NOMMEZ VOTRE HÉROS", ConsoleColor.Red);
            Console.WriteLine("\n");
            string nom = FaireSaisieCentree(" Mon héro : ");
            if (string.IsNullOrWhiteSpace(nom)) nom = "";

            string iconeHero = nom.Substring(0, 1).ToUpper();

            Console.Clear();
            Console.WriteLine("\n\n\n");
            CentrerEcriture("██████████████████████████████████████", ConsoleColor.Red);
            Console.WriteLine("\n\n");
            CentrerEcriture("1 GUERRIER ", ConsoleColor.Gray);
            CentrerEcriture("2 SORCIER", ConsoleColor.Gray);
            CentrerEcriture("3 ARCHER  ", ConsoleColor.Gray);
            CentrerEcriture("4 ASSASSIN   ", ConsoleColor.Gray);
            Console.WriteLine("\n");
            CentrerEcriture("FAIS TON CHOIX", ConsoleColor.White);

            Console.WriteLine("\n\n\n");
            CentrerEcriture("██████████████████████████████████████", ConsoleColor.Red);

            Hero joueur = null;
            while (joueur == null)
            {
                Console.WriteLine("\n");
                string choix = FaireSaisieCentree(" mon choix : ");

                if (choix == "1") joueur = new Hero(nom, iconeHero, 0, 2);
                else if (choix == "2") joueur = new Hero(nom, iconeHero, 1, 1);
                else if (choix == "3") joueur = new Hero(nom, iconeHero, 1, 0);
                else if (choix == "4") joueur = new Hero(nom, iconeHero, 0, 1);
                else
                {
                    CentrerEcriture("Choix invalide (tapez 1-4).", ConsoleColor.DarkRed);
                    Thread.Sleep(800);
                }
            }

            AreneCombat session = new AreneCombat(joueur);
            session.BouclePrincipale();
        }

        private static void AfficherIntroductionAnimee()
        {
            string[] titre = new string[]
            {
                "█ █ █▀▀ █▀█ █▀█ █▀   █ █ █▀▀   █▀▄▀█ █▀█ █▄ █ █▀ ▀█▀ █▀█ █▀▀ █▀",
                "█▀█ ██▄ █▀▄ █▄█ ▄█   ▀▄▀ ▄█    █ ▀ █ █▄█ █ ▀█ ▄█  █  █▀▄ ██▄ ▄█"
            };

            ConsoleColor[] couleurs = { ConsoleColor.Red, ConsoleColor.DarkRed, ConsoleColor.Magenta, ConsoleColor.DarkMagenta, ConsoleColor.Cyan };

            for (int i = 0; i < 4; i++)
            {
                if (Console.KeyAvailable) { Console.ReadKey(true); break; }
                Console.Clear();
                Console.WriteLine("\n\n\n\n");

                Console.ForegroundColor = couleurs[i % couleurs.Length];
                foreach (string ligne in titre)
                {
                    int espace = (Console.WindowWidth - ligne.Length) / 2;
                    if (espace < 0) espace = 0;
                    Console.WriteLine(new string(' ', espace) + ligne);
                }

                Console.ForegroundColor = ConsoleColor.Gray;
                Console.WriteLine("\n\n");
                CentrerEcriture("BIENVENUE DANS SHOREWOOD", ConsoleColor.White);
                CentrerEcriture("[ Appuyez sur une touche pour commencer ]", ConsoleColor.DarkGray);

                Thread.Sleep(800);
            }
            Console.ResetColor();
        }

        private static void CentrerEcriture(string texte, ConsoleColor couleur)
        {
            Console.ForegroundColor = couleur;
            int width = Console.WindowWidth > 0 ? Console.WindowWidth : 80;
            int espace = (width - texte.Length) / 2;
            if (espace < 0) espace = 0;
            Console.WriteLine(new string(' ', espace) + texte);
            Console.ResetColor();
        }

        private static string FaireSaisieCentree(string invitation)
        {
            int width = Console.WindowWidth > 0 ? Console.WindowWidth : 80;
            int espace = (width - (invitation.Length + 15)) / 2;
            if (espace < 0) espace = 0;
            Console.Write(new string(' ', espace) + invitation);
            Console.ForegroundColor = ConsoleColor.Cyan;
            string saisie = Console.ReadLine();
            Console.ResetColor();
            return saisie ?? "";
        }
    }
}