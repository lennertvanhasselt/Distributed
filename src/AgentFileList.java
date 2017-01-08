
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class AgentFileList implements Runnable, Serializable {
	private static final long serialVersionUID = 1L;
	private Node nodeagent;
	private ArrayList<FileInfo> totalFileList = new ArrayList<FileInfo>();
	private ArrayList<String> totalFileListStrings = new ArrayList<String>();
	private int i;

	public AgentFileList()
	{
		
	}
	@Override
	public void run() {	
		try {
			//Check local files if files have been deleted/added
			nodeagent.replicateNewFiles();
			//update TotalFileList
			update();
			//lock files
			checkLock();
			//unlock files
			checkUnLock();
			//send TotalFileList to node
			nodeagent.setTotalFileList(totalFileList);
			//reset nodeagent
			nodeagent=null;
		} catch (RemoteException | ClassNotFoundException | MalformedURLException | UnknownHostException
				| NotBoundException e) {
			e.printStackTrace();
		}
	}
	
	private void checkLock(){
		if(nodeagent.indexToLock!=-1){
			if(nodeagent.indexToLock>=0 && nodeagent.indexToLock < totalFileList.size()){
				totalFileList.get(nodeagent.indexToLock).lock();
				nodeagent.indexToLock=-1;
			}
		}
	}
	
	private void checkUnLock(){
		if(nodeagent.indexToUnlock!=-1){
			if(nodeagent.indexToUnlock>=0 && nodeagent.indexToUnlock < totalFileList.size()){
				totalFileList.get(nodeagent.indexToUnlock).unLock();
				nodeagent.indexToUnlock=-1;
			}
		}
	}
	
	private void update()
	{

		//this loop deletes the files that are in deletedfiles from the total filelist
		if(!nodeagent.deletedFiles.isEmpty()){
			for(int j=0; j<totalFileList.size();j++){
				if(nodeagent.deletedFiles.contains(totalFileList.get(j).getNameFile())){
					totalFileList.remove(j);
					totalFileListStrings.remove(j);
				}
			}
			nodeagent.deletedFiles.clear();
		}
		
		//this loop adds new files from the replicated file list to the totalFileList
		i=nodeagent.replicatedFiles.size();
		String repFileName;
		for(int j=0;j<i;j++) {
			repFileName = nodeagent.replicatedFiles.get(j).getNameFile();
			if(!totalFileListStrings.contains(repFileName)) { //returns true if deleted, false if not present
				totalFileList.add(nodeagent.replicatedFiles.get(j));
				totalFileListStrings.add(nodeagent.replicatedFiles.get(j).getNameFile());
			}
		}
		
		//this loop replaces the Fileinfo's of localFileList with the updated fileInfo's from TotalFileList
		for(int j = 0; j<totalFileList.size();j++){
			if (totalFileList.get(j).getOriginalOwnerNode().firstKey()==nodeagent.getOwnNode())
			{
				for(int k = 0; k<nodeagent.localFiles.size();k++){
					if(totalFileList.get(j).getNameFile().equals(nodeagent.localFiles.get(k).getNameFile())){
						nodeagent.localFiles.set(k, totalFileList.get(j)); 
					}
				}			
			}
		}
	}
	
	public void setNode(Node nodeUpdate){
		this.nodeagent=nodeUpdate;
	}

}
