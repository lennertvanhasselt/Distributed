import java.net.MulticastSocket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class MulticastReceive implements Runnable{
	
	private InetAddress ip;
	private String hostname;
	
	public MulticastReceive(InetAddress ip, String hostname){
		this.ip=ip;
		this.hostname=hostname;
	}
	
public void run(){
		
		try {
			MulticastSocket socket = new MulticastSocket(8888);
			
			socket.joinGroup(ip);
			
			while (true) {
				byte[] buf = new byte[256];
				// Receive the information and print it.
	            DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
				socket.receive(msgPacket);
				InetAddress ip = msgPacket.getAddress();
	            hostname = new String(buf, 0, buf.length);
	            System.out.println(ip+ " "+hostname);
	            
	            
	         }
			 } catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
	}
}