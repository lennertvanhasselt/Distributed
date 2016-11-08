import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class MainMulticast extends Thread {
    
    final static String INET_ADDR = "225.1.1.1";
    final static int PORT = 8888;

    public static void main(String[] args) throws UnknownHostException, InterruptedException {
        // Get the address that we are going to connect to.
        InetAddress group = InetAddress.getByName(INET_ADDR);
     
        // Open a new DatagramSocket, which will be used to send the data.
        try (MulticastSocket serverSocket = new MulticastSocket(PORT)) {
        	serverSocket.joinGroup(group);
                String msg = "192.168.1.12";

                // Create a packet that will contain the data
                // (in the form of bytes) and send it.
                DatagramPacket msgPacket = new DatagramPacket(msg.getBytes(),
                        msg.getBytes().length, group, PORT);
                serverSocket.send(msgPacket);
                System.out.println("Server sent multicast packet with message: " + msg);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}