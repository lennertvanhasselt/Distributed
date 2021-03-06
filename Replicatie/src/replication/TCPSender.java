package replication;
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
	PeerInterFace pf;
	String IpToSend;
	String fileName;
	int fileLength;
	Socket sock;
	
	FileInputStream fis;
	BufferedInputStream bis;
	OutputStream os;
	
	public TCPSender(String IpToReceive,String fileName, int fileLength){
		this.IpToSend = IpToReceive;
		this.fileName = fileName;
		this.fileLength = fileLength;
	}

	@Override
	public void run() {

		try {
			pf = (PeerInterFace) Naming.lookup("//" + IpToSend + "/Peer");
			SOCKET_PORT = pf.setupTCPReceiver(fileName, fileLength);
			System.out.println("received port: " + SOCKET_PORT);
			
			sock = new Socket(IpToSend, SOCKET_PORT);
			
			System.out.println("Sending File: " + fileName + ", with length: " + fileLength + ", to: " + IpToSend);
			
			File myFile = new File("C:/temp/"+fileName);
			byte [] mybytearray  = new byte [(int)myFile.length()];
	        fis = new FileInputStream(myFile);
	        bis = new BufferedInputStream(fis);
	        bis.read(mybytearray,0,mybytearray.length);
	        os = sock.getOutputStream();
	        System.out.println("Sending " + fileName + "(" + mybytearray.length + " bytes)");
	        os.write(mybytearray);
	        os.flush();
	        System.out.println("Done.");	
	        
	        bis.close();
	        os.close();
	        sock.close();
			
		} catch (IOException | NotBoundException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}

}
