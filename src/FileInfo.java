
public class FileInfo {
	
	public String nameFile;
	public int originalOwnerNode;
	public int downloaded;
	
	public FileInfo(String nameFile, int originalOwnerNode, int downloaded){
		this.nameFile = nameFile;
		this.originalOwnerNode = originalOwnerNode;
		this.downloaded = downloaded;
	}
	
	public void setNameFile(String nameFile) {
		this.nameFile = nameFile;
	}
	
	public void setOriginalOwnerNode(int originalOwnerNode) {
		this.originalOwnerNode = originalOwnerNode;
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
	
	public int getDownloaded() {
		return downloaded;
	}
}
