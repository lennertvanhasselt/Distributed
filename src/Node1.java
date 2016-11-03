import java.net.InetAddress;
import java.rmi.Naming;
import java.util.Scanner;


public class Node1 {

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
	 	String Nodename;
	 	int choice=0;
		try {
			String name = "//192.168.1.3/cliNode";
   	 		ClientInterface cf = (ClientInterface) Naming.lookup(name);
		
   	 		System.out.println("Give the name of the node: ");
   	 		Nodename=scan.nextLine();
   	 		
   	 		InetAddress address = InetAddress.getByName("192.168.0.4");
   	 		
   	 		int ownNode = cf.setNode(Nodename, address); 
   	 		
   	 		while(choice < 4)
   	 		{
   	 			choice = menu(scan);
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
	 	System.out.println("1) Bestand opvragen");
	 	System.out.println("2) ");
	 	System.out.println("3) ");
	 	System.out.println("4) Afsluiten");
	 	System.out.println("Geef u keuzen in: ");
	 	int menuChoice = scan.nextInt();
		return menuChoice;	
	}

}
