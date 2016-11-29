import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class checkFileList implements Runnable{
	
	Node node;
	
	public checkFileList(Node node){
		this.node=node;
	}
	
	public void run(){
		while(true){
			try {
				System.out.println("30 seconds over, updating filelist...");
				node.replicateNewFiles();
				Thread.sleep(10000);
			} catch (RemoteException | ClassNotFoundException | MalformedURLException | NotBoundException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}