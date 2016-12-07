import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class RMIStarter implements Runnable  {
	
	private NodeInterface nf;
	private Agent agent;
	
	public RMIStarter(Agent agent){
		this.agent = agent;
	}
	
	public void run(){
		try {
			nf = (NodeInterface) Naming.lookup("//192.168.1.12/Node");
			nf.startAgent(agent);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
