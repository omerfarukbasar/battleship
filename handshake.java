import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class handshake {


    public static void startConnection(){
        int port = 4445;
        try{
            DatagramSocket socket = new DatagramSocket(port);
            byte[] buffer = new byte[256];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            System.out.println("Listening for broadcast messages on port " + port);


            socket.receive(packet);
            String receivedMessage = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Received broadcast message: " + receivedMessage);
            socket.close();

        }
        catch(SocketException e){System.err.println("host handshake socket error: " + e.getMessage());}
        catch(IOException e){System.err.println("host handshake IO error: " + e.getMessage());}
    }
    public static void joinConnection(){
        int port = 4445;
        try{
            // Replace this with the broadcast address of your network
            InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");

            DatagramSocket socket = new DatagramSocket();
            socket.setBroadcast(true);

            String message = "Hello, from client!";
            byte[] buffer = message.getBytes();

            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, broadcastAddress, port);
            socket.send(packet);
            System.out.println("Broadcast message sent!");

            socket.close();
        }
        catch(IOException e){System.err.println("host handshake error: " + e.getMessage());}
    }
}
