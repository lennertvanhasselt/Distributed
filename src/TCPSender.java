import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;

public class TCPSender implements Runnable{
	
	int SOCKET_PORT = 3248;
	NodeInterface nf;
	String IpToSend;
	String fileName;
	Boolean local;
	Socket sock;
	
	FileInputStream fis;
	BufferedInputStream bis;
	OutputStream os;
	
	public TCPSender(String IpToSend,String fileName, Boolean local){
		this.IpToSend = IpToSend;
		this.fileName = fileName;
		this.local = local;
	}

	@Override
	public void run() {
		
		File myFile;

		try {
			if(local) //local tells method if sending is from local or from replicated
				myFile = new File("C:/temp/local/"+fileName);
			else
				myFile = new File("C:/temp/replicated/"+fileName);
			
			int fileLength = (int) myFile.length();
			
			nf = (NodeInterface) Naming.lookup("//" + IpToSend + "/Node");
			SOCKET_PORT = nf.setupTCPReceiver(fileName, fileLength);	
			//let other node know we want to send him the file, returns the SOCKET_PORT
			
			sock = new Socket(IpToSend, SOCKET_PORT);
			
			System.out.println("Sending File: " + fileName + ", with length: " + fileLength + ", to: " + IpToSend);
			
			
			
			byte [] mybytearray  = new byte [fileLength]; 	//create an array of bytes of file
	        fis = new FileInputStream(myFile);
	        bis = new BufferedInputStream(fis);
	        bis.read(mybytearray,0,mybytearray.length); 	//read whole file in 1 array
	        os = sock.getOutputStream();
	        os.write(mybytearray);
	        os.flush();
	        
	        bis.close();
	        os.close();
	        sock.close();
		} catch (IOException | NotBoundException e) {
			e.printStackTrace();
		}
		
	}

}