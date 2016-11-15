import java.io.IOException;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.TreeMap;

public class ClientInfo extends UnicastRemoteObject implements ClientInterface{
	
	private static final long serialVersionUID = 1L;
	private ListNodes list;
	
	public ClientInfo() throws ClassNotFoundException, IOException, RemoteException {
		super();
		list = new ListNodes();
	}
	// A new node will be added to a TreeMap.
	public void setNode(String clientName, InetAddress IP) throws ClassNotFoundException, NotBoundException, IOException
	{
		int hashed = hashing(clientName); 
		
		while(list.keyInTable(hashed))
			hashed++;
			
		list.addToTable(hashed, IP);
			
		System.out.println(hashed+" "+IP);
		
		String name = "/"+IP.toString()+"/Node";
		System.out.println("test: "+name);
	 	NodeInterface mni = (NodeInterface) Naming.lookup(name);
	 	
	 	InetAddress address = InetAddress.getLocalHost();
	 	String addr = address.getHostAddress();
		
		mni.setNameServer(addr,hashed,list.getNumberOfNodes());
		
		return;
	}
	
	//If a filename is received the server will search for the node which possesses this file and return an IP.
	public TreeMap<Integer,InetAddress> searchFile(String search) throws ClassNotFoundException
	{
		TreeMap<Integer,InetAddress> tree = new TreeMap<Integer,InetAddress>();
		
		int hashed = hashing(search);
		
		tree = list.getFileIP(hashed);
		
		System.out.println("Found file "+search+" at "+tree.get(tree.firstKey()));
		
		return tree;
	}
	
	//If the node wants to exit the menu and leave the network, it will return if deleting the node was a success or not.
	public boolean deleteNode(int aNode) throws ClassNotFoundException
	{
		
		String answer;
		boolean nodeDeleted = list.deleteNode(aNode);
		if(nodeDeleted)
			answer = "succes!";
		else
			answer = "failed!";
		System.out.println("Node "+aNode+" deleted.  "+answer);
		return nodeDeleted;
	}
	
	// This is used for hashing the name and to receive a unique number.
	public int hashing(String name)
	{
		int hashed = Math.abs((int) Integer.toUnsignedLong(name.hashCode())%32768);//number between 0 and 32768
		//to unsigned Long is to make it absolute
		return hashed;
	}
	
	public TreeMap<Integer, InetAddress> getPreviousNext(int node) throws ClassNotFoundException, IOException {
		return list.getPreviousNext(node);
	}
	
}