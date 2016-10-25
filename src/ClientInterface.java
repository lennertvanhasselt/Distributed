import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote{
	int setNode(String clientName)throws RemoteException;
}
