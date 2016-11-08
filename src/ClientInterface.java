import java.net.InetAddress;
import java.rmi.Remote;
import java.rmi.RemoteException;

// Interface for RMI.
public interface ClientInterface extends Remote{
	int setNode(String clientName, InetAddress IP)throws RemoteException, ClassNotFoundException;
	InetAddress searchFile(String search)throws RemoteException, ClassNotFoundException;
	boolean deleteNode(int aNode)throws RemoteException, ClassNotFoundException;
}
