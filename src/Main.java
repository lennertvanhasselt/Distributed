import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

//GROUP 8 #ZWAM

public class Main {
	// New clients will be created if there is a valuable request from a node which includes a name and IP.
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		ClientInfo cli = new ClientInfo();
		String bindLocation = "//localhost/cliNode";
		
		ListNodes list = new ListNodes();
		
		try{
			LocateRegistry.createRegistry(1099);
			Naming.bind(bindLocation, cli);
	        System.out.println("NodeServer is ready at:" + bindLocation);
            System.out.println("java RMI registry created.");
        } catch (MalformedURLException | AlreadyBoundException e) {
            System.out.println("java RMI registry already exists.");
		}
	}

}
