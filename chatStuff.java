import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class chatStuff {

    public static DataOutputStream hostIM(JTextArea textArea){
        int PORT = 4545;
        try (ServerSocket serverSocket = new ServerSocket(PORT);){
            // Accept into socket
            Socket socket = serverSocket.accept();

            // Setup streams
            DataInputStream fromClient = new DataInputStream(socket.getInputStream());
            DataOutputStream toClient = new DataOutputStream(socket.getOutputStream());

            // Separate into new thread
            new Thread(() -> listenToMsg(fromClient, textArea)).start();
            return toClient;
        }
        catch (IOException e) {System.err.println("Server error: " + e.getMessage());}
        return null;
    }
    // Individual thread that handles each client
    private static void listenToMsg(DataInputStream fromClient,JTextArea textArea) {
        try {
            // Read messages from client while connection is still open
            while (true) {
                String Msg = fromClient.readUTF();
                textArea.append("Opponent: " + Msg + "\n");
            }
        }
        catch (Exception e) {textArea.append("Opponent Disconnected \n");}
    }
    public static DataOutputStream clientIM(String serverIP, JTextArea textArea){
        int PORT = 4545;
        try (Socket socket = new Socket(serverIP, PORT)) {

            // Setup data streams
            DataInputStream fromServer = new DataInputStream(socket.getInputStream());
            DataOutputStream toServer = new DataOutputStream(socket.getOutputStream());

            // Keep reading in broadcasted messages from server
            new Thread(() -> listenToMsg(fromServer,textArea)).start();
            return toServer;
        }
        catch (IOException e) {System.err.println("Connection error: " + e.getMessage());}
        return null;
    }
    // Allows for sending a message upon hitting send
    public static void sendMessage(String message, DataOutputStream toServer, JTextArea chat) {
        try {
            // Encrypt message using communication key and send
            toServer.writeUTF(message);
            chat.append("You: " + message + "\n");
        }
        catch (IOException e) {System.err.println("Send error: " + e.getMessage());}
        catch (Exception e) {System.err.println("Other Send error: " + e.getMessage());}
    }

}
