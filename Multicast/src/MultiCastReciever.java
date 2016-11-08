import java.net.MulticastSocket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

//					  Receiver
public class MultiCastReciever implements Runnable{
	
	InetAddress address;
	int PORT;
	
	public MultiCastReciever(InetAddress IP, int PortNR){
		address=IP;
		PORT=PortNR;
	}
	
public void run(){
		byte[] buf = new byte[256];
		try {
			MulticastSocket socket = new MulticastSocket(PORT);
			
			socket.joinGroup(address);
			
			while (true) {
        	
				// Receive the information and print it.
	            DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
					socket.receive(msgPacket);
	            String msg = new String(buf, 0, buf.length);
	            System.out.println("Socket 1 received msg: " + msg);
	         }
			 } catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
	}
}
