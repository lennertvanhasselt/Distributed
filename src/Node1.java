import java.net.InetAddress;
import java.rmi.Naming;
import java.util.Scanner;


public class Node1 {

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
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
   	 		int ownNode = cf.setNode(Nodename, address); 
   	 		
   	 		while(exit == false)
   	 		{
   	 			choice = menu(scan);
   	 			switch (choice) {
   	 			case 1: InetAddress destinationAdr = searchFile(cf, scan);
   	 					break;
   	 			case 3: deleteNode(cf, ownNode);
   	 					break;
   	 			case 4: exit = true;
   	 					break;
   	 			default: exit = false;
   	 					break;
   	 			}
   	 		}
   	 		scan.close();
	
		} catch(Exception e) {
	         System.err.println("FileServer exception: "+ e.getMessage());
	       e.printStackTrace();
	    }
	}
	
	static int menu(Scanner scan)
	{
	 	System.out.println("                  Menu                  ");
	 	System.out.println("----------------------------------------");
	 	System.out.println("1) Search file");
	 	System.out.println("2) ");
	 	System.out.println("3) Delete node from server");
	 	System.out.println("4) Exit");
	 	System.out.println("Give your choice: ");
	 	int menuChoice = scan.nextInt();
		return menuChoice;	
	}
	
	static InetAddress searchFile(ClientInterface cf, Scanner scan)
	{
		System.out.println("Which file do you want?");
	 	String search = scan.nextLine();
	 	search = scan.nextLine();
	 	try {
	 	InetAddress adrFile = cf.searchFile(search); 
	 	return adrFile;	
	 	} catch(Exception e) {
	         System.err.println("FileServer exception: "+ e.getMessage());
	       e.printStackTrace();
	       return null;
	    }
	}
	
	static void deleteNode(ClientInterface cf, int ownNode) {
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

}
