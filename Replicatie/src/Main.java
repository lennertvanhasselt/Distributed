import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Iterator;

public class Main {

	public static void main(String[] args) throws RemoteException {
		
		//make rmi possible to peer
		Peer peer = new Peer();
		String bindLocationPeer = "//localhost/Peer";
		try{
			LocateRegistry.createRegistry(1099);
			Naming.bind(bindLocationPeer, peer);
	        System.out.println("PeerRMIServer is ready at:" + bindLocationPeer);
            System.out.println("java RMI registry created.");
        } catch (MalformedURLException | AlreadyBoundException e) {
            System.out.println("java RMI registry already exists.");
		}
		
		peer.ReplicateLocalFiles("192.168.1.2");
		
	}
}
