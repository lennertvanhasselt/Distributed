import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastSender implements Runnable {
	
	public int port;
	public InetAddress group;
	
	public MulticastSender(int port, InetAddress group ){
		this.port = port;
		this.group = group;
	}

	public void run() {
        try (MulticastSocket socket = new MulticastSocket(port)){
        	socket.joinGroup(group);
        	String msg = "192.168.1.12";

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
