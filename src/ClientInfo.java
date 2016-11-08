import java.io.IOException;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientInfo extends UnicastRemoteObject implements ClientInterface{
	
	private static final long serialVersionUID = 1L;
	private ListNodes list;
	
	public ClientInfo() throws ClassNotFoundException, IOException, RemoteException {
		super();
		list = new ListNodes();
	}
	// A new node will be added to a TreeMap.
	public int setNode(String clientName, InetAddress IP) throws ClassNotFoundException
	{
		int hashed = hashing(clientName); 
		
		while(list.keyInTable(hashed))
			hashed++;
			
		list.AddToTable(hashed, IP);
			
		System.out.println(hashed+" "+IP);
		
		return hashed;
	}
	
	//If a filename is received the server will search for the node which possesses this file and return an IP.
	public InetAddress searchFile(String search) throws ClassNotFoundException
	{
		int hashed = hashing(search);
		
		InetAddress IP = list.getFileIP(hashed);
		
		System.out.println("Found file "+search+" at "+IP);
		
		return IP;
	}
	
	//If the node wants to exit the menu and leave the network, it will return if deleting the node was a success or not.
	public boolean deleteNode(int ownNode) throws ClassNotFoundException
	{
		
		String answer;
		boolean nodeDeleted = list.deleteNode(ownNode);
		if(nodeDeleted)
			answer = "succes!";
		else
			answer = "failed!";
		System.out.println("Node "+ownNode+" deleted.  "+answer);
		return nodeDeleted;
	}
	
	// This is used for hashing the name and to receive a unique number.
	public int hashing(String name)
	{
		int hashed = Math.abs((int) Integer.toUnsignedLong(name.hashCode())%32768);//number between 0 and 32768
		//to unsigned Long is to make it absolute
		return hashed;
	}
	
}