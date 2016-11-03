import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

//GROUP 8 FTW zwam

public class Main {

	public static void main(String[] args) throws ClassNotFoundException, IOException {
		ClientInfo cli = new ClientInfo();
		String bindLocation = "//localhost/cliNode";
		
		ListNodes list = new ListNodes();
		
		try{
			LocateRegistry.createRegistry(1099);
			Naming.bind(bindLocation, cli);
	        System.out.println("NodeServer is ready at:" + bindLocation);
            System.out.println("java RMI registry created.");
        } catch (MalformedURLException | AlreadyBoundException e) {
            System.out.println("java RMI registry already exists.");
		}
		/*		Original code
		Scanner sc = new Scanner(System.in);
		String filename;
		
		System.out.println("Name of the node: ");
		filename = sc.nextLine();
		
		int length = filename.length();
		
		char[] fileChars = filename.toCharArray();
		
		int sum=0;
		for(int i=0;i<length;i++)
			sum=sum*97+(i+1)*Character.getNumericValue(fileChars[i]);
		
		sum = Math.abs(sum)%32768;
		
		System.out.println(sum);
		
		TreeMap<Integer,InetAddress> Table = new TreeMap<Integer,InetAddress>();
		//TreeMap zodat het direct geordend staat
		
		
		
		sc.close();
		*/
	}

}
