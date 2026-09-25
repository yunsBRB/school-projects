using System;
using System.Collections.Generic;
using System.Threading;

namespace HvM_2._0
{
    public class AreneCombat
    {
        private int _largeur = 15;
        private int _hauteur = 15;
        private Hero _joueur;
        private List<Monstre> _monstres = new List<Monstre>();
        private Pnj villageois;
        private Random _rnd = new Random();

        private int _puitsX = 13, _puitsY = 13;
        private int _offsetX;
        private int _offsetY;
        private int _bossKills = 0;

        public AreneCombat(Hero joueur)
        {
            _joueur = joueur;
            _joueur.X = 1; _joueur.Y = 1;
            villageois = new Pnj("VILLAGEOIS", "?", "Viens au puit pour augmenter tes PV") { X = 12, Y = 13 };
            CalculerCentrage();
            GenererMonstres(10);
        }

        private void CalculerCentrage()
        {
            int winWidth = Console.WindowWidth > 0 ? Console.WindowWidth : 100;
            int winHeight = Console.WindowHeight > 0 ? Console.WindowHeight : 30;
            _offsetX = (winWidth - (_largeur * 3)) / 2 - 5;
            if (_offsetX < 2) _offsetX = 5;
            _offsetY = (winHeight - _hauteur) / 2 - 2;
            if (_offsetY < 1) _offsetY = 2;
        }

        private void GenererMonstres(int quantite)
        {
            string[] types = { "Loup", "Orque", "Dragoon" };
            LancerdeDe d4 = new LancerdeDe(4);
            LancerdeDe d6 = new LancerdeDe(6);

            while (_monstres.Count < quantite)
            {
                int x = _rnd.Next(1, _largeur - 1);
                int y = _rnd.Next(1, _hauteur - 1);

                if ((x == 1 && y == 1) || (x == _puitsX && y == _puitsY) || (x == villageois.X && y == villageois.Y)) continue;
                if (_monstres.Exists(m => m.X == x && m.Y == y)) continue;

                string type = types[_rnd.Next(types.Length)];
                Monstre nouveau = new Monstre(type, "X", ConsoleColor.DarkRed) { X = x, Y = y };

                if (type == "Loup") { nouveau.BronzeLoot = d6.Lancer() * 2; }
                else if (type == "Orque") { nouveau.PV += 5; nouveau.ArgentLoot = d6.Lancer();nouveau.OrLoot = _rnd.Next(0, 2); }
                else if (type == "Dragon") { nouveau.PV += 5; nouveau.OrLoot = d4.Lancer() ; nouveau.PlatineLoot = _rnd.Next(1, 3); nouveau.DiamantLoot = _rnd.Next(0, 2); }
          
        

                _monstres.Add(nouveau);
            }

            int bossCrees = 0;
            while (bossCrees < 2 && _monstres.Count >= 2)
            {
                int index = _rnd.Next(_monstres.Count);
                if (!_monstres[index].EstBoss)
                {
                    _monstres[index].PromouvoirEnBoss();
                    bossCrees++;
                }
            }
        }

        public void BouclePrincipale()
        {
            Console.Clear();
            DessinerInterfaceComplete();

            while (_joueur.EstVivant && _monstres.Count > 0)
            {
                if (Console.KeyAvailable)
                {
                    ConsoleKeyInfo touche = Console.ReadKey(true);
                    int prevX = _joueur.X; int prevY = _joueur.Y;

                    if (touche.Key == ConsoleKey.UpArrow || touche.Key == ConsoleKey.Z) { if (_joueur.Y > 0) _joueur.Y--; }
                    else if (touche.Key == ConsoleKey.DownArrow || touche.Key == ConsoleKey.S) { if (_joueur.Y < _hauteur - 1) _joueur.Y++; }
                    else if (touche.Key == ConsoleKey.LeftArrow || touche.Key == ConsoleKey.Q) { if (_joueur.X > 0) _joueur.X--; }
                    else if (touche.Key == ConsoleKey.RightArrow || touche.Key == ConsoleKey.D) { if (_joueur.X < _largeur - 1) _joueur.X++; }

                    NettoyerPositionEcran(prevX, prevY);

                    if (_joueur.X == _puitsX && _joueur.Y == _puitsY)
                    {
                        _joueur.SeReposer();
                        AfficherNotificationCombat("PV et Force  +5");
                    }

                    DessinerEntites();
                    foreach (var m in _monstres)
                    { Console.SetCursorPosition(_offsetX + (m.X * 3), _offsetY + m.Y);
                        Console.ForegroundColor = m.Couleur;
                        Console.Write($"[{m.Icone}]");
                    }

                    MettreAJourPanneauStatistiques();
                    VerifierRencontreCombat();
                }
                Thread.Sleep(20);
            }
            DeclencherFinDePartie();
        }

        private void DessinerInterfaceComplete()
        {
            CalculerCentrage();
            for (int y = 0; y < _hauteur; y++)
            {
                Console.SetCursorPosition(_offsetX, _offsetY + y);
                Console.ForegroundColor = ConsoleColor.DarkGray;
                for (int x = 0; x < _largeur; x++) Console.Write("  .");
            }
            MettreAJourPanneauStatistiques();
            DessinerEntites();
        }

        private void NettoyerPositionEcran(int x, int y)
        {
            Console.SetCursorPosition(_offsetX + (x * 3), _offsetY + y);
            Console.ForegroundColor = ConsoleColor.DarkGray;
            Console.Write("   .");
        }

        private void DessinerEntites()
        {
            Console.SetCursorPosition(_offsetX + (_puitsX * 3), _offsetY + _puitsY);
            Console.ForegroundColor = ConsoleColor.Blue; Console.Write("[░]");

            Console.SetCursorPosition(_offsetX + (villageois.X * 3), _offsetY + villageois.Y);
            Console.ForegroundColor = villageois.Couleur; Console.Write($"[?]");

           

            Console.SetCursorPosition(_offsetX + (_joueur.X * 3), _offsetY + _joueur.Y);
            Console.ForegroundColor = ConsoleColor.Gray; Console.Write("[");
            Console.ForegroundColor = _joueur.Couleur; Console.Write(_joueur.Icone);
            Console.ForegroundColor = ConsoleColor.Gray; Console.Write("]");
            Console.ResetColor();
        }

        private void MettreAJourPanneauStatistiques()
        {
            int colStats = _offsetX + (_largeur * 3) + 6;

            Console.SetCursorPosition(colStats, _offsetY + 1);
            Console.ForegroundColor = ConsoleColor.White;
            Console.Write($"{_joueur.Nom} [{_joueur.Icone}]              ");

            Console.SetCursorPosition(colStats, _offsetY + 3);
            Console.ForegroundColor = ConsoleColor.Red;
            Console.Write(" PV   ♥ : [");
            int barresPv = _joueur.SanteMax > 0 ? (int)(((double)_joueur.PV / _joueur.SanteMax) * 12) : 0;
            Console.Write(new string('■', Math.Max(0, barresPv)) + new string(' ', Math.Max(0, 12 - barresPv)) + "] ");

            Console.SetCursorPosition(colStats, _offsetY + 4);
            Console.ForegroundColor = ConsoleColor.DarkYellow;
            Console.Write(" FORCE : [");
            int barresForce = Math.Min(12, _joueur.Force / 2);
            Console.Write(new string('■', Math.Max(0, barresForce)) + new string(' ', Math.Max(0, 12 - barresForce)) + "] ");

       

            Console.SetCursorPosition(colStats, _offsetY + 7);
            Console.ForegroundColor = ConsoleColor.Gray;
            Console.Write($"Kills : {10 - _monstres.Count}   ");

            int ligSac = _offsetY + _hauteur + 2;
            Console.SetCursorPosition(_offsetX, ligSac);
            Console.ForegroundColor = ConsoleColor.Cyan; Console.Write($" ♦Diamant: {_joueur.Diamant}  ");
            Console.ForegroundColor = ConsoleColor.DarkCyan; Console.Write($"○Platine: {_joueur.Platine}  ");
            Console.ForegroundColor = ConsoleColor.Yellow; Console.Write($"○ Or: {_joueur.Or}  ");
            Console.ForegroundColor = ConsoleColor.DarkGray; Console.Write($"○Argent: {_joueur.Argent}  ");
            Console.ForegroundColor = ConsoleColor.DarkYellow; Console.Write($"○Bronze: {_joueur.Bronze}  ");
            

            if (Math.Abs(_joueur.X - villageois.X) <= 1 && Math.Abs(_joueur.Y - villageois.Y) <= 1)
            {
                Console.SetCursorPosition(_offsetX, ligSac + 2);
                Console.ForegroundColor = ConsoleColor.Gray;
                Console.Write($"{villageois.Nom} : \"{villageois.Replique}\"");
            }
            else
            {
                Console.SetCursorPosition(_offsetX, ligSac + 2);
                Console.Write(new string(' ', 80));
            }
            Console.ResetColor();
        }

        private void VerifierRencontreCombat()
        {
            Monstre cible = _monstres.Find(m => Math.Abs(m.X - _joueur.X) + Math.Abs (m.Y - _joueur.Y) == 1);
            if (cible != null)
            {
                AfficherNotificationCombat("combat en cours ....");
                while (_joueur.EstVivant && cible.EstVivant)
                {
                    _joueur.Frapper(cible);
                    if (cible.EstVivant) cible.Frapper(_joueur);
                    MettreAJourPanneauStatistiques();
                    Thread.Sleep(150);
                }
                while (Console.KeyAvailable)
                {
                    Console.ReadKey(true);
                }
                if (_joueur.EstVivant)
                {
                    bool etaitBoss = cible.EstBoss;
                    _joueur.Diamant += cible.DiamantLoot;
                    _joueur.Platine += cible.PlatineLoot;
                    _joueur.Or += cible.OrLoot;
                    _joueur.Argent += cible.ArgentLoot;
                    _joueur.Bronze += cible.BronzeLoot;

                    _joueur.ResetBuff();
                    _monstres.Remove(cible);

                    Console.Clear();
                    DessinerInterfaceComplete();
                    if(etaitBoss)
                    { _bossKills++;


                        _joueur.Diamant += 2;
                        _joueur.Platine += 5;
                        Console.WriteLine("\n\n\n\n\n\n");
                        AfficherNotificationCombat($"tu as kill {_bossKills} boss sur 2 (+ 2 diamant, + 5 platine)");
                    }
                    else
                    { 
                    AfficherNotificationCombat($"t'as kill un {cible.Nom} !!");
                    }

                }
            }
        }

        private void AfficherNotificationCombat(string texte)
        {
            int ligSac = _offsetY + _hauteur + 2;
            Console.SetCursorPosition( _offsetX, ligSac + 4);
            Console.ForegroundColor = ConsoleColor.White;
            Console.Write(texte.PadRight(80));
            Console.ResetColor();
        }

        public void AfficherTexteCentre(string texte, ConsoleColor couleur)
        {
            Console.ForegroundColor = couleur;
            int width = Console.WindowWidth > 0 ? Console.WindowWidth : 80;
            int espace = (width - texte.Length) / 2;
            if (espace < 0) espace = 0;
            Console.WriteLine(new string(' ', espace) + texte);
            Console.ResetColor();
        }

        private void DeclencherFinDePartie()
        {
            Console.Clear();
            Console.WriteLine("\n\n\n");
            if (_joueur.EstVivant)
            {
                AfficherTexteCentre("██╗   ██╗██╗ ██████╗████████╗ ██████╗ ██╗██████╗ ███████╗", ConsoleColor.Green);
                AfficherTexteCentre("██║   ██║██║██╔════╝╚══██╔══╝██╔═══██╗██║██╔══██╗██╔════╝", ConsoleColor.Green);
                AfficherTexteCentre("██║   ██║██║██║        ██║   ██║   ██║██║██████╔╝█████╗  ", ConsoleColor.Green);
                AfficherTexteCentre("╚██╗ ██╔╝██║██║        ██║   ██║   ██║██║██╔══██╗██╔══╝  ", ConsoleColor.Green);
                AfficherTexteCentre(" ╚████╔╝ ██║╚██████╗   ██║   ╚██████╔╝██║██║  ██║███████╗", ConsoleColor.Green);
                AfficherTexteCentre("  ╚═══╝  ╚═╝ ╚══════╝   ╚═╝    ╚═════╝ ╚═╝╚═╝  ╚═╝╚══════╝", ConsoleColor.Green);
                Console.WriteLine("\n");
                AfficherTexteCentre("GG t'as win !", ConsoleColor.White);
            }
            else
            {
                AfficherTexteCentre(" ██████╗  █████╗ ███╗   ███╗███████╗      ██████╗ ██╗   ██╗███████╗██████╗ ", ConsoleColor.Red);
                AfficherTexteCentre("██╔════╝ ██╔══██╗████╗ ████║██╔════╝     ██╔═══██╗██║   ██║██╔════╝██╔══██╗", ConsoleColor.Red);
                AfficherTexteCentre("██║  ███╗███████║██╔████╔██║█████╗       ██║   ██║██║   ██║█████╗  ██████╔╝", ConsoleColor.Red);
                AfficherTexteCentre("██║   ██║██╔══██║██║╚██╔╝██║██╔══╝       ██║   ██║╚██╗ ██╔╝██╔══╝  ██╔══██╗", ConsoleColor.Red);
                AfficherTexteCentre("╚██████╔╝██║  ██║██║ ╚═╝ ██║███████╗     ╚██████╔╝ ╚████╔╝ ███████╗██║  ██║", ConsoleColor.Red);
                AfficherTexteCentre(" ╚═════╝ ╚═╝  ╚═╝╚═╝     ╚═╝╚══════╝      ╚═════╝   ╚═══╝  ╚══════╝╚═╝  ╚═╝", ConsoleColor.Red);
                Console.WriteLine("\n");
                AfficherTexteCentre("Looseur", ConsoleColor.White);
            }
            Console.WriteLine("\n\n");
            AfficherTexteCentre($"Diamant : {_joueur.Diamant} - Platine : {_joueur.Platine} - Or : {_joueur.Or} - Argent {_joueur.Argent} - Bronze {_joueur.Bronze}", ConsoleColor.Gray);
            Console.ReadKey(true);
        }
    }
}