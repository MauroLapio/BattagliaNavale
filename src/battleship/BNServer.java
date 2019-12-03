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

class Game
{
    public Board board = new Board();

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

    /*
    public boolean boardFilledUp() //metodo non ancora in uso
    {
        return Arrays.stream(board).allMatch(p -> p != null);
    }*/

    public synchronized void move(int x, int y, String Position, int Boat)
    {
        boolean Controllo = false;
        
        /*
        if (player != currentPlayer)
        {
            throw new IllegalStateException("Not your turn");
        }
        else if (player.opponent == null)
        {
            throw new IllegalStateException("You don't have an opponent yet");
        }
        else*/ /*
        if (player != currentPlayer)
        {
        throw new IllegalStateException("Not your turn");
        }
        else if (player.opponent == null)
        {
        throw new IllegalStateException("You don't have an opponent yet");
        }
        else*/ switch (Position) {
        //Posizionamento Nord
            case "nord":
                for (int i=0; i < Boat; i++)
                {
                    board.setPos(x, y+i, 1);
                }   break;
        
        //Posizionamento Est
            case "est":
                for (int i=0; i < Boat; i++)
                {
                    board.setPos(x+i, y, 1);
                }   break;
        //Posizionamento Sud
            case "sud":
                for (int i=0; i < Boat; i++)
                {
                    board.setPos(x, y-i, 1);
                }   break;
        //Posizionamento Ovest
            case "ovest":
                for (int i=0; i < Boat; i++)
                {
                    board.setPos(x-i, y, 1);
                }   break;
            default:
                break;
        }
    }
    
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
        
        private boolean ControlloCollisioni(int X, int Y, int Position, int Boat)
        {
            if (Position == 0) //Posizionamento Nord
            {
                for (int i=0; i < Boat; i++)
                {
                    board.setPos(X, Y+i, 1);
                }
                for(int j=0; j < 3;j++)
                {
                    for (int i=0; i < Boat+2; i++)
                    {
                        if(board.getPos(X-1+j, Y-1-i) == 1)
                        {
                            return true;
                        }
                    }
                }
            }
            else if (Position == 1) //Posizionamento Sud
            {
                for (int i=0; i < Boat; i++)
                {
                    board.setPos(X, Y-i, 1);
                }
                for(int j=0; j < 3;j++)
                {
                    for (int i=0; i < Boat+2; i++)
                    {
                        if(board.getPos(X-1+j, Y+1+i) == 1)
                        {
                            return true;
                        }
                    }
                }
            }
            else if (Position == 2) //Posizionamento Est
            {
                for (int i=0; i < Boat; i++)
                {
                    board.setPos(X+i, Y, 1);
                }
                for(int j=0; j < 3;j++)
                {
                    for (int i=0; i < Boat+2; i++)
                    {
                        if(board.getPos(X-1+i, Y-1+j) == 1)
                        {
                            return true;
                        }
                    }
                }
            }
            else if (Position == 3) //Posizionamento Ovest
            {
                for (int i=0; i < Boat; i++)
                {
                    board.setPos(X-i, Y, 1);
                }
                for(int j=0; j < 3;j++)
                {
                    for (int i=0; i < Boat+2; i++)
                    {
                        if(board.getPos(X-1+i, Y+1-j) == 1)
                        {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        boolean idflag=false; //controllo del giocatore
        private void processCommands()
        {
            while (input.hasNextLine())
            {
                String command = input.nextLine();
                int x = 0;
                int y = 0;
                String Position = "";
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
                               x = (Integer.parseInt(command.substring(2)));
                            }
                            if (command.startsWith("Y"))
                            {
                                y = (Integer.parseInt(command.substring(2)));
                            }
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
                               x = (Integer.parseInt(command.substring(2)));
                            }
                            if (command.startsWith("Y"))
                            {
                                y = (Integer.parseInt(command.substring(2)));
                            }
                        }
                    }
                    output.println('\n');
                    output.println("END"); //termina l'output di linee
                }                
                if(command.contentEquals("play"))
                {
                    String client; //stringa contenente input dal client

                    output.println(board.getBoard()); //output della tabella
                    while(!b1.barche.isEmpty())
                    {
                        int b = b1.barche.firstElement(); //barca da inserire nella tabella
                        output.println("Inserisci la posizione X della barca di dimensione " + b + " (massimo 21): ");
                        output.println("END");

                        client=input.nextLine();
                        while(Integer.valueOf(client) < 1 || Integer.valueOf(client) > 21) //controllo sull'input della posizione x
                        {
                            output.println("posizione barca non valida");
                            output.println("END");
                            client=input.nextLine();
                        }
                        x = Integer.valueOf(client); //posizione x da input
                        output.println("Hai inserito " + x);

                        output.println("Inserisci la posizione Y della barca di dimensione " + b + " (massimo 21): ");
                        output.println("END");
                        client=input.nextLine();
                        System.out.println("test input: "+Integer.valueOf(client)); //test dei comandi inviati al server
                        while(Integer.valueOf(client) < 1 || Integer.valueOf(client) > 21) //controllo sull'input della posizione Y
                        {
                            output.println("Posizione barca non valida");
                            output.println("END");
                            client=input.nextLine();
                        }
                        y = Integer.valueOf(client); //posizione y da input
                        output.println("Hai inserito " + y);
                        
                        output.println("Inserisci la direzione della barca di dimensione " + b + " (nord,sud,est,ovest): ");
                        output.println("END");
                        client=input.nextLine();
                        System.out.println("test input: "+client); //test dei comandi inviati al server
                        while(!client.equals("nord") && !client.equals("sud") && !client.equals("ovest") && !client.equals("est")) //controllo sull'input della direzione
                        {
                            System.out.println("test input: "+client);
                            output.println("Direzione barca non valida");
                            output.println("END");
                            client=input.nextLine();
                        }
                        Position = client; //direzione da input
                        output.println("Hai inserito " + Position);

                        //board.setPos(x, y, b);
                        move(x-1, y-1, Position, b); //inserisce la barca in posizione EST con gli appositi controlli
                        b1.barche.remove(0); //rimuove la barca dalla lista barche dell'utente
                    }

                    output.println(board.getBoard()); //stampa l'intera tabella
                }
            }
        }
    }
}

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