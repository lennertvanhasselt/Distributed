import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastSender implements Runnable {
	
	public int port;
	
	public MulticastSender(int port){
		this.port = port;
	}

	public void run() {
        try (MulticastSocket socket = new MulticastSocket(port)){
        	InetAddress group = InetAddress.getByName("225.1.1.1"); //multicast ip
        	socket.joinGroup(group);
        	String msg = "#zwan";

           // Create a packet that will contain the data
           // (in the form of bytes) and send it.
           DatagramPacket msgPacket = new DatagramPacket(msg.getBytes(),
                    msg.getBytes().length, group, port);
           socket.send(msgPacket);
           System.out.println("Sent multicast packet with message: " + msg);
        } catch (IOException ex) {
            ex.printStackTrace();
        }		
	}
}
