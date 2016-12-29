import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.TreeMap;

// Interface for RMI between Node and Server
public interface ClientInterface extends Remote{
	void setNode(String clientName, InetAddress IP)throws RemoteException, ClassNotFoundException, MalformedURLException, NotBoundException, IOException;
	TreeMap<Integer,InetAddress> searchFile(String search)throws RemoteException, ClassNotFoundException;
	boolean deleteNode(int aNode)throws RemoteException, ClassNotFoundException;
	TreeMap<Integer,InetAddress> getPreviousNext(int node)throws RemoteException, ClassNotFoundException, IOException;
}
