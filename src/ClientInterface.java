import java.net.InetAddress;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote{
	int setNode(String clientName, InetAddress IP)throws RemoteException;
}
