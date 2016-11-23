import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class TCPReceiver implements Runnable{
	
	String IPToReceive, fileName;
	int SOCKET_PORT = 3248;
	
	int FILE_SIZE = 6022386; //larger then original file
	
	int bytesRead;
	int current = 0;
	FileOutputStream fos;
	BufferedOutputStream bos;
	Socket sock;
	
	public TCPReceiver(String IPToReceive,String fileName){
		this.IPToReceive=IPToReceive;
		this.fileName=fileName;
	}
	
	@Override
	public void run() {
		try {
			System.out.println("Trying to connect with "+IPToReceive);
			sock = new Socket(IPToReceive, SOCKET_PORT); //ERROR
			System.out.println("Connected with "+IPToReceive);
			
			  // receive file
		      byte [] mybytearray  = new byte [FILE_SIZE];
		      InputStream is = sock.getInputStream();
		      fos = new FileOutputStream("C:/temp/"+fileName);
		      bos = new BufferedOutputStream(fos);
		      bytesRead = is.read(mybytearray,0,mybytearray.length);
		      current = bytesRead;

		      do {
		         bytesRead =
		            is.read(mybytearray, current, (mybytearray.length-current));
		         if(bytesRead >= 0) current += bytesRead;
		      } while(bytesRead > -1);

		      bos.write(mybytearray, 0 , current);
		      bos.flush();
		      System.out.println("File " + fileName
		          + " downloaded (" + current + " bytes read)");
		      
		      fos.close();
		      bos.close();
		      sock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
