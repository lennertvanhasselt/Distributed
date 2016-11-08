import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainMulticastClient {
    
    final static String INET_ADDR = "225.1.1.1";
    final static int PORT = 8888;

    public static void main(String[] args) throws UnknownHostException {
        // Get the address that we are going to connect to.
        InetAddress address = InetAddress.getByName(INET_ADDR);
        
        // Create a buffer of bytes, which will be used to store
        // the incoming bytes containing the information from the server.
        // Since the message is small here, 256 bytes should be enough.
        
        
        // Create a new Multicast socket (that will allow other sockets/programs
        // to join it as well.
            
        new Thread(new MultiCastReciever(address,PORT)).start();
            
    }
}