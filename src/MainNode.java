import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

// Make connection with the server and give the name of your node.
// After the connection is established a menu appears where different options are available to use.
// To make a choice in the menu, just press the number in front of the option.
public class MainNode {

	public static void main(String[] args) throws ClassNotFoundException, IOException {
	 	boolean exit = false;
	 	int choice=0;
		Scanner scan = new Scanner(System.in);
		
		InetAddress address = InetAddress.getLocalHost();
	 	System.out.println(address.getHostAddress());
		
		System.out.println("Give the name of the node: ");
		String nodename=scan.nextLine();
		
		Node node = new Node();
		
		//not implemented bc of agents (checks local files every 30 secs)
		//new Thread(new CheckFileList(node)).start();
		
		//make sure rmi can be performed to the node
		try{
			String bindLocationNode = "//localhost/Node";
			LocateRegistry.createRegistry(1099);
			Naming.bind(bindLocationNode, node);
	        System.out.println("NodeServer is ready at:" + bindLocationNode);
            System.out.println("java RMI registry created.");
        } catch (MalformedURLException | AlreadyBoundException e) {
            System.out.println("java RMI registry already exists.");
		}
		
		//start multicast to discover nameserver and other nodes
		new Thread(new MulticastSender(nodename)).start();
		
		//wait for rmi to be performed by server
		System.out.println("Waiting for rmi to be performed by server");
		while(node.check==false)
	 	{
	 		System.out.print("");   //without print, the check doesn't update.
	 	}
	 	System.out.println("nameserver recognized: " + node.mainServer);
	 	//start multicastreceive to see other nodes join
	 	new Thread(new MulticastReceive(node)).start();
		try {
			String name = "//"+node.mainServer+"/cliNode";
   	 		ClientInterface cf = (ClientInterface) Naming.lookup(name);
   	 		node.setClientInterface(cf);
   	 		
   	 		while(exit == false)
   	 		{
   	 			choice = node.menu(scan);
   	 			switch (choice) {
   	 			case 1: node.searchFile(scan);
   	 					break;
   	 			case 2: System.out.println(node.getPreviousNode() + " - "+node.getNextNode());
   	 					break;
   	 			case 3: node.replicateLocalFiles();
						break;
   	 			case 4: node.deleteNode();
   	 					exit = true;
   	 					break;
   	 			case 5: AgentFileList agent = new AgentFileList();
   	 					String nextIP = node.getNextIP();
   	 					System.out.println(nextIP);
   	 					NodeInterface nf = (NodeInterface) Naming.lookup("//"+nextIP+"/Node");
   	 					nf.startAgentFileList(agent);
   	 					break;
   	 			case 6: node.printTotalFileList();
   	 					break;
   	 			case 7: System.out.print("Give index: ");
   	 					int index = scan.nextInt(); 	 					
   	 					node.downloadFile(index,scan);
   	 			default:exit = false;
   	 					break;
   	 			}
   	 		}
   	 		scan.close();
   	 		System.exit(0);
		} catch(Exception e) {
	         System.err.println("FileServer exception: "+ e.getMessage());
	       e.printStackTrace();
	    }
	}
}
