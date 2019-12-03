package battleship;

import java.util.Vector;

class Boats
{
    public Vector<Integer> barche; //vettore contenente le barche della battaglia navale indicate dalla loro dimensione -> 2,2,2,3,3,4,5
    int id; //id del giocatore che possiede le barche

    public Boats(int id)
    {
        this.id=id;
        barche = new Vector<>();

        barche.add(2);
        barche.add(2);
        barche.add(2);

        barche.add(3);
        barche.add(3);

        barche.add(4);

        barche.add(5);
    }

    public int getBarca(int i)
    {
        return barche.elementAt(i);
    }

    public Vector<Integer> getBarche()
    {
        return barche;
    }

    public int size()
    {
        return barche.size();
    }
}