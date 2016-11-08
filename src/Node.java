import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.Scanner;


public class Node {
	
	private int ownNode, previousNode, nextNode, totalNodes = 0;
	
	public Node() {	
		
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
	
	public void setOwnNode(int ownNode)
	{
		this.ownNode = ownNode;
	}
	
	public void setTotalNodes(int totalNodes)
	{
		this.totalNodes = totalNodes;
		
		if (totalNodes == 0)
		{
			previousNode = ownNode;
			nextNode = ownNode;
		}
		else if(totalNodes <= 1)
		{
			
		}
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
	
	public int hashing(String name)
	{
		int hashed = Math.abs((int) Integer.toUnsignedLong(name.hashCode())%32768);//number between 0 and 32768
		//to unsigned Long is to make it absolute
		return hashed;
	}
}
