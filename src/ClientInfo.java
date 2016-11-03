import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientInfo extends UnicastRemoteObject implements ClientInterface{
	
	private static final long serialVersionUID = 1L;
	
	public ClientInfo() throws RemoteException {
		super();
	}
		
	public int setNode(String clientName, InetAddress IP){

		int hashed = (int) Integer.toUnsignedLong(clientName.hashCode())%32768; //number between 0 and 32768
		//to unsigned Long is to make it absolute
		
		System.out.println(hashed);
		
		while(ListNodes.keyInTable(hashed))
			hashed++;
		
		ListNodes.AddToTable(hashed, IP);
		
	
		
		System.out.println(hashed+" "+IP);
		
		return hashed;
	}
	
}