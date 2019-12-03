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

    public String getBoard() //ritorna tutte le caselle della tabella
    {
        String ret = "";
        int i,j;

        for (i=0; i<21; i++)
        {
            for (j=0;j<21;j++)
            {
                ret += String.valueOf(board[i][j]) + ' '; //aggiunta del valore all'interno della casella alla stringa da ritornare
            }
            ret+='\n';
        }

        return ret;
    }

    public int getPos(int x, int y)
    {
        return board[x][y];
    }

    public void setPos(int x, int y, int val)
    {
        if (x<=21 && x>=0)
        {
            if(y<=21 && y>=0)
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