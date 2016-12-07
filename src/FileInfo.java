import java.io.Serializable;
import java.net.InetAddress;
import java.util.TreeMap;

public class FileInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public String nameFile;
	public TreeMap<Integer,InetAddress> originalOwnerNode;
	public int downloaded;
	public TreeMap<Integer,InetAddress> replicateNode;
	
	public FileInfo(String nameFile, TreeMap<Integer,InetAddress> originalOwnerNode){
		this.nameFile = nameFile;
		this.originalOwnerNode = originalOwnerNode;
		replicateNode = null;
		downloaded = 0;
	}
	
	public void setNameFile(String nameFile) {
		this.nameFile = nameFile;
	}
	
	public void setOriginalOwnerNode(TreeMap<Integer,InetAddress> originalOwnerNode) {
		this.originalOwnerNode = originalOwnerNode;
	}
	
	public void setReplicateNode(TreeMap<Integer,InetAddress> replicateNode){
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
	
	public TreeMap<Integer,InetAddress> getOriginalOwnerNode() {
		return originalOwnerNode;
	}
	
	public TreeMap<Integer,InetAddress> getReplicateNode(){
		return replicateNode;
	}
	
	public int getDownloaded() {
		return downloaded;
	}
}
