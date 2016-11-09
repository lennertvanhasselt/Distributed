import java.net.InetAddress;
import java.rmi.Remote;
import java.rmi.RemoteException;

// Interface for RMI.
public interface NodeInterface {
	void changePrevNext(int nextNode, int previousNode, String nextIP, String previousIP)throws RemoteException, ClassNotFoundException;
	void setPreviousNode(int previousNode, String previousIP)throws RemoteException, ClassNotFoundException;
	void setNextNode(int nextNode, String nextIP)throws RemoteException, ClassNotFoundException;
	void setNameServer(String ip, int ownNode, int totalNodes);
}