
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientInfo extends UnicastRemoteObject implements ClientInterface{
	
	public ClientInfo() throws RemoteException {
		
	}
		
	public int setNode(String clientName){
			return 0;
	}
	
}
