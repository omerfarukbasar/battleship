import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class gameServer {
    // Port to be used for game communication
    private static final int PORT = 8991;

    // Data fields
    private static final int GRID_SIZE = 10;
    private static final int EMPTY = 0;
    private static final int OCCUPIED = 1;
    private static final int HIT = 2;
    private static final int MISS = 3;

    // Listen for moves and update the board
    public static void listenToMoves(JTextArea textArea, JButton[][] yourBoardButtons, int[][] playerBoard, boolean[] isMyTurn) {
        // Setup datagram socket
        try (DatagramSocket udpSocket = new DatagramSocket(PORT)) {
            // Setup to receive packets
            byte[] buffer = new byte[256];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            System.out.println("Listening for game moves on port " + PORT);

            // Read moves from opponent while the connection is still open
            while (true) {
                udpSocket.receive(packet);
                String receivedMessage = new String(packet.getData(), 0, packet.getLength());
                String[] coords = receivedMessage.split(",");
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);



                // Update the board based on whether it's a hit or miss
                String responseMessage;
                if (playerBoard[x][y] == OCCUPIED) {
                    playerBoard[x][y] = HIT; // Hit
                    yourBoardButtons[x][y].setBackground(Color.RED);
                    responseMessage = "HIT";
                }
                else {
                    playerBoard[x][y] = MISS;
                    yourBoardButtons[x][y].setBackground(Color.WHITE);
                    responseMessage = "MISS";
                }

                textArea.append("Opponent Turn: (" + ((x*10) + (y+1)) + ") (" + responseMessage + ")\n");
                textArea.setCaretPosition(textArea.getDocument().getLength());

                // Send the hit/miss status back to the player who sent the move
                byte[] responseBuffer = responseMessage.getBytes();
                DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length, packet.getAddress(), packet.getPort());
                udpSocket.send(responsePacket);

                // Switch turn to the host/client depending on the current state
                isMyTurn[0] = true;
            }

        } catch (SocketException e) {System.out.println("Listen Socket error: " + e.getMessage());
        } catch (IOException e) {System.out.println("Listen IO error: " + e.getMessage());}
    }

    // Send a move and update the opponent's board based on the response
    public static void sendMove(String message, String serverIP, JTextArea chat, JButton[][] opponentBoardButtons, int row, int col) {
        // Setup datagram socket
        try(DatagramSocket udpSocket = new DatagramSocket()) {

            InetAddress broadcastAddress = InetAddress.getByName(serverIP);
            // Send the move
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcastAddress, PORT);
            udpSocket.send(packet);

            // Listen for a response (hit/miss)
            byte[] responseBuffer = new byte[256];
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            udpSocket.receive(responsePacket);
            String responseMessage = new String(responsePacket.getData(), 0, responsePacket.getLength());

            // Update the opponent's board based on the hit/miss status
            if (responseMessage.equals("HIT"))
                opponentBoardButtons[row][col].setBackground(Color.RED);
            else
                opponentBoardButtons[row][col].setBackground(Color.BLUE);

            String[] coords = message.split(",");
            int x = Integer.parseInt(coords[0]);
            int y = Integer.parseInt(coords[1]);

            chat.append("Your Turn: (" + ((x*10) + (y+1)) + ") (" + responseMessage + ")\n");
            chat.setCaretPosition(chat.getDocument().getLength());
            udpSocket.close();
        }
        catch (IOException e) {System.err.println("Send IO error: " + e.getMessage());}
    }
}