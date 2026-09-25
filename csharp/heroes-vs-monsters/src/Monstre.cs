using System;

namespace HvM_2._0
{
    public class Monstre : Personnage
    {
        public int DiamantLoot { get; set; }
        public int PlatineLoot { get; set; }
        public int OrLoot { get; set; }
        public int ArgentLoot { get; set; }
        public int BronzeLoot { get; set; }
        
        public bool EstBoss { get; set; }

        public Monstre(string nom, string icone, ConsoleColor couleur) : base(nom, icone)
        {
            Couleur = couleur;
            SanteMax = 2;
            PV = 2;
        }

        public void PromouvoirEnBoss()
        {
            EstBoss = true;
            Nom = "BOSS" + Nom;
            Icone = "!";
            Couleur = ConsoleColor.Magenta;
            SanteMax = 5;
            PV = 5;
        }

        public override void Frapper(Personnage cible)
        {
            if (EstBoss)
            {

                    
                cible.PV = Math.Max(0, cible.PV - 3);
            }
            else
            { base.Frapper(cible);
            }
        }
    }
}