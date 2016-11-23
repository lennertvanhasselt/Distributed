import java.net.Socket;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PeerInterFace extends Remote{
	public void setupTCPReceiver(String fileName, int fileLength, String ipSender)throws RemoteException, ClassNotFoundException;
}
