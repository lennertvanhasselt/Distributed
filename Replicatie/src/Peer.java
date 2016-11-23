import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Peer implements PeerInterFace{
	
	ArrayList<String> results;
	File[] files;
	
	public Peer(){
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
	
	public ArrayList<String> listAllFiles(String directory){
		files = new File(directory).listFiles();
		for(File file : files){
			if(file.isFile()){
				results.add(file.getName());
			}
		}
		return results;	
	}
	
	public void ReplicateLocalFiles(String ipToSend){
		String fileName = results.get(0);
		File f = files[0];
		int fileLength = (int) f.length();
		new Thread(new TCPSender(ipToSend,fileName,fileLength)).start();		
	}
	
	public void setupTCPReceiver(String fileName, int fileLength, String IpSender)throws ClassNotFoundException{
		new Thread(new TCPReceiver(fileName, fileLength, IpSender));
	}

}
