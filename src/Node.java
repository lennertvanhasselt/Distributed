import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;


public class Node extends UnicastRemoteObject implements NodeInterface {
	
	private static final long serialVersionUID = 1L;
	private int previousNode, nextNode, totalNodes = -1;
	public int ownNode;
	private String ownIP, previousIP, nextIP = null;
	private NodeInterface nf;
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
	public InetAddress searchFile(ClientInterface cf, Scanner scan)
	{
		System.out.println("Which file do you want?");
	 	String search = scan.nextLine();
	 	search = scan.nextLine();
	 	try {
	 	InetAddress adrFile = cf.searchFile(search);
	 	String IP = adrFile.toString();
	 	
	 	// Ping the host of the file
	 	IP=IP.substring(1);
		System.out.println(IP);
	 	Process p = Runtime.getRuntime().exec("ping "+IP);
		BufferedReader inputStream = new BufferedReader(
				new InputStreamReader(p.getInputStream()));

		String s = "";
		// reading output stream of the command
		while ((s = inputStream.readLine()) != null) {
			System.out.println(s);
		}
	 	return adrFile;	
	 	} catch(Exception e) {
	         System.err.println("FileServer exception: "+ e.getMessage());
	       e.printStackTrace();
	       return null;
	    }
	}
	
	// When the node decides to leave the menu and the network it will close and the server will delete the node from it's map.
	public void deleteNode(ClientInterface cf, int ownNode) {
		try{
		if(ownNode != previousNode && ownNode != nextNode)
		{
			nf = (NodeInterface) Naming.lookup("//"+previousIP+"/cliNode");
			nf.setNextNode(nextNode, nextIP);
			nf = (NodeInterface) Naming.lookup("//"+nextIP+"/cliNode");
			nf.setPreviousNode(previousNode, previousIP);
		}
		Boolean answer = cf.deleteNode(ownNode); 
		if (answer == true)
	 	 System.out.println("Node deleted");
		else
		 System.out.println("Node is not deleted");
		return;
		} catch(Exception e) {
	         System.err.println("FileServer exception: "+ e.getMessage());
	       e.printStackTrace();
	       return;
	    }
	}
	
	public void setOwnNode(int ownNode, InetAddress ownIP)
	{
		this.ownIP = ownIP.toString().substring(1);
		this.ownNode = ownNode;
	}
	
	public void setPreviousNode(int previousNode, String previousIP)
	{
		this.previousNode = previousNode;
		this.previousIP = previousIP;
	}
	
	public void setNextNode(int nextNode, String nextIP)
	{
		this.nextNode = nextNode;
		this.nextIP = nextIP;
	}
	
	public int getPreviousNode()
	{
		return previousNode;
	}
	
	public int getNextNode()
	{
		return nextNode;
	}
	
	public int getOwnNode()
	{
		return ownNode;
	}
	
	public boolean hashing(String name, InetAddress IPraw) throws RemoteException, ClassNotFoundException, MalformedURLException, NotBoundException
	{
		String IP = IPraw.toString();
		IP=IP.substring(1);
		nf = (NodeInterface) Naming.lookup("//"+IP+"/cliNode");
		int hashed = Math.abs((int) Integer.toUnsignedLong(name.hashCode())%32768);//number between 0 and 32768
		//to unsigned Long is to make it absolute
		if (previousNode == -1 && nextNode == -1)
		{
			previousIP = IP;
			nextIP = IP;
			previousNode = ownNode;
			nextNode = ownNode;
			
		}
		else if(ownNode == previousNode && ownNode == nextNode)
		{
			nf.changePrevNext(nextNode, ownNode, nextIP, ownIP);
			previousNode = hashed;
			nextNode = hashed;
			previousIP = IP;
			nextIP = IP;
			
		}
		else if(hashed < ownNode && hashed > previousNode)
		{
			nf.changePrevNext(nextNode, ownNode, nextIP, ownIP);
			previousNode = hashed;
			previousIP = IP;
		}
		else if(hashed < ownNode && hashed == previousNode)
		{
			nf.changePrevNext(nextNode, ownNode, nextIP, ownIP);
			previousNode = hashed+1;
			previousIP = IP;
		}
		else if(hashed == ownNode)
		{
			nextNode = hashed+1;
			nextIP = IP;
		}
		else if(hashed > ownNode && hashed < nextNode)
		{
			nextNode = hashed;
			nextIP = IP;
		}
		return true;
	}
	
	public void setTotalNodes(int totalNodes)
	{
		this.totalNodes = totalNodes;
	}
	
	public void changePrevNext(int nextNode, int previousNode, String nextIP, String previousIP)
	{
		this.nextNode = nextNode;
		this.previousNode = previousNode;
		this.nextIP = nextIP;
		this.previousIP = previousIP;
	}
	
	public void setNameServer(String ip, int ownNode, int totalNodes)
	{
		mainServer = ip;
		this.ownNode = ownNode;
		this.totalNodes = totalNodes;
		
		check = true;
		
	}
	
}
