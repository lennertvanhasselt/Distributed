
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
	private ArrayList<String> totalFileList = new ArrayList<String>();
	private int i;

	public AgentFileList()
	{
		
	}
	@Override
	public void run() {	
		try {
			nodeagent.replicateNewFiles();
			update();
			nodeagent.setTotalFileList(totalFileList);
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
			i = nodeagent.deletedFiles.size();
			for(int j=i-1;j>=0;j--) {
				if(totalFileList.remove(nodeagent.deletedFiles.get(j))) { //returns true if deleted, false if not present
					nodeagent.deletedFiles.remove(j);
				} else { System.out.println("File in deletedFiles does not exist:  "+nodeagent.deletedFiles.get(j)); }
			}
		}
		i=nodeagent.replicatedFiles.size();
		String repFileName;
		for(int j=0;j<i;j++) {
			repFileName = nodeagent.replicatedFiles.get(j).getNameFile();
			if(!totalFileList.contains(repFileName)) { //returns true if deleted, false if not present
				totalFileList.add(repFileName);
			}
		}

	}
	
	public void setNode(Node nodeUpdate){
		this.nodeagent=nodeUpdate;
	}

}
