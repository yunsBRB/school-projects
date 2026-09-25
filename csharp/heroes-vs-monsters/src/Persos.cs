using System;

namespace HvM_2._0
{
    public abstract class Personnage
    {
        protected static LancerdeDe De6 = new LancerdeDe(6);
        protected static LancerdeDe De4 = new LancerdeDe(4);

        public string Nom { get; set; }
        public string Icone { get; set; }
        public int Endurance { get; set; }
        public int Force { get; set; }
        public int PV { get; set; }
        public int SanteMax { get; set; }
        public bool EstVivant => PV > 0;

        public int X { get; set; }
        public int Y { get; set; }
        public ConsoleColor Couleur { get; set; }

        protected Personnage(string nom, string icone)
        {
            Nom = nom;
            Icone = icone;
            Couleur = ConsoleColor.Gray;

            Endurance = GenererCarac();
            Force = GenererCarac();

            int modEnd = Endurance < 5 ? -1 : Endurance < 10 ? 0 : Endurance < 15 ? 1 : 2;
            SanteMax = Endurance + modEnd;
            PV = SanteMax;
        }

        private int GenererCarac()
        {
            return De6.Lancer() + De6.Lancer() + 3;
        }

        public virtual void Frapper(Personnage cible)
        {
            int degats = Force / 3;
            if (degats < 1) degats = 1;
            cible.PV = Math.Max(0, cible.PV - degats);
        }
    }
}