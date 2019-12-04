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
    public boolean idflag=false; //controllo del giocatore

    Player currentPlayer;
    Player opponent;
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
        else*/ switch (Position) 
        {
        //Posizionamento Nord
            case "n","N", "nord", "Nord":
                for (int i=0; i < Boat; i++)
                {
                    board.setPos(x, y+i, 1);
                }   break;
        
        //Posizionamento Est
            case "e":
                for (int i=0; i < Boat; i++)
                {
                    board.setPos(x+i, y, 1);
                }   break;
        //Posizionamento Sud
            case "s":
                for (int i=0; i < Boat; i++)
                {
                    board.setPos(x, y-i, 1);
                }   break;
        //Posizionamento Ovest
            case "o":
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
        Boats b;
        char id;
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
            
            this.b = new Boats(id);
            
            if (id == '1')
            {
                currentPlayer = this;
            }
            else if (id == '2')
            {
                opponent = this;
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
                if(command.contentEquals("id")) //controlla se il giocatore è 1 o 2
                {
                    System.out.println("flag: "+ idflag);
                    if(idflag == false)
                    {
                        idflag = true;
                        System.out.println("change: "+idflag);
                        output.println(1);
                    }
                    else
                    {
                        output.println(2);
                    }
                    
                    for(int i=0;i<b.size();i++)//Creato inserimento del client con rispettive risposte
                    {
                        System.out.println(b.getBarca(i));
                        output.println(b.getBarca(i));
                        Boat = (b.getBarca(i));
                    }
                    output.println('\n');
                    output.println("END"); //termina l'output di linee
                }                
                if(command.contentEquals("play"))
                {
                    String client; //stringa contenente input dal client

                    output.println(board.getBoard()); //output della tabella
                    while(!b.barche.isEmpty())
                    {
                        int barca = b.barche.firstElement(); //barca da inserire nella tabella
                        output.println("Inserisci la posizione X della barca di dimensione " + barca + " (massimo 21): ");
                        output.println("END");

                        client=input.nextLine();
                        System.out.println("test input: "+client);
                        while(Integer.valueOf(client) < 1 || Integer.valueOf(client) > 21) //controllo sull'input della posizione x
                        {
                            output.println("Posizione barca non valida");
                            output.println("END");
                            client=input.nextLine();
                            System.out.println("test input: "+client);
                        }
                        x = Integer.valueOf(client); //posizione x da input
                        output.println("Hai inserito " + x);

                        output.println("Inserisci la posizione Y della barca di dimensione " + barca + " (massimo 21): ");
                        output.println("END");
                        client=input.nextLine();
                        System.out.println("test input: "+client); //test dei comandi inviati al server
                        while(Integer.valueOf(client) < 1 || Integer.valueOf(client) > 21) //controllo sull'input della posizione Y
                        {
                            output.println("Posizione barca non valida");
                            output.println("END");
                            client=input.nextLine();
                            System.out.println("test input: "+client);
                        }
                        y = Integer.valueOf(client); //posizione y da input
                        output.println("Hai inserito " + y);
                        
                        output.println("Inserisci la direzione della barca di dimensione " + barca + " (nord,sud,est,ovest): ");
                        output.println("END");
                        client=input.nextLine();
                        System.out.println("test input: "+client); //test dei comandi inviati al server
                        while(!client.equals("nord") && !client.equals("sud") && !client.equals("ovest") && !client.equals("est")) //controllo sull'input della direzione
                        {
                            output.println("Direzione barca non valida");
                            output.println("END");
                            client=input.nextLine();
                            System.out.println("test input: "+client);
                        }
                        Position = client; //direzione da input
                        output.println("Hai inserito " + Position);

                        //board.setPos(x, y, b);
                        move(x-1, y-1, Position, barca); //inserisce la barca in posizione EST con gli appositi controlli
                        b.barche.remove(0); //rimuove la barca dalla lista barche dell'utente
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