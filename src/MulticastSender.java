import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastSender implements Runnable {
	
	private String hostname;
	
	public MulticastSender(String hostname){
		this.hostname = hostname;
	}

	@Override
	public void run() {
		try(MulticastSocket socket = new MulticastSocket(8888);){
			
			InetAddress ip = InetAddress.getByName("225.1.1.1"); //multicast ip-address
			
			socket.joinGroup(ip);
			
	        DatagramPacket msgPacket = new DatagramPacket(hostname.getBytes(),hostname.getBytes().length, ip, 8888);
	        socket.send(msgPacket);
	        System.out.println("Sent multicast packet with message: " + hostname);
			
        } catch (IOException ex) {
          ex.printStackTrace();
        }	
		
	}

}
