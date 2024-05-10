import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class liveConnectionCheck {
    // Port to be used for connection check
    private static final int PORT = 8988;

    // Protocol for connecting server, initiated by hitting connect
    public static void clientStatus(String opponentIP, JFrame parent) {
        try (Socket socket = new Socket(opponentIP, PORT);){
            System.out.println("Started 'connection check' socket on Port " + PORT  +"\n");
            // Setup data streams
            DataInputStream fromServer = new DataInputStream(socket.getInputStream());

            while (true)
                fromServer.readUTF();
        }
        catch (Exception e) {
            System.err.println("Opponent Disconnected");
            JOptionPane.showMessageDialog(parent, "Opponent Disconnected");
            System.exit(0);
        }
    }

    // Protocol for starting up the server
    public static void hostStatus(JFrame parent) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Started 'connection check' socket on Port " + PORT  +"\n");

            Socket socket = serverSocket.accept();

            // Setup streams
            DataInputStream fromClient = new DataInputStream(socket.getInputStream());

            while (true)
                fromClient.readUTF();
        }
        catch (Exception e) {
            System.err.println("Opponent Disconnected");
            JOptionPane.showMessageDialog(parent, "Opponent Disconnected");
            System.exit(0);
        }
    }
}
