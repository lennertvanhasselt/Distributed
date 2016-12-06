import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class CheckFileList implements Runnable{
	
	Node node;
	
	public CheckFileList(Node node){
		this.node=node;
	}
	
	public void run(){
		while(true){
			try {
				Thread.sleep(30000);
				System.out.println("30 seconds over, updating filelist...");
				node.replicateNewFiles();
			} catch (RemoteException | ClassNotFoundException | MalformedURLException | NotBoundException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}
