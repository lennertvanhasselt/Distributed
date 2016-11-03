import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientInfo extends UnicastRemoteObject implements ClientInterface{
	
	private static final long serialVersionUID = 1L;
	
	public ClientInfo() throws RemoteException {
		super();
	}
		
	public int setNode(String clientName, InetAddress IP) throws ClassNotFoundException{
		
		int hashed = hashing(clientName); 
		
		while(ListNodes.keyInTable(hashed))
			hashed++;
			
		ListNodes.AddToTable(hashed, IP);
			
		System.out.println(hashed+" "+IP);
		
		return hashed;
	}
	
	public InetAddress searchFile(String search) throws ClassNotFoundException
	{
		int hashed = hashing(search);
		
		InetAddress IP = ListNodes.getFileIP(hashed);
		
		System.out.println("Found file "+search+" at "+IP);
		
		return IP;
	}
	
	public boolean deleteNode(int ownNode) throws ClassNotFoundException
	{
		
		String answer;
		boolean nodeDeleted = ListNodes.deleteNode(ownNode);
		if(nodeDeleted)
			answer = "succes!";
		else
			answer = "failed!";
		System.out.println("Node "+ownNode+" deleted.  "+answer);
		return nodeDeleted;
	}
	
	public int hashing(String name)
	{
		int hashed = Math.abs((int) Integer.toUnsignedLong(name.hashCode())%32768);//number between 0 and 32768
		//to unsigned Long is to make it absolute
		return hashed;
	}
	
}