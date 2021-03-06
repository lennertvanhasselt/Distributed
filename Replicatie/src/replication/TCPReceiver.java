package replication;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPReceiver implements Runnable {
	
	int SOCKET_PORT = 0;
	ServerSocket servsock;
	Socket sock;
	String fileName;
	int fileLength;
	PeerInterFace pf;
	
	FileOutputStream fos = null;
	BufferedOutputStream bos = null;
	
	public TCPReceiver(String fileName, int fileLength){
		this.fileName = fileName;
		this.fileLength = fileLength;
	}

	public void run() {
		
		try {
			
			//serversocket will choose an available port
			servsock = new ServerSocket(0);
			SOCKET_PORT = servsock.getLocalPort();
			System.out.println("Assigned port: " + SOCKET_PORT);
			System.out.println("waiting for connection....");
			sock=servsock.accept();
			System.out.println("accepted connection");
			
			//receive array
		    byte [] mybytearray  = new byte [fileLength];
		    InputStream is = sock.getInputStream();
		    fos = new FileOutputStream("C:/temporary/"+fileName);
		    bos = new BufferedOutputStream(fos);
		    is.read(mybytearray,0, fileLength);
		   
		    	   
		    //write array to file
			bos.write(mybytearray, 0 , fileLength);
			bos.flush();
			System.out.println("File " + fileName
			+ " downloaded (" + fileLength + " bytes read)");
						      
			fos.close();
			bos.close();
			sock.close();
			servsock.close();
					
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
}
