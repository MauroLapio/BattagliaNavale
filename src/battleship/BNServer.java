package battleship;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BNServer
{
    public static void main(String[] args) throws Exception
    {
        try (ServerSocket listener = new ServerSocket(58901))
        {
            System.out.println("BATTLESHIP");
            
            ExecutorService pool = Executors.newFixedThreadPool(2);
            while (true)
            {
                Game game = new Game();
                pool.execute(game.new Player(listener.accept(), '1'));
                pool.execute(game.new Player(listener.accept(), '2'));
            }
        }
    }
}

class Game
{
    public int[][] board = new int[21][21];

    Player currentPlayer;

    public boolean hasWinner()
    {
        /*return (board[0] != null && board[0] == board[1] && board[0] == board[2])
            || (board[3] != null && board[3] == board[4] && board[3] == board[5])
            || (board[6] != null && board[6] == board[7] && board[6] == board[8])
            || (board[0] != null && board[0] == board[3] && board[0] == board[6])
            || (board[1] != null && board[1] == board[4] && board[1] == board[7])
            || (board[2] != null && board[2] == board[5] && board[2] == board[8])
            || (board[0] != null && board[0] == board[4] && board[0] == board[8])
            || (board[2] != null && board[2] == board[4] && board[2] == board[6]
        );*/
        return false;
    }

    public boolean boardFilledUp()
    {
        return Arrays.stream(board).allMatch(p -> p != null);
    }

    public synchronized void move(int location, Player player)
    {
        if (player != currentPlayer)
        {
            throw new IllegalStateException("Not your turn");
        }
        else if (player.opponent == null)
        {
            throw new IllegalStateException("You don't have an opponent yet");
        }
        else if (board[location] != null)
        {
            throw new IllegalStateException("Cell already occupied");
        }
        //board[location] = currentPlayer;
        currentPlayer = currentPlayer.opponent;
    }

    /**
     * A Player is identified by a character mark which is either 'X' or 'O'.
     * For communication with the client the player has a socket and associated
     * Scanner and PrintWriter.
     */
    class Player implements Runnable
    {
        Boats b1;
        Boats b2;
        char id;
        Player opponent;
        Socket socket;
        Scanner input;
        PrintWriter output;

        public Player(Socket socket, char id)
        {
            this.socket = socket;
            this.id = id;
        }

        @Override
        public void run()
        {
            try
            {
                setup();
                processCommands();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (opponent != null && opponent.output != null)
                {
                    System.out.println("Other player left");
                }
                try
                {
                    socket.close();
                }
                catch (IOException e) {}
            }
        }

        private void setup() throws IOException
        {
            input = new Scanner(socket.getInputStream());
            output = new PrintWriter(socket.getOutputStream(), true);
            
            b1 = new Boats(1);
            b2 = new Boats(2);
            
            if (id == '1')
            {
                currentPlayer = this;
                System.out.println("Waiting for opponent to connect");
            }
            else if (id == '2')
            {
                opponent = currentPlayer;
                opponent.opponent = this;
                System.out.println("Insert the new boat coordinates");
            }
        }

        private void processCommands()
        {
            boolean idflag=false; //controllo del giocatore
            while (input.hasNextLine())
            {
                String command = input.nextLine();
                
                System.out.println(command);
                if(command.contentEquals("id1")) //controlla se il giocatore è 1 o 2
                {
                    if(idflag==false)
                    {
                        output.println(1);
                        idflag = true;
                    }
                    else
                    {
                        output.println(2);
                    }
                }
                
                if(this == currentPlayer)
                {
                    for(int i=0;i<b1.size();i++)
                    {
                        System.out.println(b1.getBarca(i));
                        output.println(b1.getBarca(i));
                    }
                }
                else
                {
                    for(int i=0;i<b2.size();i++)
                    {
                        System.out.println(b2.getBarca(i));
                        output.println(b2.getBarca(i));
                    }
                }
                
                output.println("END"); //termina l'output di linee
                
                
            }
        }

        private void processMoveCommand(int location)
        {
            try
            {
                move(location, this);
                output.println("VALID_MOVE");
                opponent.output.println("OPPONENT_MOVED " + location);
                if (hasWinner())
                {
                    output.println("VICTORY");
                    opponent.output.println("DEFEAT");
                }
                else if (boardFilledUp())
                {
                    output.println("TIE");
                    opponent.output.println("TIE");
                }
            }
            catch (IllegalStateException e)
            {
                output.println("MESSAGE " + e.getMessage());
            }
        }
    }
    
    
    class Boats
    {
        public Vector<Integer> barche;
        int id;
                
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
}