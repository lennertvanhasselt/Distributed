import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NodeInterface extends Remote{
	void startAgent(Agent agent) throws MalformedURLException, RemoteException, NotBoundException;
}
