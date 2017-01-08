import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
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
			//give agent and start the agent on the next node
			nf = (NodeInterface) Naming.lookup("//"+nextIP+"/Node");
			nf.startAgentFileList(agent);
		} catch (MalformedURLException | NotBoundException e) {
			e.printStackTrace();
		} catch(RemoteException e){
			System.out.println("agent stopped");
		}		
	}

}
