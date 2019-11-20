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

    public synchronized void move(int X, int Y, int Position, int Boat, Player player)
    {
        if (player != currentPlayer)
        {
            throw new IllegalStateException("Not your turn");
        }
        else if (player.opponent == null)
        {
            throw new IllegalStateException("You don't have an opponent yet");
        }
        else if (Position == 0) //Posizionamento Nord
        for (int i=0; i < Boat; i++)
        {
            board[X][Y-i] = 1;
        }
        else if (Position == 1) //Posizionamento Sud
        for (int i=0; i < Boat; i++)
        {
            board[X][Y+i] = 1;
        }
        else if (Position == 2) //Posizionamento Est
        for (int i=0; i < Boat; i++)
        {
            board[X+i][Y] = 1;
        }
        else if (Position == 3) //Posizionamento Ovest
        for (int i=0; i < Boat; i++)
        {
            board[X-i][Y] = 1;
        }
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
                int X = 0;
                int Y = 0;
                int Position = 0;
                int Boat = 0;
                
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
                    
                    if(this == currentPlayer)
                    {
                        for(int i=0;i<b1.size();i++)//Creato inserimento del client con rispettive risposte
                        {
                            System.out.println(b1.getBarca(i));
                            output.println(b1.getBarca(i));
                            Boat = (b1.getBarca(i));
                            if (command.startsWith("QUIT"))
                            {
                                return;
                            }
                            if (command.startsWith("X"))
                            {
                               X = (Integer.parseInt(command.substring(2)));
                            }
                            if (command.startsWith("Y"))
                            {
                                Y = (Integer.parseInt(command.substring(2)));
                            }
                            if (command == "NORTH") 
                            {
                                Position = 0;
                            }
                            if (command == "SOUTH")
                            {
                                Position = 1;
                            }
                            if (command == "EAST")
                            {
                                Position = 2;
                            }
                            if (command == "WEST")
                            {
                                Position = 3;
                            }
                            processMoveCommand(X, Y, Position, Boat);
                        }
                    }
                    else
                    {
                        for(int i=0;i<b2.size();i++)
                        {
                            System.out.println(b2.getBarca(i));
                            output.println(b2.getBarca(i));
                            if (command.startsWith("QUIT"))
                            {
                                return;
                            }
                            if (command.startsWith("X"))
                            {
                               X = (Integer.parseInt(command.substring(2)));
                            }
                            if (command.startsWith("Y"))
                            {
                                Y = (Integer.parseInt(command.substring(2)));
                            }
                            if (command == "NORTH")
                            {
                                Position = 0;
                            }
                            if (command == "SOUTH")
                            {
                                Position = 1;
                            }
                            if (command == "EAST")
                            {
                                Position = 2;
                            }
                            if (command == "WEST")
                            {
                                Position = 3;
                            }
                            processMoveCommand(X, Y, Position, Boat);
                        }
                    }
                    output.println("END"); //termina l'output di linee
                }                
                if(command.contentEquals("play"))
                {
                    output.println("  1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21\n1\n2\n3\n4\n5\n6\n7\n8\n9\n10\n11\n12\n13\n14\n15\n16\n17\n18\n19\n20\n21");
                }
            }
        }

        private void processMoveCommand(int X, int Y, int Position, int Boat)
        {
            try
            {
                move(X, Y, Position, Boat, this);
                output.println("VALID_MOVE");//Rimossa la risposta su dove ha mosso il giocatore
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