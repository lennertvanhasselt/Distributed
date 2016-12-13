package replication;
import java.io.File;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class Peer extends UnicastRemoteObject implements PeerInterFace{
	
	private static final long serialVersionUID = 1L;
	ArrayList<String> results;
	File[] files;
	
	public Peer() throws ClassNotFoundException, RemoteException{
		results = new ArrayList<String>();	
	}
	
	public int menu(Scanner scan)
	{
		System.out.println("                  Menu                  ");
		System.out.println("----------------------------------------");
		System.out.println("1) listAllFiles");
		System.out.println("2) replicate local file");
		System.out.println("3) ");
		System.out.println("4) ");
		System.out.println("Give your choice: ");
		int menuChoice = scan.nextInt();
		return menuChoice;	
	}
	
	public void listAllFiles(String directory){
		files = new File(directory).listFiles();
		for(File file : files){
			if(file.isFile()){
				results.add(file.getName());
			}
		}
		
		Iterator<String> it = results.iterator();
		
		while(it.hasNext()){
			System.out.println(it.next());
		}
	}
	
	public void ReplicateLocalFiles(String ipToSend){
		String fileName;
		int fileLength;
		File f;
		
		results.clear();
		listAllFiles("c:/temp/");
		
		Iterator<String> it = results.iterator();
		while(it.hasNext()){
			fileName = it.next();
			f = new File("c:/temp/"+fileName);
			fileLength = (int) f.length();
			Thread thread = new Thread(new TCPSender(ipToSend,fileName,fileLength));	
			thread.start();
		}
	
	}
	
	public synchronized int setupTCPReceiver(String fileName, int fileLength)throws ClassNotFoundException{
		TCPReceiver rec = new TCPReceiver(fileName,fileLength);
		Thread thread = new Thread(rec);
		thread.start();
		
		while(rec.SOCKET_PORT==0){
			System.out.print("");
		}
		
		System.out.println("returning " + rec.SOCKET_PORT);
		return rec.SOCKET_PORT;
	}
}

