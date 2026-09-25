using System;

namespace HvM_2._0
{
    public class Hero : Personnage
    {
        public int Diamant { get; set; }
        public int Platine { get; set; }
        public int Or { get; set; }
        public int Argent { get; set; }
        public int Bronze { get; set; }
        public int Cuir { get; set; }

        private int _forceInitiale;

        public Hero(string nom, string icone, int bonusForce, int bonusEndurance) : base(nom, icone)
        {
            Force += bonusForce;
            Endurance += bonusEndurance;

            int modEnd = Endurance < 5 ? -1 : Endurance < 10 ? 0 : Endurance < 15 ? 1 : 2;
            SanteMax = Endurance + modEnd;
            PV = SanteMax;

            Couleur = ConsoleColor.White;
            _forceInitiale = Force;
        }

        public void SeReposer()
        {
            PV = SanteMax;
            Force = _forceInitiale + 5;
        }

        public void ResetBuff() => Force = _forceInitiale;
    }
}