
public class FileInfo {
	
	public String nameFile;
	public int originalOwnerNode;
	public int downloaded;
	public int replicateNode;
	
	public FileInfo(String nameFile, int originalOwnerNode, int replicateNode, int downloaded){
		this.nameFile = nameFile;
		this.originalOwnerNode = originalOwnerNode;
		this.replicateNode = replicateNode;
		this.downloaded = downloaded;
	}
	
	public void setNameFile(String nameFile) {
		this.nameFile = nameFile;
	}
	
	public void setOriginalOwnerNode(int originalOwnerNode) {
		this.originalOwnerNode = originalOwnerNode;
	}
	
	public void setReplicateNode(int replicateNode){
		this.replicateNode = replicateNode;
	}
	
	public void incrementDownloaded() {
		downloaded++;
	}
	
	public void decrementDownloaded() {
		if (downloaded != 0)
			downloaded--;
	}
	
	public String getNameFile() {
		return nameFile;
	}
	
	public int getOriginalOwnerNode() {
		return originalOwnerNode;
	}
	
	public int getReplicateNode(){
		return replicateNode;
	}
	
	public int getDownloaded() {
		return downloaded;
	}
}
