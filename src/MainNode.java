import java.net.InetAddress;
import java.rmi.Naming;
import java.util.Scanner;

// Make connection with the server and give the name of your node.
// After the connection is established a menu appears where different options are available to use.
// To make a choice in the menu, just press the number in front of the option.
public class MainNode {

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		Node node = new Node();
	 	String Nodename;
	 	boolean exit = false;
	 	int choice=0;
		try {
			String name = "//192.168.1.3/cliNode";
   	 		ClientInterface cf = (ClientInterface) Naming.lookup(name);
	
   	 		System.out.println("Give the name of the node: ");
   	 		Nodename=scan.nextLine();
   	 		InetAddress address = InetAddress.getLocalHost();
   	 		address = InetAddress.getByName(address.getHostAddress());
   	 		new Thread(new MulticastReceive(address,Nodename)).start();
   	 		new Thread(new MulticastSender(address,Nodename)).start();
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
}
