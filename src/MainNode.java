import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Scanner;

// Make connection with the server and give the name of your node.
// After the connection is established a menu appears where different options are available to use.
// To make a choice in the menu, just press the number in front of the option.
public class MainNode implements MainNodeInterface{

	public static String servername;
	public static boolean check;
	public static int ownNode;
	public static int numberOfNodes;
	
	public static void main(String[] args) throws ClassNotFoundException, RemoteException, IOException {
		
		Scanner scan = new Scanner(System.in);
		
		System.out.println("Give the name of the node: ");
		String Nodename=scan.nextLine();
		
		InetAddress address = InetAddress.getLocalHost();
	 	address = InetAddress.getByName(address.getHostAddress());
		
		new Thread(new MulticastSender(address,Nodename)).start();
		check = false;
		Node node = new Node();
	 	boolean exit = false;
	 	while(check==false)
	 	{
	 		
	 	}
	 	int choice=0;
		try {
			String name = "//192.168.1.3/cliNode";
   	 		ClientInterface cf = (ClientInterface) Naming.lookup(name);
	
   	 		int ownNode = cf.setNode(Nodename, address); 
   	 		node.setOwnNode(ownNode);
   	 		
   	 		while(exit == false)
   	 		{
   	 			choice = node.menu(scan);
   	 			switch (choice) {
   	 			case 1: InetAddress destinationAdr = node.searchFile(cf, scan);
   	 					break;
   	 			case 4: node.deleteNode(cf, ownNode);
   	 					exit = true;
   	 					break;
   	 			default:exit = false;
   	 					break;
   	 			}
   	 		}
   	 		scan.close();
	
		} catch(Exception e) {
	         System.err.println("FileServer exception: "+ e.getMessage());
	       e.printStackTrace();
	    }
	}

	@Override
	public void setNameServerIP(String ip, int ownNode, int numberOfNodes) {
		servername = ip;
		this.ownNode = ownNode;
		this.numberOfNodes = numberOfNodes;
		
		check = true;	
	}
}
