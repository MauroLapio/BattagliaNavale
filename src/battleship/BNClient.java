package battleship;

import java.awt.Font;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Scanner;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * A client for a multi-player tic tac toe game. Loosely based on an example in
 * Deitel and Deitel’s “Java How to Program” book. For this project I created a
 * new application-level protocol called TTTP (for Tic Tac Toe Protocol), which
 * is entirely plain text. The messages of TTTP are:
 *
 * Client -> Server
 *     MOVE <n>
 *     QUIT
 *
 * Server -> Client
 *     WELCOME <char>
 *     VALID_MOVE
 *     OTHER_PLAYER_MOVED <n>
 *     OTHER_PLAYER_LEFT
 *     VICTORY
 *     DEFEAT
 *     TIE
 *     MESSAGE <text>
 */
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
        try
        {
            System.out.println("Connessione in corso...");
            out.println("id1");
            
            id = in.nextLine();
            System.out.println("Benvenuto giocatore "+ id);
            System.out.println("Navi disponibili:");
            Vector<String> barche = new Vector<>();
            
            barche.add(in.nextLine());
            
            for(int i=0;i<barche.size();i++)
            {
                System.out.println("barca " + i+1 + ": " + barche.elementAt(i)+'\n');
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            socket.close();
        }
    }

    static class Square extends JPanel
    {
        JLabel label = new JLabel();

        public Square()
        {
            setBackground(Color.white);
            setLayout(new GridBagLayout());
            label.setFont(new Font("Arial", Font.BOLD, 40));
            add(label);
        }

        public void setText(char text)
        {
            label.setForeground(text == 'X' ? Color.BLUE : Color.RED);
            label.setText(text + "");
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
        client.play();
    }
}

