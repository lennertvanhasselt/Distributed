import java.io.BufferedInputStream;
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
	
	public servSockAccept(ServerSocket servsock) throws UnknownHostException{
		this.servsock = servsock;
	}
	
	public void run(){
		try {
			System.out.println("Runnable begin");
			sock=servsock.accept();
			System.out.println("Runnable end");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Socket getsock(){
		return sock;
	}
}
