import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class RMIStarter implements Runnable{
	
	AgentFileList agent;
	String nextIP;
	public RMIStarter(AgentFileList agent, String nextIP){
		this.agent=agent;
		this.nextIP=nextIP;
	}

	public void run() {
		NodeInterface nf;
		try {
			nf = (NodeInterface) Naming.lookup("//"+nextIP+"/Node");
			nf.startAgentFileList(agent);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
