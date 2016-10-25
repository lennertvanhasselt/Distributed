package node1;

import java.util.Scanner;
import java.net.InetAddress;
import java.rmi.*;
import node1.ClientInterface;

public class Node1 {

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		String nodename;
		InetAddress IP = null;
		
		try {
			String name = "Moet nog aangepast worden";
			ClientInterface ci = (ClientInterface) Naming.lookup(name);
			
			System.out.println("Give the name of the node: ");
			nodename=scan.nextLine();
			scan.close();
			IP = InetAddress.getLocalHost();	
			ci.setNode(name, IP);
			
		} catch(Exception e) {
	         System.err.println("FileServer exception: "+ e.getMessage());
	         e.printStackTrace();
	    }
	}

}
