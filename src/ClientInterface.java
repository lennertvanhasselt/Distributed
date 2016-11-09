import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

// Interface for RMI.
public interface ClientInterface extends Remote{
	int setNode(String clientName, InetAddress IP)throws RemoteException, ClassNotFoundException, MalformedURLException, NotBoundException, IOException;
	InetAddress searchFile(String search)throws RemoteException, ClassNotFoundException;
	boolean deleteNode(int aNode)throws RemoteException, ClassNotFoundException;
}
