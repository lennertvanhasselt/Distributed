
import java.net.InetAddress;

import java.net.UnknownHostException;

public class MainMulticast{
    
    final static String INET_ADDR = "225.1.1.1";
    final static int PORT = 8888;

    public static void main(String[] args) throws UnknownHostException, InterruptedException {
        // Get the address that we are going to connect to.
        InetAddress group = InetAddress.getByName(INET_ADDR);
     
        // Open a new DatagramSocket, which will be used to send the data.
        	new Thread(new MulticastSender(PORT,group)).start();

    }
}