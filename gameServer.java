import javax.swing.*;
import java.awt.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.io.IOException;

public class gameServer {
    // Individual thread that handles each client
    public static void listenToMoves(JTextArea textArea, JButton[][] yourBoardButtons, int[][] playerBoard) {
        int gamePort = 8991;
        try (DatagramSocket udpSocket = new DatagramSocket(gamePort)) {
            byte[] buffer = new byte[256];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            System.out.println("Listening for game moves on port " + gamePort);

            // Read messages from client while the connection is still open
            while (true) {
                // Log the received message
                udpSocket.receive(packet);
                String receivedMessage = new String(packet.getData(), 0, packet.getLength());
                String[] coords = receivedMessage.split(",");
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);

                textArea.append("Opponent: " + receivedMessage + "\n");
                textArea.setCaretPosition(textArea.getDocument().getLength());

                // Update the board based on whether it's a hit or a miss
                if (playerBoard[x][y] == 1) {
                    playerBoard[x][y] = 2; // Hit
                    yourBoardButtons[x][y].setBackground(Color.RED); // Mark as hit
                } else {
                    playerBoard[x][y] = 3; // Miss
                    yourBoardButtons[x][y].setBackground(Color.WHITE); // Mark as miss
                }
            }

        } catch (SocketException e) {System.out.println("game listen Socket error: " + e.getMessage());
        } catch (IOException e) {System.out.println("game listen IO error: " + e.getMessage());}
    }

    // Allows for sending a move upon hitting send
    public static void sendMove(String message, String serverIP, JTextArea chat) {
        try {
            InetAddress broadcastAddress = InetAddress.getByName(serverIP);
            DatagramSocket udpSocket = new DatagramSocket();
            udpSocket.setBroadcast(true);

            // Broadcast the move to find the server
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcastAddress, 8991);
            udpSocket.send(packet);
            chat.append("You: " + message + "\n");
            chat.setCaretPosition(chat.getDocument().getLength());
        } catch (IOException e) {System.err.println("game send IO error: " + e.getMessage());}
    }
}
