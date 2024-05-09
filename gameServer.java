import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import javax.swing.*;
import java.net.*;
import java.io.IOException;

public class gameServer {
    private static final int PORT = 8991;

    // Listen for moves and update the board
    public static void listenToMoves(JTextArea textArea, JButton[][] yourBoardButtons, int[][] playerBoard, boolean[] isMyTurn) {
        try (DatagramSocket udpSocket = new DatagramSocket(PORT)) {
            byte[] buffer = new byte[256];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            System.out.println("Listening for game moves on port " + PORT);

            // Read messages from client while the connection is still open
            while (true) {
                udpSocket.receive(packet);
                String receivedMessage = new String(packet.getData(), 0, packet.getLength());
                String[] coords = receivedMessage.split(",");
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);

                textArea.append("Opponent moved to: (" + x + "," + y + ")\n");
                textArea.setCaretPosition(textArea.getDocument().getLength());

                // Update the board based on whether it's a hit or miss
                if (playerBoard[x][y] == 1) {
                    playerBoard[x][y] = 2; // Hit
                    yourBoardButtons[x][y].setBackground(Color.RED); // Mark as hit
                } else {
                    playerBoard[x][y] = 3; // Miss
                    yourBoardButtons[x][y].setBackground(Color.WHITE); // Mark as miss
                }

                // Switch turn to the host/client depending on the current state
                isMyTurn[0] = true;
            }

        } catch (SocketException e) {
            System.out.println("Socket error: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO error: " + e.getMessage());
        }
    }

    // Allows sending a move upon hitting send
    public static void sendMove(String message, String serverIP, JTextArea chat) {
        try {
            InetAddress broadcastAddress = InetAddress.getByName(serverIP);
            DatagramSocket udpSocket = new DatagramSocket();
            udpSocket.setBroadcast(true);

            // Broadcast the move to find the server
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcastAddress, PORT);
            udpSocket.send(packet);
            chat.append("You: " + message + "\n");
            chat.setCaretPosition(chat.getDocument().getLength());
            udpSocket.close();
        } catch (IOException e) {
            System.err.println("Send IO error: " + e.getMessage());
        }
    }
}
