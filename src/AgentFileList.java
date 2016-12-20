
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;

public class AgentFileList implements Runnable, Serializable {
	private static final long serialVersionUID = 1L;
	private Node nodeagent;
	private ArrayList<String> TotalFileList = new ArrayList<String>();


	public AgentFileList()
	{
		
	}
	@Override
	public void run() {	
		try {
			System.out.println("#zwam");
			nodeagent.replicateNewFiles();
			update();
			nodeagent=null;
		} catch (RemoteException | ClassNotFoundException | MalformedURLException | UnknownHostException
				| NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void update()
	{
		if(!nodeagent.deletedFiles.isEmpty()){
			Iterator<String> it1 = nodeagent.deletedFiles.iterator();
			int i = nodeagent.deletedFiles.size();
			for(int j=i-1;j>=0;j--) {
				if(TotalFileList.remove(nodeagent.deletedFiles.get(j))) { //returns true if deleted, false if not present
					nodeagent.deletedFiles.remove(j);
				} else { System.out.println("File in deletedFiles does not exist:  "+nodeagent.deletedFiles.get(j)); }
			}
		
			Iterator<FileInfo> it2 = nodeagent.replicatedFiles.iterator();
			i=nodeagent.replicatedFiles.size();
			for(int j=i-1;j>=0;j--) {
				if(TotalFileList.remove(nodeagent.replicatedFiles.get(j))) { //returns true if deleted, false if not present
					nodeagent.replicatedFiles.remove(j);
				} else {
					System.out.println("File in deletedFiles does not exist:  "+nodeagent.replicatedFiles.get(j));
				}
			}
		}

	}
	
	public void setNode(Node nodeUpdate){
		this.nodeagent=nodeUpdate;
	}

}
