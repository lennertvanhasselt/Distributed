package replication;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPReceiver implements Runnable {
	
	int SOCKET_PORT = 3248;
	ServerSocket servsock;
	Socket sock;
	String fileName;
	int fileLength;
	String IpSender;
	PeerInterFace pf;
	
	FileOutputStream fos = null;
	BufferedOutputStream bos = null;
	int byteRead;
	
	public TCPReceiver(String fileName, int fileLength, String IpSender){
		this.fileName = fileName;
		this.fileLength = fileLength;
		this.IpSender = IpSender;
	}

	public void run() {
		
		System.out.println("setting up socket");
		
		try {
			
			
			servsock = new ServerSocket(SOCKET_PORT);
			System.out.println("waiting for connection....");
			sock=servsock.accept();
			System.out.println("accepted connection");
			
			//receive array
		    byte [] mybytearray  = new byte [fileLength];
		    InputStream is = sock.getInputStream();
		    fos = new FileOutputStream("C:/temp/"+fileName);
		    bos = new BufferedOutputStream(fos);
		    byteRead = is.read(mybytearray,0, fileLength);
		   
		    //write array to file
			bos.write(mybytearray, 0 , fileLength);
			bos.flush();
			System.out.println("File " + fileName
			+ " downloaded (" + fileLength + " bytes read)");
			      
			fos.close();
			bos.close();
			sock.close();
			servsock.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
