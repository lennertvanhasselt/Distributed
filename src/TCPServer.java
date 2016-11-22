import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer implements Runnable{
	
	String fileToSend;
	String IPToSend;
	int SOCKET_PORT = 13267;
	FileInputStream fis;
	BufferedInputStream bis;
	OutputStream os;
	ServerSocket servsock;
	Socket sock;
	
	public TCPServer(String fileToSend, String IPToSend){
		this.fileToSend = fileToSend;
		this.IPToSend = IPToSend;
	}
	
	public void run(){
		try {
			servsock = new ServerSocket(SOCKET_PORT);
			System.out.println("waiting...");
			
			sock=servsock.accept();
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
	
}
