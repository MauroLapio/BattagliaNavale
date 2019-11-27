package battleship;

import java.util.Scanner;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

public class BNClient
{
    public String id;
    private Socket socket;
    private Scanner in;
    private PrintWriter out;

    public BNClient(String serverAddress) throws Exception
    {
        socket = new Socket(serverAddress, 58901);
        in = new Scanner(socket.getInputStream());
        out = new PrintWriter(socket.getOutputStream(), true);
    }
    
    public void play() throws Exception
    {
        String input = ""; 
        try
        {
            Scanner keys = new Scanner(System.in); //input da tastiera dell'utente
            out.println("play"); //indica al server che il giocatore è entrato
            
            while(!input.equals("END")) //output della tabella
            {
                input=in.nextLine(); //input DAL server
                if(!input.equals("END")) //controllo per non eseguire output di "END"
                {
                    System.out.println(input); //output AL client
                }
            }
            
            out.println(keys.nextLine());
        }
        catch (Exception e)
        {
            System.out.println("Errore: "+ e);
        }
        finally
        {
            socket.close();
        }
    }
    public static void main(String[] args) throws Exception
    {
        if (args.length != 1)
        {
            System.err.println("Pass the server IP as the sole command line argument");
            return;
        }
        BNClient client = new BNClient(args[0]);
        
        System.out.println("Connessione in corso...");
        client.out.println("id1");

        client.id = client.in.nextLine();
        System.out.println("Benvenuto giocatore "+ client.id);
        System.out.println("Navi disponibili:");

        while(client.in.hasNextLine())
        {
            String ln = client.in.nextLine();
            if(ln.equals("END"))
            {
                break;
            }
            System.out.println(ln);
        }
        client.play();
    }
}

