using System;
using System.Collections.Generic;
using System.Text;

namespace ExoCompteCourant
{
    public class Courant
    {
        public string Numero { get; set; }
        public Personne Titulaire { get; set; }
        public double LigneDeCredit { get; set; }

        private double _solde;
        public double Solde
        {
            get { return _solde; }
        }

        public void Depot(double montant)
        {
            _solde += montant;
            Console.WriteLine($"Depot de {montant}€ — Solde : {_solde}€");
        }

        public void Retrait(double montant)
        {
            if (_solde - montant < -LigneDeCredit)
            {
                Console.WriteLine("Retrait refusé — limite de crédit atteinte.");
            }
            else
            {
                _solde -= montant;
                Console.WriteLine($"Retrait de {montant}€ — Solde : {_solde}€");
            }
        }
    }
}

