import javax.swing.*;
import java.io.IOException;
import java.net.*;

public class chatStuff {
    // Port to be used for chat communication
    private static final int PORT = 8989;

    // Listens to messages sent in the chat using the UDP for data transmission
    public static void listenToMsg(JTextArea textArea) {
        // Setup datagram socket
        try (DatagramSocket udpSocket = new DatagramSocket(PORT)) {
            // Setup to receive packets
            byte[] buffer = new byte[256];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            System.out.println("Listening for chat messages on port " + PORT);

            // Read messages from opponent while connection is still open
            while (true) {
                // Unpack message
                udpSocket.receive(packet);
                String receivedMessage = new String(packet.getData(), 0, packet.getLength());

                // Update chat panel to reflect new messages
                textArea.append("Opponent: " + receivedMessage + "\n");
                textArea.setCaretPosition(textArea.getDocument().getLength());
            }
        }
        catch (SocketException e) {System.out.println("Chat Listen Socket error: " + e.getMessage());}
        catch (IOException e) {System.out.println("Chat Listen IO error: " + e.getMessage());}
    }

    // Sends messages to opponent using UDP for data transmission
    public static void sendMsg(String message, String serverIP, JTextArea chat) {
        // Setup datagram socket
        try (DatagramSocket udpSocket = new DatagramSocket()) {
            // Setup broadcasting to opponent
            InetAddress broadcastAddress = InetAddress.getByName(serverIP);
            udpSocket.setBroadcast(true);

            // Pack your message and send to opponent
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcastAddress, PORT);
            udpSocket.send(packet);

            // Add your message to chat panel history
            chat.append("You: " + message + "\n");
            chat.setCaretPosition(chat.getDocument().getLength());
        }
        catch (IOException e) {System.err.println("Chat Send IO error: " + e.getMessage());}
    }
}
