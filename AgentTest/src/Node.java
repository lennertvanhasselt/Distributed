import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Node extends UnicastRemoteObject implements NodeInterface {
	

	private static final long serialVersionUID = 1L;
	private NodeInterface nf;
	
	public Node()throws ClassNotFoundException, IOException, RemoteException{
	}
	
	public void startAgent(Agent agent) throws MalformedURLException, RemoteException, NotBoundException{
		System.out.println("ja?");
		Thread thread = new Thread(agent);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nf = (NodeInterface) Naming.lookup("//192.168.1.12/Node");
		nf.startAgent(agent);
		//new Thread(new RMIStarter(agent)).start();
	}
}
