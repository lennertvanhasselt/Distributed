
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class AgentFileList implements Runnable, Serializable {
	private Node node;
	private ArrayList<FileInfo> TotalFileList = new ArrayList<FileInfo>();
	public AgentFileList(Node node)
	{
		this.node = node;
	}
	private static final long serialVersionUID = 1L;
	@Override
	public void run() {	
		System.out.println("#zwam");
		update();
	}
	
	private void update()
	{
		Iterator<FileInfo> it1 = node.deletedFiles.iterator();
		while(it1.hasNext()){
			if(TotalFileList.contains(it1.next())){
				TotalFileList.remove(it1.next());
	            node.deletedFiles.remove(it1.next());
			}
		}
	
		Iterator<FileInfo> it2 = node.replicatedFiles.iterator();
		while(it2.hasNext()){
			if(!TotalFileList.contains(it2.next())){
				TotalFileList.add(it2.next());
			}
		}
		
	}

}
