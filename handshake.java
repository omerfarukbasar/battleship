import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

public class handshake {

    public static void startConnection() {
        int gamePort = 8989;

        try (DatagramSocket udpSocket = new DatagramSocket(gamePort)) {
            byte[] buffer = new byte[256];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            System.out.println("Listening for broadcast messages on port " + gamePort);

            // Receive broadcast message from a client
            udpSocket.receive(packet);
            InetAddress clientAddress = packet.getAddress();
            int clientPort = packet.getPort();
            System.out.println(clientPort);

            // Send the server's IP address and TCP port to the client
            String serverMessage = "Received request to join";
            buffer = serverMessage.getBytes();
            DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
            udpSocket.send(response);
            udpSocket.close();

        }
        catch (SocketException e) {System.out.println("host Socket error: " + e.getMessage());}
        catch (IOException e) {System.out.println("host IO error: " + e.getMessage());}
    }

    public static String joinConnection() {
        //int gamePort = 8989;
        try {
            // Broadcast address for the network
            InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");
            DatagramSocket udpSocket = new DatagramSocket();
            udpSocket.setBroadcast(true);

            // Broadcast message to find the server
            String message = "Join game request";
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcastAddress, 8989);
            udpSocket.send(packet);

            // Listen for the server's response containing its IP address and TCP port
            buffer = new byte[256];
            packet = new DatagramPacket(buffer, buffer.length);
            udpSocket.receive(packet);
            udpSocket.close();

            // Extract server IP address and TCP port
            String serverIp = packet.getAddress().toString().substring(1);

            return serverIp;

        } catch (IOException e) {System.err.println("Join IO error: " + e.getMessage());}
        return null;
    }
}



