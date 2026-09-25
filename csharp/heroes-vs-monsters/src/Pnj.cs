using System;

namespace HvM_2._0
{
    public class Pnj : Personnage
    {
        public string Replique { get; set; }

        public Pnj(string nom, string icone, string replique) : base(nom, icone)
        {
            Replique = replique;
            Couleur = ConsoleColor.White;
        }
    }
}