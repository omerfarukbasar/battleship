import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

public class handshake {
    public static void startConnection() {
        try (DatagramSocket udpSocket = new DatagramSocket(8989)) {

            byte[] buffer = new byte[256];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            System.out.println("Listening for broadcast messages on port " + 8989);

            // Receive broadcast message from a client
            udpSocket.receive(packet);
            InetAddress clientAddress = packet.getAddress();
            int clientPort = packet.getPort();

            // Log the received message
            String receivedMessage = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Received broadcast message: " + receivedMessage);

            // Send the server's IP address and TCP port to the client
            String serverMessage = InetAddress.getLocalHost().getHostAddress() + ":" + 8990;
            buffer = serverMessage.getBytes();
            DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
            udpSocket.send(response);
            System.out.println("Sent server IP and port: " + serverMessage);
            udpSocket.close();

            // Start the TCP server to accept incoming connections
            try (ServerSocket tcpServer = new ServerSocket(8990)) {
                System.out.println("Waiting for a client to connect via TCP on port " + 8990);
                Socket clientSocket = tcpServer.accept(); // Blocking until a client connects
                System.out.println("Client connected via TCP!");

                // Communicate over the TCP connection
                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);

                // Example conversation: echo back the received message
                String clientInput;
                while ((clientInput = input.readLine()) != null) {
                    System.out.println("Received from client: " + clientInput);
                    output.println("Echo: " + clientInput);
                }
            }
        }
        catch (SocketException e) {System.out.println("host Socket error: " + e.getMessage());}
        catch (IOException e) {System.out.println("host IO error: " + e.getMessage());}
    }

    public static void joinConnection() {
        try {
            // Broadcast address for the network
            InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");

            DatagramSocket udpSocket = new DatagramSocket();
            udpSocket.setBroadcast(true);

            // Broadcast message to find the server
            String message = "Hello, from client!";
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcastAddress, 8989);
            udpSocket.send(packet);
            System.out.println("Broadcast message sent!");

            // Listen for the server's response containing its IP address and TCP port
            buffer = new byte[256];
            packet = new DatagramPacket(buffer, buffer.length);
            udpSocket.receive(packet);
            //System.out.println(packet.getSocketAddress());
            //InetAddress serverAddress = packet.getAddress();
            //String serverMessage = new String(packet.getData(), 0, packet.getLength());
            udpSocket.close();

            // Extract server IP address and TCP port
            String[] serverInfo = packet.getAddress().toString().substring(1).split(":");
            String serverIp = serverInfo[0];
            //String serverAddress = serverInfo[0];
            int tcpPort = Integer.parseInt(serverInfo[1]);
            System.out.println("Received server IP and port: " + serverIp + ":" + tcpPort);

            // Establish a TCP connection to the server
            try (Socket tcpSocket = new Socket(serverIp, tcpPort)) {
                System.out.println("Connected to server via TCP!");

                // Send a message via TCP
                BufferedReader input = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
                PrintWriter output = new PrintWriter(tcpSocket.getOutputStream(), true);
                output.println("Hello, TCP server!");

                // Read response
                String response = input.readLine();
                System.out.println("Received response from server: " + response);
            }
        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }
}



