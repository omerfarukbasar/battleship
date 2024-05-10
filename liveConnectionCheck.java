import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class liveConnectionCheck {
    // Port to be used for connection check
    private static final int PORT = 8988;

    // Protocol for connecting server, initiated by hitting connect
    public static void clientStatus(String opponentIP) {
        try (Socket socket = new Socket(opponentIP, PORT);){
            System.out.println("Started 'connection check' socket on Port " + PORT  +"\n");
            // Setup data streams
            DataInputStream fromServer = new DataInputStream(socket.getInputStream());
            DataOutputStream toServer = new DataOutputStream(socket.getOutputStream());

            while (true)
                fromServer.readUTF();
        }
        catch (IOException e) {System.err.println("Connection error: " + e.getMessage());}
        catch (Exception e) {System.err.println("Other Connection error: " + e.getMessage());}
    }

    // Protocol for starting up the server
    public static void hostStatus() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Started 'connection check' socket on Port " + PORT  +"\n");

            Socket socket = serverSocket.accept();

            // Setup streams
            DataInputStream fromClient = new DataInputStream(socket.getInputStream());
            DataOutputStream toClient = new DataOutputStream(socket.getOutputStream());

            while (true)
                fromClient.readUTF();
        }
        catch (IOException e) {System.err.println("Server error: " + e.getMessage());}
        catch (Exception e) {System.out.println("Other server error: " + e.getMessage());}
    }
}
