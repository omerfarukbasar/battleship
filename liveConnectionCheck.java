import javax.swing.*;
import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class liveConnectionCheck {
    // Port to be used for connection check
    private static final int PORT = 8988;

    // Connection check done by the client connecting to the host
    public static void clientStatus(String opponentIP, JFrame parent) {
        // Setup socket for listening
        try (Socket socket = new Socket(opponentIP, PORT);){
            System.out.println("Started 'connection check' socket on Port " + PORT  +"\n");

            // Setup data stream
            DataInputStream fromServer = new DataInputStream(socket.getInputStream());

            while (true)
                fromServer.readUTF();
        }
        // Once host has closed application or disconnected
        catch (Exception e) {
            System.err.println("Opponent Disconnected");
            JOptionPane.showMessageDialog(parent, "Opponent Disconnected");
            System.exit(0);
        }
    }

    // Connection check done by the host
    public static void hostStatus(JFrame parent) {
        // Setup socket for listening
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Started 'connection check' socket on Port " + PORT  +"\n");

            // Connect client to host
            Socket socket = serverSocket.accept();

            // Setup stream
            DataInputStream fromClient = new DataInputStream(socket.getInputStream());

            while (true)
                fromClient.readUTF();
        }
        // Once client has closed application or disconnected
        catch (Exception e) {
            System.err.println("Opponent Disconnected");
            JOptionPane.showMessageDialog(parent, "Opponent Disconnected");
            System.exit(0);
        }
    }
}
