using System;
using System.Collections.Generic;
using System.Threading;

namespace HvM_2._0
{
    public class LancerdeDe
    {
        private static Random _rnd = new Random();
        public int Faces { get; set; }

        public LancerdeDe(int faces) => Faces = faces;
        public int Lancer() => _rnd.Next(1, Faces + 1);
    }
}