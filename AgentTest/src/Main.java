import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {
	
	public static void main(String[] args) throws ClassNotFoundException, IOException, NotBoundException{
		
		Node node = new Node();
		Agent startAgent = new Agent();
		NodeInterface nf;
		String bindLocationNode = "Node";
		try{
			Registry reg = LocateRegistry.createRegistry(1099);
			reg.bind(bindLocationNode, node);
	        System.out.println("NodeServer is ready at:" + bindLocationNode);
            System.out.println("java RMI registry created.");
            
            nf = (NodeInterface) Naming.lookup("//192.168.1.12/Node");
    		nf.startAgent(startAgent);
            
        } catch (MalformedURLException | AlreadyBoundException e) {
            System.out.println("java RMI registry already exists.");
		}
	}

}
