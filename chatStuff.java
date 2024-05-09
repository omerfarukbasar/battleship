import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;

public class chatStuff {

    // Individual thread that handles each client
    public static void listenToMsg(JTextArea textArea) {
        int gamePort = 8989;
        try (DatagramSocket udpSocket = new DatagramSocket(gamePort)) {
            byte[] buffer = new byte[256];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            System.out.println("Listening for broadcast messages on port " + gamePort);

            // Read messages from client while connection is still open
            while (true) {
                // Log the received message
                udpSocket.receive(packet);
                String receivedMessage = new String(packet.getData(), 0, packet.getLength());
                textArea.append("Opponent: " + receivedMessage + "\n");
            }

        }
        catch (SocketException e) {System.out.println("host Socket error: " + e.getMessage());}
        catch (IOException e) {System.out.println("host IO error: " + e.getMessage());}
    }
    // Allows for sending a message upon hitting send
    public static void sendMessage(String message, String serverIP, JTextArea chat) {
        try {
            // Broadcast address for the network
            InetAddress broadcastAddress = InetAddress.getByName(serverIP);
            DatagramSocket udpSocket = new DatagramSocket();
            udpSocket.setBroadcast(true);

            // Broadcast message to find the server
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcastAddress, 8989);
            udpSocket.send(packet);
            chat.append("You: " + message + "\n");
        } catch (IOException e) {System.err.println("Join IO error: " + e.getMessage());}
    }

}
