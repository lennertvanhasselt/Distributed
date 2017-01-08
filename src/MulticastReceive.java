import java.net.MulticastSocket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class MulticastReceive implements Runnable{
	Node node;
	public MulticastReceive(Node node){
		this.node = node;
	}
	
	public void run(){
		
		try (MulticastSocket socket = new MulticastSocket(8888);){			
			
			socket.joinGroup(InetAddress.getByName("225.1.1.1")); //multicast IP same everywhere
			
			while (true) {
				byte[] buf = new byte[256];
				// Receive the information and print it.
	            DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);  
				socket.receive(msgPacket); 										//receive the message
				InetAddress ip = msgPacket.getAddress();  						//extract address from packet
	            String hostname = new String(buf, 0, buf.length); 				//message to string
	            hostname = hostname.replaceAll(Character.toString((char) 0),"");//delete empty bytes of buf          
	            node.hashing(hostname, ip); //set prev/next node if necessary
	            
	         }
			 } catch (IOException | ClassNotFoundException e) {
					e.printStackTrace();
			}
	}
}