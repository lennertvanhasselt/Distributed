import java.io.IOException;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.TreeMap;

//SERVER SIDE
public class ClientInfo extends UnicastRemoteObject implements ClientInterface{
	
	private static final long serialVersionUID = 1L;
	private ListNodes list;
	
	public ClientInfo() throws ClassNotFoundException, IOException, RemoteException {
		super();
		list = new ListNodes();
	}
	
	// A new node will be added to a TreeMap. 
	// The includes are: clientName which will be hashed and the IP of the host
	// If the entry in the list with that hash-value is already in use, the hash-value will be increased with 1
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
	
	// If a filename is received the server will search for the node which possesses this file and returns an IP.
	public TreeMap<Integer,InetAddress> searchFile(String search) throws ClassNotFoundException
	{
		TreeMap<Integer,InetAddress> tree = new TreeMap<Integer,InetAddress>();
		int hashed = hashing(search);
		tree = list.getFileIP(hashed);
		
		System.out.println("Found file "+search+" at "+tree.get(tree.firstKey()));
		return tree;
	}
	
	// If the node wants to exit the menu and leave the network.
	// It will return if deleting the node was a success or not.
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
	
	// Used for hashing the name and receive a unique number.
	// The hash --> Number between 0 and 32768 to unsigned Long is to make it absolute
	public int hashing(String name)
	{
		int hashed = Math.abs((int) Integer.toUnsignedLong(name.hashCode())%32768);
		return hashed;
	}
	
	// Used to return a TreeMap which includes the name and IP of the previous and the next entry of the list.
	public TreeMap<Integer, InetAddress> getPreviousNext(int node) throws ClassNotFoundException, IOException {
		return list.getPreviousNext(node);
	}
	
	//only used in mainserver to check the list of nodes
	public ListNodes getList(){
		return list;
	}
	
}