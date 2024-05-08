import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class hostHandshake {

    public static void startConnection(){
        try{
            int port = 4445;

            DatagramSocket socket = new DatagramSocket(port);
            byte[] buffer = new byte[256];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            System.out.println("Listening for broadcast messages on port " + port);

            while (true) {
                socket.receive(packet);
                String receivedMessage = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received broadcast message: " + receivedMessage);
                // Additional handling (e.g., starting a TCP connection) could be added here.
            }
        }
        catch(SocketException e){System.err.println("host handshake socket error: " + e.getMessage());}
        catch(IOException e){System.err.println("host handshake IO error: " + e.getMessage());}
    }
}
