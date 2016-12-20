
import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;

public class AgentFileList implements Runnable, Serializable {
	private static final long serialVersionUID = 1L;
	private Node nodeagent;
	private NodeInterface nf;
	private ArrayList<FileInfo> TotalFileList = new ArrayList<FileInfo>();

	public AgentFileList()
	{
		
	}
	@Override
	public void run() {	
		System.out.println("#zwam");
		//update();
		try {
			System.out.println("print 4");
			nf = (NodeInterface) Naming.lookup("//"+nodeagent.getNextIP()+"/Node");
			nf.startAgentFileList(this);
		} catch (MalformedURLException | RemoteException | NotBoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("print 5");
		nodeagent=null;
	}
	
	private void update()
	{
		Iterator<FileInfo> it1 = nodeagent.deletedFiles.iterator();
		while(it1.hasNext()){
			if(TotalFileList.contains(it1.next())){
				TotalFileList.remove(it1.next());
				nodeagent.deletedFiles.remove(it1.next());
			}
		}
	
		Iterator<FileInfo> it2 = nodeagent.replicatedFiles.iterator();
		while(it2.hasNext()){
			if(!TotalFileList.contains(it2.next())){
				TotalFileList.add(it2.next());
			}
		}
	}
	
	public void setNode(Node nodeUpdate){
		this.nodeagent=nodeUpdate;
	}

}
