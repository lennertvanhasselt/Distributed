import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

//GROUP 8 #ZWAM

public class MainServer {
	
	
	public static ClientInfo cli;
	// New clients will be created if there is a valuable request from a node which includes a name and IP.
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		
		//make sure nodes wil be able to perform rmi to server clientinfo
		cli = new ClientInfo();
		String bindLocation = "//localhost/cliNode";
		
		InetAddress address = InetAddress.getLocalHost();
	 	System.out.println(address.getHostAddress());
				
		try{
			LocateRegistry.createRegistry(1099);
			Naming.bind(bindLocation, cli);
	        System.out.println("NodeServer is ready at:" + bindLocation);
            System.out.println("java RMI registry created.");
            new Thread(new MulticastReceiverServer()).start();
        } catch (MalformedURLException | AlreadyBoundException e) {
            System.out.println("java RMI registry already exists.");
		}
		
		new Thread(new MulticastReceiverServer()).start();
	}

}
