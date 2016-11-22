import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.util.TreeMap;


public class Node extends UnicastRemoteObject implements NodeInterface {
	
	private static final long serialVersionUID = 1L;
	private int previousNode = -1, nextNode = -1, totalNodes = -1;
	public int ownNode;
	private String ownIP, previousIP, nextIP = null;
	private NodeInterface nf;
	private ClientInterface cf;
	public String mainServer;
	public boolean check;
	
	public Node() throws ClassNotFoundException, IOException, RemoteException {	
		mainServer = "";
		check = false;
	}
	
	// This is the menu that will appear on the console ones the connection with the server is established.
	public int menu(Scanner scan)
	{
	 	System.out.println("                  Menu                  ");
	 	System.out.println("----------------------------------------");
	 	System.out.println("1) Search file");
	 	System.out.println("2) ");
	 	System.out.println("3) ");
	 	System.out.println("4) Exit and delete node");
	 	System.out.println("Give your choice: ");
	 	int menuChoice = scan.nextInt();
		return menuChoice;	
	}
	
	// The option where the user can give a filename and receive the IP of the node who has that file.
	// When the node has received the IP of the location it will start to ping this node to make sure it has connection with it.
	// If the connection is not working and it can't find the node, an update of the network will take place with the node deleted. 
	public InetAddress searchFile(Scanner scan) throws ClassNotFoundException, IOException
	{
		System.out.println("Which file do you want?");
	 	String search = scan.nextLine();
	 	search = scan.nextLine();
	 	int nodeNumber=0;
	 	try {
	 		TreeMap<Integer,InetAddress> map = new TreeMap<Integer,InetAddress>();
	 		map = cf.searchFile(search);
	 		String IP = map.get(map.firstKey()).toString().substring(1);
	 		nodeNumber=map.firstKey();
	 		System.out.println("ip from searchFile: "+IP);
	 		nf = (NodeInterface) Naming.lookup("//" + IP + "/Node");
	 		nf.checkUpdate();
	 	
	 		// Ping the host of the file
	 		System.out.println(IP);
			Process p = Runtime.getRuntime().exec("ping "+IP);
			BufferedReader inputStream = new BufferedReader(
				new InputStreamReader(p.getInputStream()));
			String s = "";
			
			// reading output stream of the command
			while ((s = inputStream.readLine()) != null) {
				System.out.println(s);
			}
			return map.get(map.firstKey());	
	 	} catch(Exception e) {
	 		updateNetwork(nodeNumber);
	        System.err.println("FileServer exception: "+ e.getMessage());
	        e.printStackTrace();
	        return null;
	    }
	}
	
	// When the node decides to leave the menu and the network it will close and the server will delete the node from it's map.
	// The Previous and Next node in the system will have their previous or next node updated because the deleted node is not available anymore.
	public void deleteNode(int ownNode) throws ClassNotFoundException, IOException{
		int contactedNode = -1;
		try {
			if (ownNode != previousNode && ownNode != nextNode) {
				contactedNode = previousNode;
				System.out.println(previousIP);
				nf = (NodeInterface) Naming.lookup("//" + previousIP + "/Node");
				System.out.println("tot hier");
				nf.setNextNode(nextNode, nextIP);
				contactedNode = nextNode;
				nf = (NodeInterface) Naming.lookup("//" + nextIP + "/Node");
				nf.setPreviousNode(previousNode, previousIP);
			}
			Boolean answer = cf.deleteNode(ownNode);
			if (answer == true)
				System.out.println("Node deleted");
			else
				System.out.println("Node is not deleted");
			return;
		} catch (Exception e) {
			System.err.println("FileServer exception: " + e.getMessage());
			e.printStackTrace();
			updateNetwork(contactedNode);
			return;
		}
	}
	
	// The network will be updated if a node is not available, when it is not available it will be deleted and it's previous and next node will be informed and changed.
	// Their Previous or next node will be changed such that the deleted node will no longer be in the system.
	public void updateNetwork(int node) throws ClassNotFoundException, IOException {
		TreeMap<Integer, InetAddress> prevNext = cf.getPreviousNext(node);
		int nn = -1;
		int pn = -1;
		int first = prevNext.firstKey();
		int last = prevNext.lastKey();
		
		//Delete the corrupt node before checking others? Otherwise an error will result in not deleting the corrupt node
		cf.deleteNode(node); 
		System.out.println("Delete " + node);
		
		if (first > node && last < node) {
			nn = last;
			pn = first;
		} else if (first > node && last > node) {
			nn = first;
			pn = last;
		} else if (first < node && last < node) {
			nn = last;
			pn = first;
		} else
			System.out.println("error Node nn, pn");
		
		// If previous node is not his own node
		if (pn != ownNode) {
			String IP = prevNext.get(pn).toString().substring(1);
			try {
				nf = (NodeInterface) Naming.lookup("//" + IP + "/Node");
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (NotBoundException e) {
				updateNetwork(pn);
				e.printStackTrace();
			}
			IP = prevNext.get(nn).toString().substring(1);
			nf.setNextNode(nn, IP);
		} else {
			nextNode=nn;
			nextIP = prevNext.get(nn).toString().substring(1);
		}
		
		// If next node is not his own node
		if (nn != ownNode) {
			String IP = prevNext.get(nn).toString().substring(1);
			try {
				nf = (NodeInterface) Naming.lookup("//" + IP + "/Node");
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (NotBoundException e) {
				updateNetwork(nn);
				e.printStackTrace();
			}
			IP = prevNext.get(pn).toString().substring(1);
			nf.setPreviousNode(pn, IP);
		} else {
			previousNode=pn;
			previousIP = prevNext.get(pn).toString().substring(1);
		}
		//cf.deleteNode(node);
		return;
	}
	
	// When receiving a multicast, the hashvalue will be calculated and used to change the next or previous Node and IP.
	// The change depends on how many nodes are in the system and what their previous and next are.
	public boolean hashing(String name, InetAddress IPraw) throws ClassNotFoundException, IOException
	{
		String IP = IPraw.toString();
		IP = IP.substring(1);
		int hashed = Math.abs((int) Integer.toUnsignedLong(name.hashCode()) % 32768);
		// number between 0 and 32768
		// to unsigned Long is to make it absolute
		try {
			// When no node is located in the system.
			// -->It will ad his own node and ip to his previous and next
			if (previousNode == -1 && nextNode == -1) {
				previousIP = IP;
				nextIP = IP;
				previousNode = ownNode;
				nextNode = ownNode;
				
			// When only 1 node is located in the system.
			// -->It will be added as the previous and the next node.
			} else if (ownNode == previousNode && ownNode == nextNode) {
				nf = (NodeInterface) Naming.lookup("//" + IP + "/Node");
				System.out.println("ownNode: "+ownNode+"  ownIP: "+ownIP+ "send to "+IP);
				nf.changePrevNext(ownNode, ownNode, ownIP, ownIP);
				previousNode = hashed;
				nextNode = hashed;
				previousIP = IP;
				nextIP = IP;
				System.out.println("nextNode: "+this.nextNode);
				System.out.println("nextIP: "+this.nextIP);
				System.out.println("previousNode: "+this.previousNode);
				System.out.println("previousIP: "+this.previousIP);
			
			} else if(nextNode == previousNode) {
				if (nextNode < ownNode) {
						
					if (hashed < nextNode) {
							nf = (NodeInterface) Naming.lookup("//" + IP + "/Node");
							nf.changePrevNext(nextNode, ownNode, nextIP, ownIP);
							nextNode = hashed;
							nextIP = IP;
							System.out.println("nextNode: "+this.nextNode);
							System.out.println("nextIP: "+this.nextIP);
						}
						
						if (hashed > nextNode && hashed < ownNode) {
							previousNode = hashed;
							previousIP = IP;
							System.out.println("previousNode: "+this.previousNode);
							System.out.println("previousIP: "+this.previousIP);
						}
						
						if (hashed == nextNode && hashed < ownNode) {
							hashed++;
							previousNode = hashed;
							previousIP = IP;
							System.out.println("previousNode: "+this.previousNode);
							System.out.println("previousIP: "+this.previousIP);
						}
						
						if (hashed > ownNode) {
							nf = (NodeInterface) Naming.lookup("//" + IP + "/Node");
							nf.changePrevNext(nextNode, ownNode, nextIP, ownIP);
							nextNode = hashed;
							nextIP = IP;
							System.out.println("nextNode: "+this.nextNode);
							System.out.println("nextIP: "+this.nextIP);
						}
						
						if (hashed == ownNode) {
							hashed++;
							nf = (NodeInterface) Naming.lookup("//" + IP + "/Node");
							nf.changePrevNext(nextNode, ownNode, nextIP, ownIP);
							nextNode = hashed;
							nextIP = IP;
							System.out.println("nextNode: "+this.nextNode);
							System.out.println("nextIP: "+this.nextIP);
						}
					} else  {
						
						if (hashed < ownNode) {
							previousNode = hashed;
							previousIP = IP;
							System.out.println("previousNode: "+this.previousNode);
							System.out.println("previousIP: "+this.previousIP);
						}
						
						if (hashed > ownNode && hashed < nextNode) {
							nf = (NodeInterface) Naming.lookup("//" + IP + "/Node");
							nf.changePrevNext(nextNode, ownNode, nextIP, ownIP);
							nextNode = hashed;
							nextIP = IP;
							System.out.println("nextNode: "+this.nextNode);
							System.out.println("nextIP: "+this.nextIP);
						}
						
						if (hashed == ownNode && hashed < nextNode) {
							hashed++;
							nf = (NodeInterface) Naming.lookup("//" + IP + "/Node");
							nf.changePrevNext(nextNode, ownNode, nextIP, ownIP);
							nextNode = hashed;
							nextIP = IP;
							System.out.println("nextNode: "+this.nextNode);
							System.out.println("nextIP: "+this.nextIP);
						}
						
						if (hashed > nextNode) {
							previousNode = hashed;
							previousIP = IP;
							System.out.println("previousNode: "+this.previousNode);
							System.out.println("previousIP: "+this.previousIP);
						}
						
						if (hashed == nextNode) {
							hashed++;
							previousNode = hashed;
							previousIP = IP;
							System.out.println("previousNode: "+this.previousNode);
							System.out.println("previousIP: "+this.previousIP);
						}
					}			
			}
				
				
				
				
				
				// When more then 1 node is located in the system and the hash is smaller than it's own but bigger than it's previous.
			// -->It will be added as the previous node.
			/*} else if (hashed < ownNode && hashed > previousNode) {
				nf = (NodeInterface) Naming.lookup("//" + IP + "/Node");
				nf.changePrevNext(nextNode, ownNode, nextIP, ownIP);
				previousNode = hashed;
				previousIP = IP;
				System.out.println("previousNode: "+this.previousNode);
				System.out.println("previousIP: "+this.previousIP);
			
			// When more then 1 node is located in the system and the hash is smaller than it's own but the same than it's previous.
			// --> It will be added as the previous node.
			} else if (hashed < ownNode && hashed == previousNode) {
				nf = (NodeInterface) Naming.lookup("//" + IP + "/Node");
				nf.changePrevNext(nextNode, ownNode, nextIP, ownIP);
				previousNode = hashed + 1;
				previousIP = IP;
				System.out.println("previousNode: "+this.previousNode);
				System.out.println("previousIP: "+this.previousIP);
			
			// When the new node has the same hashvalue, it will be his next node.
			} else if (hashed == ownNode) {
				nextNode = hashed + 1;
				nextIP = IP;
				System.out.println("nextNode: "+this.nextNode);
				System.out.println("nextIP: "+this.nextIP);
			
			// When more then 1 node is located in the system and the hash is bigger than it's own but smaller than it's next.
			// -->It will be added as the next node.
			} else if (hashed > ownNode && hashed < nextNode) {
				nextNode = hashed;
				nextIP = IP;
				System.out.println("nextNode: "+this.nextNode);
				System.out.println("nextIP: "+this.nextIP);
			}*/
			return true;

		} catch (NotBoundException e) {
			updateNetwork(hashed);
			e.printStackTrace();
			return false;
		}
	}
	
	// First initialisation of the node when it is connected with the server.
	// It receives it's ip, node and total amounts of nodes located in the system
	public void setNameServer(String ip, int ownNode, int totalNodes) throws RemoteException, ClassNotFoundException, UnknownHostException
	{
		System.out.println("SetNameServer");
		mainServer = ip;
		this.ownNode = ownNode;
		this.totalNodes = totalNodes;
		System.out.println(mainServer);
		System.out.println(this.ownNode);
		System.out.println(this.totalNodes);
		
		InetAddress address = InetAddress.getLocalHost();
	 	address = InetAddress.getByName(address.getHostAddress());
	 	ownIP = address.toString().substring(1);

		System.out.println("setNameServer IP: "+ownIP);
		previousIP = ownIP;
		nextIP = ownIP;
		previousNode = ownNode;
		nextNode = ownNode;
		
		check = true;
	}
	
	// Changing the previous and the next node and ip for this certain node.
	public void changePrevNext(int nextNode, int previousNode, String nextIP, String previousIP)throws RemoteException, ClassNotFoundException
	{
		System.out.println("changePrevNext");
		this.nextNode = nextNode;
		this.previousNode = previousNode;
		this.nextIP = nextIP;
		this.previousIP = previousIP;
		System.out.println("nextNode: "+this.nextNode+" previousNode: "+this.previousNode);
		System.out.println("nextIP: "+this.nextIP+" previousIP: "+this.previousIP);
	}
	
	// Sets total nodes
	public void setTotalNodes(int totalNodes)
	{
		this.totalNodes = totalNodes;
	}
	
	// Sets ClientInterface
	public void setClientInterface(ClientInterface cf)
	{
		this.cf = cf;
	}

	// Used to display that someone is checking a file on your node.
	public void checkUpdate() throws RemoteException, ClassNotFoundException {
		System.out.println("Someone is checking your files... weird...");
		
	}
	
	// Used to get previous node
	public int getPreviousNode()
	{
		return previousNode;
	}
	
	// Used to get next node
	public int getNextNode()
	{
		return nextNode;
	}
	
	// Used to get own node
	public int getOwnNode()
	{
		return ownNode;
	}
	
	// Set the ip and node of the previous node.
	public void setPreviousNode(int previousNode, String previousIP)throws RemoteException, ClassNotFoundException
	{	
		this.previousNode = previousNode;
		this.previousIP = previousIP;
		System.out.println("set previous Node");
		System.out.println("previousNode: "+this.previousNode);
		System.out.println("previousIP: "+this.previousIP);
	}
		
	// Set the ip and node of the next node.
	public void setNextNode(int nextNode, String nextIP)throws RemoteException, ClassNotFoundException
	{
		this.nextNode = nextNode;
		this.nextIP = nextIP;
		System.out.println("set next Node");
		System.out.println("nextNode: "+this.nextNode);
		System.out.println("nextIP: "+this.nextIP);
	}	
}