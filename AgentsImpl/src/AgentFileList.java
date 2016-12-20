
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class AgentFileList implements Runnable, Serializable {
	private static final long serialVersionUID = 1L;
	private Node nodeagent;
	private ArrayList<FileInfo> TotalFileList = new ArrayList<FileInfo>();

	public AgentFileList(Node node)
	{
		this.nodeagent = node;
	}
	@Override
	public void run() {	
		System.out.println("#zwam");
		update();
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
	
//	public void setNode(Node node){
//		this.nodeagent=node;
//	}

}
