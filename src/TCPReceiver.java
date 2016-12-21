import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
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
	
	FileOutputStream fos = null;
	BufferedInputStream bis = null;
	
	public TCPReceiver(String fileName, int fileLength){
		this.fileName = fileName;
		this.fileLength = fileLength;
	}

	public void run() {
		int current;
		try {
			
			//serversocket will choose an available port
			servsock = new ServerSocket(0);
			SOCKET_PORT = servsock.getLocalPort();
			System.out.println("Assigned port: " + SOCKET_PORT);
			System.out.println("waiting for connection....");
			sock=servsock.accept();
			System.out.println("accepted connection");
			
			//receive array
		    byte [] mybytearray  = new byte [1024];
		    int byteLength = 1024;
		    InputStream is = sock.getInputStream();
		    fos = new FileOutputStream("C:/temp/replicated/"+fileName);
		    bis = new BufferedInputStream(is, 1024);
		    while((current = bis.read(mybytearray,0,1024)) != -1) {
		    	byteLength = byteLength + 1024;
		    	fos.write(mybytearray,0,current);
		    }
		    
		    /*bytesRead=is.read(mybytearray,0, mybytearray.length);
		    current = bytesRead;
		    
		    do {
		         bytesRead =
		            is.read(mybytearray, current, (mybytearray.length-current));
		         if(bytesRead >= 0) current += bytesRead;
		      } while(bytesRead > -1);
		    	   
		    //write array to file
			bos.write(mybytearray, 0 , current);
			bos.flush(); */
			System.out.println("File " + fileName
			+ " downloaded (" + byteLength + " bytes read)");
						      
			fos.close();
			bis.close();
			sock.close();
			servsock.close();
					
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
}