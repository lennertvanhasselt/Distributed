import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws ClassNotFoundException, RemoteException {
		
		boolean exit = false;
		int choice=0;
		Scanner scan = new Scanner(System.in);
		
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
		
		while(exit == false)
		{
			choice = peer.menu(scan);
			switch(choice){
			case 1:
				peer.listAllFiles("c:/temp/");
				break;
			case 2:
				peer.ReplicateLocalFiles("192.168.1.12");
				break;
			case 3:
				break;
			case 4:
				exit = true;
				break;
			default:
				exit = false;
				break;
			}
		}
		scan.close();	
	}
}
