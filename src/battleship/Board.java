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
            for (j=0;j<21;j++)
            {
                ret += String.valueOf(board[j][i]) + ' '; //aggiunta del valore all'interno della casella alla stringa da ritornare
            }
            ret+='\n';
        }

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