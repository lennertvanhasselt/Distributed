package replication;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PeerInterFace extends Remote{
	public int setupTCPReceiver(String fileName, int fileLength)throws RemoteException, ClassNotFoundException;
}
