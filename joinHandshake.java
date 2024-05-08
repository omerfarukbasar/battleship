import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class joinHandshake {
    public static void joinConnection(){
        try{
            int port = 4445;

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
