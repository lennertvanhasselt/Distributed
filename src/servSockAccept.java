import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class servSockAccept implements Runnable{
	
	ServerSocket servsock;
	Socket sock;
	FileInputStream fis;
	BufferedInputStream bis;
	OutputStream os;
	String fileToSend;
	
	public servSockAccept(ServerSocket servsock, String fileToSend) throws UnknownHostException{
		this.servsock = servsock;
		this.fileToSend=fileToSend;
	}
	
	public void run(){
		try {
			System.out.println("Runnable begin");
			sock=servsock.accept(); //doesn't go through this.
			System.out.println("Runnable end");
			
			System.out.println("Accepted connection: "+sock);
			
			File myFile = new File("C:/temp/"+fileToSend);
			byte [] mybytearray  = new byte [(int)myFile.length()];
	        fis = new FileInputStream(myFile);
	        bis = new BufferedInputStream(fis);
	        bis.read(mybytearray,0,mybytearray.length);
	        os = sock.getOutputStream();
	        System.out.println("Sending " + fileToSend + "(" + mybytearray.length + " bytes)");
	        os.write(mybytearray,0,mybytearray.length);
	        os.flush();
	        System.out.println("Done.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Socket getsock(){
		return sock;
	}
}
