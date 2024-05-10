import java.io.IOException;
import java.net.*;

public class handshake {
    // Port to be used for handshake protocol
    private static final int PORT = 8990;

    // Handshake protocol initiated by the host
    public static String startConnection() {
        // Setup datagram socket
        try (DatagramSocket udpSocket = new DatagramSocket(PORT)) {
            // Setup to receive packets
            byte[] buffer = new byte[256];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            System.out.println("Listening for connection requests on port " + PORT);

            // Receive connection request from an opponent
            udpSocket.receive(packet);

            // Extract joiner's IP address from packet
            String opponentIp = packet.getAddress().toString().substring(1);

            // Extract joiner's destination for sending your IP
            InetAddress destinationAddress = packet.getAddress();
            int destinationPort = packet.getPort();

            // Send your IP address to the joiner
            String serverMessage = "Received request to join";
            buffer = serverMessage.getBytes();
            DatagramPacket response = new DatagramPacket(buffer, buffer.length, destinationAddress, destinationPort);
            udpSocket.send(response);
            udpSocket.close();

            // Return opponent IP to use for later
            return opponentIp;
        }
        catch (SocketException e) {System.out.println("host Socket error: " + e.getMessage());}
        catch (IOException e) {System.out.println("host IO error: " + e.getMessage());}
        // Should not reach here unless something goes wrong
        return null;
    }

    // Handshake protocol initiated by the person who joins
    public static String joinConnection() {
        // Setup datagram socket
        try(DatagramSocket udpSocket = new DatagramSocket()) {
            // Setup broadcasting to opponent, use special address "255.255.255.255"
            // The special address is used to broadcast to every device in the network
            InetAddress destinationAddress = InetAddress.getByName("255.255.255.255");
            udpSocket.setBroadcast(true);

            // Broadcast join request to find someone hosting a match
            String message = "Join game request";
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, destinationAddress, PORT);
            udpSocket.send(packet);

            // Listen for host's response containing its IP address
            buffer = new byte[256];
            packet = new DatagramPacket(buffer, buffer.length);
            udpSocket.receive(packet);
            udpSocket.close();

            // Extract host's IP
            String opponentIp = packet.getAddress().toString().substring(1);

            // Return opponent IP to use for later
            return opponentIp;

        } catch (IOException e) {System.err.println("Join IO error: " + e.getMessage());}
        // Should not reach here unless something goes wrong
        return null;
    }
}



