import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;

public class TCPServer implements Runnable{
	
	NodeInterface nf;
	String fileToSend;
	String IPToSend;
	int SOCKET_PORT = 13267;
	
	ServerSocket servsock;
	Socket sock;
	String ownIP;
	
	public TCPServer(String fileToSend, String IPToSend) throws UnknownHostException{
		this.fileToSend = fileToSend;
		this.IPToSend = IPToSend;
		ownIP = InetAddress.getLocalHost().getHostAddress();
	}
	
	public void run(){
		try {
			servsock = new ServerSocket(SOCKET_PORT);
			
			Runnable servAcc = new servSockAccept(servsock,fileToSend);
			
			Thread thread = new Thread(servAcc);
			thread.start();
			
			System.out.println("waiting...");
			
			nf = (NodeInterface) Naming.lookup("//" + IPToSend + "/Node"); //Let the other node know we want to send a file
			nf.readyTCP(ownIP,fileToSend);
			
			thread.join();
	        
	        sock.close();
	        servsock.close();
			
		} catch (IOException | NotBoundException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
