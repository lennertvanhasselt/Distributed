
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientInfo extends UnicastRemoteObject implements ClientInterface{
	
	
	
	public ClientInfo() throws RemoteException {
		super();
	}
		
	public int setNode(String clientName, InetAddress IP){
			return 0;
	}
	
}
