import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastSender implements Runnable {
	
	private InetAddress ip;
	private String hostname;
	
	public MulticastSender(InetAddress ip, String hostname){
		this.ip = ip;
		this.hostname = hostname;
	}

	@Override
	public void run() {
		try(MulticastSocket socket = new MulticastSocket(8888);){
			
			socket.joinGroup(ip);
			
	        DatagramPacket msgPacket = new DatagramPacket(hostname.getBytes(),hostname.getBytes().length, ip, 8888);
	        socket.send(msgPacket);
	        System.out.println("Sent multicast packet with message: " + hostname);
			
        } catch (IOException ex) {
          ex.printStackTrace();
        }	
		
	}

}
