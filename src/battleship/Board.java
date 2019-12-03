package battleship;

public class Board
{
    public int[][] board;

    public Board ()
    {
        this.board = new int[21][21];

        int i, j; //contatori
        for (i=0; i<21; i++)
        {
            for (j=0;j<21;j++)
            {
                board[i][j] = 0;
            }
        }
    }

    public synchronized String getBoard() //ritorna tutte le caselle della tabella
    {
        String ret = "";
        int i,j;

        for (i=21-1; i>=0; i--)
        {
            if(i>=9)
            {
                ret += i+1 + " "; //aggiunge il numero della linea della tabella con uno spazio
            }
            else
            {
                ret += i+1 + "  "; //aggiunge il numero della linea della tabella con due spazi
            }
            for (j=0;j<21;j++)
            {
                ret += String.valueOf(board[j][i]) + ' '; //aggiunta del valore all'interno della casella alla stringa da ritornare
            }
            ret+='\n';
        }
        ret+="*  A B C D E F G H I L M N O P Q R S T U V Z";
        return ret;
    }

    public synchronized int getPos(int x, int y)
    {
        return board[x][y];
    }

    public synchronized void setPos(int x, int y, int val)
    {
        if (x<=20 && x>=0)
        {
            if(y<=20 && y>=0)
            {
                board[x][y] = val;
            }
        }
        else
        {
            System.out.println("Errore developer! setPos");
        }
    }
}