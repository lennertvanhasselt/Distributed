package node1;

import java.net.InetAddress;
import java.rmi.Naming;
import java.util.Scanner;
import java.net.InetAddress;
import java.rmi.*;
import node1.ClientInterface;

public class Node1 {

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
	 	String Nodename;
		try {
			String name = "//192.168.1.3/cliNode";
   	 		ClientInterface cf = (ClientInterface) Naming.lookup(name);
		
   	 		System.out.println("Give the name of the node: ");
   	 		Nodename=scan.nextLine();
   	 		
   	 		//InetAddress address = InetAddress.getByName("192.168.0.4");
   	 		
   	 		int ownNode = cf.setNode(Nodename, InetAddress.getLocalHost()); //not sure of this (could do manual? like ^)
   	 		
   	 		//HIER WHILE LUS MET OPTIES TOEVOEGEN OM TE BESTUREN
   	 		
   	 		scan.close();
	
		} catch(Exception e) {
	         System.err.println("FileServer exception: "+ e.getMessage());
	       e.printStackTrace();
	    }
	}

}
