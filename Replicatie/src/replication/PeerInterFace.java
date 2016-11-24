package replication;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PeerInterFace extends Remote{
	public void setupTCPReceiver(String fileName, int fileLength)throws RemoteException, ClassNotFoundException;
}
