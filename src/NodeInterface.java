import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

// Interface for RMI.
public interface NodeInterface extends Remote{
	void changePrevNext(int nextNode, int previousNode, String nextIP, String previousIP)throws RemoteException, ClassNotFoundException, MalformedURLException, NotBoundException, UnknownHostException;
	void setPreviousNode(int previousNode, String previousIP)throws RemoteException, ClassNotFoundException;
	void setNextNode(int nextNode, String nextIP)throws RemoteException, ClassNotFoundException;
	void setNameServer(String ip, int ownNode, int totalNodes)throws RemoteException, ClassNotFoundException, UnknownHostException;
	void checkUpdate()throws RemoteException, ClassNotFoundException;
	int setupTCPReceiver(String fileName, int fileLength)throws RemoteException;
	void deleteFile(FileInfo fileInfo)throws RemoteException;
	//void constructReplicatedList() throws RemoteException;
	boolean newEntryReplicatedFiles(FileInfo fi) throws RemoteException, UnknownHostException, MalformedURLException, NotBoundException;
	void startAgentFileList(AgentFileList agent) throws RemoteException, MalformedURLException, NotBoundException;
}