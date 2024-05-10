import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class gameProtocols {
    // Port to be used for game communication
    private static final int PORT = 8991;

    // Data fields
    private static final int OCCUPIED = 1;
    private static final int HIT = 2;
    private static final int MISS = 3;
    private static final int BOARD_SIZE = 10;

    // Listen for moves and update the board
    public static void listenToMoves(JTextArea announcerArea, JButton[][] playerBoardButtons, int[][] playerBoard, boolean[] isMyTurn, JFrame parent) {
        // Setup datagram socket
        try (DatagramSocket udpSocket = new DatagramSocket(PORT)) {
            // Setup to receive packets
            byte[] buffer = new byte[256];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            System.out.println("Listening for game moves on port " + PORT);

            // Read moves from opponent while the connection is still open
            while (true) {
                // Extract attack coordinates from packet
                udpSocket.receive(packet);
                String receivedMessage = new String(packet.getData(), 0, packet.getLength());
                String[] coordinates = receivedMessage.split(",");
                int row = Integer.parseInt(coordinates[0]);
                int col = Integer.parseInt(coordinates[1]);

                // Update the board based on outcome
                String result;
                if (playerBoard[row][col] == OCCUPIED) {
                    playerBoard[row][col] = HIT;
                    playerBoardButtons[row][col].setBackground(Color.RED);
                    result = "HIT";
                }
                else {
                    playerBoard[row][col] = MISS;
                    playerBoardButtons[row][col].setBackground(Color.WHITE);
                    result = "MISS";
                }

                // Relay move to announcement panel and scroll to latest message
                announcerArea.append("Opponent Turn: (" + ((row*10) + (col+1)) + ") (" + result + ")\n");
                announcerArea.setCaretPosition(announcerArea.getDocument().getLength());

                // Send outcome back to opponent
                byte[] responseBuffer = result.getBytes();
                DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length, packet.getAddress(), packet.getPort());
                udpSocket.send(responsePacket);

                // Send fleet status to opponent
                String fleetStatus = fleetDestroyed(playerBoard);
                byte[] statusBuffer = fleetStatus.getBytes();
                DatagramPacket statusPacket = new DatagramPacket(statusBuffer, statusBuffer.length, packet.getAddress(), packet.getPort());
                udpSocket.send(statusPacket);

                // Game ending condition
                if(fleetStatus.equals("YES")){
                    JOptionPane.showMessageDialog(parent, "Defeat! Your opponent has sunk all your ships.");
                    System.exit(0);
                }

                // Swap turns
                isMyTurn[0] = true;
            }

        } catch (SocketException e) {System.err.println("Listen Socket error: " + e.getMessage());
        } catch (IOException e) {System.err.println("Listen IO error: " + e.getMessage());}
    }

    // Send a move and update the opponent's board based on the response
    public static void sendMove(String opponentIP, JTextArea announcerArea, JButton[][] opponentBoardButtons, int row, int col, JFrame parent) {
        // Setup datagram socket
        try(DatagramSocket udpSocket = new DatagramSocket()) {
            // Send your move to the opponent
            InetAddress broadcastAddress = InetAddress.getByName(opponentIP);
            String message = row + "," + col;
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcastAddress, PORT);
            udpSocket.send(packet);

            // Listen for the outcome
            byte[] responseBuffer = new byte[256];
            DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length);
            udpSocket.receive(responsePacket);
            String result = new String(responsePacket.getData(), 0, responsePacket.getLength());

            // Listen for fleet status
            byte[] statusBuffer = new byte[256];
            DatagramPacket statusPacket = new DatagramPacket(statusBuffer, statusBuffer.length);
            udpSocket.receive(statusPacket);
            String fleetStatus = new String(statusPacket.getData(), 0, statusPacket.getLength());

            // Update the opponent's board based on the outcome
            if (result.equals("HIT"))
                opponentBoardButtons[row][col].setBackground(Color.RED);
            else
                opponentBoardButtons[row][col].setBackground(Color.BLUE);

            // Relay move to announcement panel and scroll to latest message
            announcerArea.append("Your Turn: (" + ((row*10) + (col+1)) + ") (" + result + ")\n");
            announcerArea.setCaretPosition(announcerArea.getDocument().getLength());
            udpSocket.close();

            // Game ending condition
            if (fleetStatus.equals("YES")){
                JOptionPane.showMessageDialog(parent, "Victory! All opponent's ships have been sunk.");
                System.exit(0);
            }
        }
        catch (IOException e) {System.err.println("Send IO error: " + e.getMessage());}
    }

    // Checks if all ships on the board have sunken
    private static String fleetDestroyed(int[][] board) {
        // Iterate through each  coordinate and check for ship presence
        for (int row = 0; row < BOARD_SIZE ; row++)
            for (int col = 0; col < BOARD_SIZE; col++)
                if (board[row][col] == OCCUPIED)
                    return "NO";

        // If all ships have sunk to the bottom of the sea
        return "YES";
    }
}