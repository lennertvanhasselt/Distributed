import java.net.MulticastSocket;
import java.rmi.NotBoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class MulticastReceiverServer implements Runnable{
	
	public MulticastReceiverServer(){
	}
	
public void run(){
		
		try {
			MulticastSocket socket = new MulticastSocket(8888);
			
			socket.joinGroup(InetAddress.getByName("225.1.1.1"));
			
			while (true) {
				byte[] buf = new byte[256];
				// Receive the information and print it.
	            DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
				socket.receive(msgPacket);
				InetAddress ip = msgPacket.getAddress();
	            String hostname = new String(buf, 0, buf.length);
	            System.out.println(ip+ " "+hostname);
	            
	            MainServer.cli.setNode(hostname,ip);
	         }
			 } catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
