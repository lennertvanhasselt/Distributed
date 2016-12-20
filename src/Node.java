import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeMap;


public class Node extends UnicastRemoteObject implements NodeInterface, Serializable {

	private static final long serialVersionUID = 1L;
	private int previousNode = -1, nextNode = -1, totalNodes = -1;
	public int ownNode;
	private String ownIP = null, previousIP= null, nextIP = null;
	private NodeInterface nf;
	private ClientInterface cf;
	public String mainServer;
	public boolean check;
	public ArrayList<FileInfo> replicatedFiles;
	public ArrayList<FileInfo> localFiles;
	public ArrayList<String> deletedFiles;
	public Boolean serverSet = false;

	public Node() throws ClassNotFoundException, IOException, RemoteException {	
		mainServer = "";
		check = false;
		localFiles = new ArrayList<FileInfo>();
		replicatedFiles = new ArrayList<FileInfo>();
		deletedFiles = new ArrayList<String>();
	}

	// This is the menu that will appear on the console ones the connection with the server is established.
	public int menu(Scanner scan)
	{
		System.out.println("                  Menu                  ");
		System.out.println("----------------------------------------");
		System.out.println("1) Search file");
		System.out.println("2) check Previous - next node");
		System.out.println("3) replicate local files");
		System.out.println("4) Exit and delete node");
		System.out.println("Give your choice: ");
		int menuChoice = scan.nextInt();
		return menuChoice;	
	}

	// The option where the user can give a filename and receive the IP of the node who has that file.
	// When the node has received the IP of the location it will start to ping this node to make sure it has connection with it.
	// If the connection is not working and it can't find the node, an update of the network will take place with the node deleted. 
	public InetAddress searchFile(Scanner scan) throws ClassNotFoundException, IOException
	{
		System.out.println("Which file do you want?");
		String search = scan.nextLine();
		search = scan.nextLine();
		int nodeNumber=0;
		try {
			TreeMap<Integer,InetAddress> map = new TreeMap<Integer,InetAddress>();
			map = cf.searchFile(search);
			String IP = map.get(map.firstKey()).toString().substring(1);
			nodeNumber=map.firstKey();
			System.out.println("ip from searchFile: "+IP);
			nf = (NodeInterface) Naming.lookup("//" + IP + "/Node");
			nf.checkUpdate();

			// Ping the host of the file
			System.out.println(IP);
			Process p = Runtime.getRuntime().exec("ping "+IP);
			BufferedReader inputStream = new BufferedReader(
					new InputStreamReader(p.getInputStream()));
			String s = "";

			// reading output stream of the command
			while ((s = inputStream.readLine()) != null) {
				System.out.println(s);
			}
			return map.get(map.firstKey());	
		} catch(Exception e) {
			updateNetwork(nodeNumber);
			System.err.println("FileServer exception: "+ e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	// When the node decides to leave the menu and the network it will close and the server will delete the node from it's map.
	// The Previous and Next node in the system will have their previous or next node updated because the deleted node is not available anymore.
	public void deleteNode() throws ClassNotFoundException, IOException{
		int contactedNode = -1;
		try {
			if(previousNode != nextNode) {
				sendReplicatedFilesToPrevious();
			}
			
			if (ownNode != previousNode && ownNode != nextNode) {
				contactedNode = previousNode;
				System.out.println(previousIP);
				nf = (NodeInterface) Naming.lookup("//" + previousIP + "/Node");
				System.out.println("tot hier");
				
				nf.setNextNode(nextNode, nextIP);
				contactedNode = nextNode;
				nf = (NodeInterface) Naming.lookup("//" + nextIP + "/Node");
				nf.setPreviousNode(previousNode, previousIP);
				deleteLocalFiles();
			}
			Boolean answer = cf.deleteNode(ownNode);
			if (answer == true)
				System.out.println("Node deleted");
			else
				System.out.println("Node is not deleted");
			return;
		} catch (Exception e) {
			System.err.println("FileServer exception: " + e.getMessage());
			e.printStackTrace();
			updateNetwork(contactedNode);
			return;
		}
	}
	
	//Delete all your local files from the file system
	public void deleteLocalFiles() throws RemoteException, ClassNotFoundException, MalformedURLException, NotBoundException {
		int totalLocalFiles = localFiles.size();
		TreeMap<Integer,InetAddress> ownerFile;
		String ownerIP;
		
		for (int i = 0; i < totalLocalFiles; i++) {
			ownerFile = localFiles.get(i).getReplicateNode();
			ownerIP = ownerFile.get(ownerFile.firstKey()).toString().substring(1);
			nf = (NodeInterface) Naming.lookup("//" + ownerIP + "/Node");
			nf.deleteFile(localFiles.get(i));
		}
	}

	// The network will be updated if a node is not available, when it is not available it will be deleted and it's previous and next node will be informed and changed.
	// Their Previous or next node will be changed such that the deleted node will no longer be in the system.
	public void updateNetwork(int node) throws ClassNotFoundException, IOException {
		TreeMap<Integer, InetAddress> prevNext = cf.getPreviousNext(node);
		int nn = -1;
		int pn = -1;
		int first = prevNext.firstKey();
		int last = prevNext.lastKey();



		//Delete the corrupt node before checking others? Otherwise an error will result in not deleting the corrupt node
		cf.deleteNode(node); 
		System.out.println("Delete " + node);

		if (first < node && last > node) {
			pn = first;
			nn = last;
		} else if (first > node && last > node) {
			nn = first;
			pn = last;
		} else if (first < node && last < node) {
			nn = first;
			pn = last;
		} else
			System.out.println("error Node nn, pn");

		System.out.println("hier");

		// If previous node is not his own node
		if (pn != ownNode) {
			String IP = prevNext.get(pn).toString().substring(1);
			try {
				nf = (NodeInterface) Naming.lookup("//" + IP + "/Node");
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (NotBoundException e) {
				updateNetwork(pn);
				e.printStackTrace();
			}
			IP = prevNext.get(nn).toString().substring(1);
			nf.setNextNode(nn, IP);
		} else {
			nextNode=nn;
			nextIP = prevNext.get(nn).toString().substring(1);
		}

		// If next node is not his own node
		if (nn != ownNode) {
			String IP = prevNext.get(nn).toString().substring(1);
			try {
				nf = (NodeInterface) Naming.lookup("//" + IP + "/Node");
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (NotBoundException e) {
				updateNetwork(nn);
				e.printStackTrace();
			}
			IP = prevNext.get(pn).toString().substring(1);
			nf.setPreviousNode(pn, IP);
		} else {
			previousNode=pn;
			previousIP = prevNext.get(pn).toString().substring(1);
		}

		//cf.deleteNode(node);
		return;
	}

	// When receiving a multicast, the hashvalue will be calculated and used to change the next or previous Node and IP.
	// The change depends on how many nodes are in the system and what their previous and next are.
	public boolean hashing(String name, InetAddress IPraw) throws ClassNotFoundException, IOException
	{
		String IP = IPraw.toString();
		IP = IP.substring(1);
		int hashed = Math.abs((int) Integer.toUnsignedLong(name.hashCode()) % 32768);
		// number between 0 and 32768
		// to unsigned Long is to make it absolute
		try {
			// When no node is located in the system.
			// -->It will ad his own node and ip to his previous and next
			if (previousNode == -1 && nextNode == -1) {
				previousIP = IP;
				nextIP = IP;
				previousNode = ownNode;
				nextNode = ownNode;

				// When only 1 node is located in the system.
				// -->It will be added as the previous and the next node.
			} else if (ownNode == previousNode && ownNode == nextNode) {
				nf = (NodeInterface) Naming.lookup("//" + IP + "/Node");
				System.out.println("ownNode: "+ownNode+"  ownIP: "+ownIP+ "send to "+IP);
				nf.changePrevNext(ownNode, ownNode, ownIP, ownIP);
				previousNode = hashed;
				nextNode = hashed;
				previousIP = IP;
				nextIP = IP;
				System.out.println("nextNode: "+this.nextNode);
				System.out.println("nextIP: "+this.nextIP);
				System.out.println("previousNode: "+this.previousNode);
				System.out.println("previousIP: "+this.previousIP);
				replicateLocalFiles();

				// When 2 nodes are located in the system.
			} else if(nextNode == previousNode) {
				if (nextNode < ownNode) {

					if (hashed < nextNode) {
						//checkOwnedFiles(hashed, IP);
						nf = (NodeInterface) Naming.lookup("//" + IP + "/Node");
						nf.changePrevNext(nextNode, ownNode, nextIP, ownIP);
						nextNode = hashed;
						nextIP = IP;
						System.out.println("nextNode: "+this.nextNode);
						System.out.println("nextIP: "+this.nextIP);
						updateFiles();
					}

					else if (hashed > nextNode && hashed < ownNode) {
						previousNode = hashed;
						previousIP = IP;
						System.out.println("previousNode: "+this.previousNode);
						System.out.println("previousIP: "+this.previousIP);
					}

					else if (hashed == nextNode && hashed < ownNode) {
						hashed++;
						previousNode = hashed;
						previousIP = IP;
						System.out.println("previousNode: "+this.previousNode);
						System.out.println("previousIP: "+this.previousIP);
					}

					else if (hashed > ownNode) {
						//checkOwnedFiles(hashed, IP);
						nf = (NodeInterface) Naming.lookup("//" + IP + "/Node");
						nf.changePrevNext(nextNode, ownNode, nextIP, ownIP);
						nextNode = hashed;
						nextIP = IP;
						System.out.println("nextNode: "+this.nextNode);
						System.out.println("nextIP: "+this.nextIP);
						updateFiles();
					}

					else if (hashed == ownNode) {
						hashed++;
						//checkOwnedFiles(hashed, IP);
						nf = (NodeInterface) Naming.lookup("//" + IP + "/Node");
						nf.changePrevNext(nextNode, ownNode, nextIP, ownIP);
						nextNode = hashed;
						nextIP = IP;
						System.out.println("nextNode: "+this.nextNode);
						System.out.println("nextIP: "+this.nextIP);
						updateFiles();
					}
				} else  {

					if (hashed < ownNode) {
						previousNode = hashed;
						previousIP = IP;
						System.out.println("previousNode: "+this.previousNode);
						System.out.println("previousIP: "+this.previousIP);
					}

					else if (hashed > ownNode && hashed < nextNode) {
						//checkOwnedFiles(hashed, IP);
						nf = (NodeInterface) Naming.lookup("//" + IP + "/Node");
						nf.changePrevNext(nextNode, ownNode, nextIP, ownIP);
						nextNode = hashed;
						nextIP = IP;
						System.out.println("nextNode: "+this.nextNode);
						System.out.println("nextIP: "+this.nextIP);
						updateFiles();
					}

					else if (hashed == ownNode && hashed < nextNode) {
						hashed++;
						//checkOwnedFiles(hashed, IP);
						nf = (NodeInterface) Naming.lookup("//" + IP + "/Node");
						nf.changePrevNext(nextNode, ownNode, nextIP, ownIP);
						nextNode = hashed;
						nextIP = IP;
						System.out.println("nextNode: "+this.nextNode);
						System.out.println("nextIP: "+this.nextIP);
						updateFiles();
					}

					else if (hashed > nextNode) {
						previousNode = hashed;
						previousIP = IP;
						System.out.println("previousNode: "+this.previousNode);
						System.out.println("previousIP: "+this.previousIP);
					}

					else if (hashed == nextNode) {
						hashed++;
						previousNode = hashed;
						previousIP = IP;
						System.out.println("previousNode: "+this.previousNode);
						System.out.println("previousIP: "+this.previousIP);
					}
				}			
			}
			else {
				if (previousNode > ownNode) {
					if (hashed < ownNode || hashed > previousNode) {
						previousNode = hashed;
						previousIP = IP;
						System.out.println("previousNode: "+this.previousNode);
						System.out.println("previousIP: "+this.previousIP);
					}

					else if (hashed < nextNode && hashed > ownNode) {
						//checkOwnedFiles(hashed, IP);
						nf = (NodeInterface) Naming.lookup("//" + IP + "/Node");
						nf.changePrevNext(nextNode, ownNode, nextIP, ownIP);
						nextNode = hashed;
						nextIP = IP;
						System.out.println("nextNode: "+this.nextNode);
						System.out.println("nextIP: "+this.nextIP);
						updateFiles();
					}
				}

				else if (previousNode < ownNode && nextNode > ownNode) {
					if (hashed > previousNode && hashed < ownNode) {
						previousNode = hashed;
						previousIP = IP;
						System.out.println("previousNode: "+this.previousNode);
						System.out.println("previousIP: "+this.previousIP);
					}

					else if (hashed > ownNode && hashed < nextNode) {
						//checkOwnedFiles(hashed, IP);
						nf = (NodeInterface) Naming.lookup("//" + IP + "/Node");
						nf.changePrevNext(nextNode, ownNode, nextIP, ownIP);
						nextNode = hashed;
						nextIP = IP;
						System.out.println("nextNode: "+this.nextNode);
						System.out.println("nextIP: "+this.nextIP);
						updateFiles();
					}					
				}

				else if (nextNode < ownNode) {
					if (hashed > ownNode || hashed < nextNode) {
						//checkOwnedFiles(hashed, IP);
						nf = (NodeInterface) Naming.lookup("//" + IP + "/Node");
						nf.changePrevNext(nextNode, ownNode, nextIP, ownIP);
						nextNode = hashed;
						nextIP = IP;
						System.out.println("nextNode: "+this.nextNode);
						System.out.println("nextIP: "+this.nextIP);
						updateFiles();
					}

					else if (hashed > previousNode && hashed < ownNode) {
						previousNode = hashed;
						previousIP = IP;
						System.out.println("previousNode: "+this.previousNode);
						System.out.println("previousIP: "+this.previousIP);
					}					
				}
			}

			return true;

		} catch (NotBoundException e) {
			updateNetwork(hashed);
			e.printStackTrace();
			return false;
		}
	}

	// First initialisation of the node when it is connected with the server.
	// It receives it's ip, node and total amounts of nodes located in the system
	public void setNameServer(String ip, int ownNode, int totalNodes) throws RemoteException, ClassNotFoundException, UnknownHostException
	{
		System.out.println("SetNameServer");
		mainServer = ip;
		this.ownNode = ownNode;
		this.totalNodes = totalNodes;
		System.out.println(mainServer);
		System.out.println(this.ownNode);
		System.out.println(this.totalNodes);

		InetAddress address = InetAddress.getLocalHost();
		address = InetAddress.getByName(address.getHostAddress());
		ownIP = address.toString().substring(1);

		System.out.println("setNameServer IP: "+ownIP);

		if (nextNode == -1 && previousNode == -1)
		{
			previousIP = ownIP;
			nextIP = ownIP;
			previousNode = ownNode;
			nextNode = ownNode;
		}

		check = true;
	}

	// Changing the previous and the next node and ip for this certain node.
	public void changePrevNext(int nextNode, int previousNode, String nextIP, String previousIP)throws RemoteException, ClassNotFoundException, MalformedURLException, NotBoundException, UnknownHostException
	{
		System.out.println("changePrevNext");
		setNextNode(nextNode, nextIP);
		setPreviousNode(previousNode,previousIP);
		while(!serverSet) {
			System.out.print("");
		}
		replicateLocalFiles();
	}

	// Sets total nodes
	public void setTotalNodes(int totalNodes)
	{
		this.totalNodes = totalNodes;
	}

	// Sets ClientInterface
	public void setClientInterface(ClientInterface cf)
	{
		this.cf = cf;
		serverSet = true;
		System.out.println("setClientInterface");
	}

	// Used to display that someone is checking a file on your node.
	public void checkUpdate() throws RemoteException, ClassNotFoundException {
		System.out.println("Someone is checking your files... weird...");

	}

	// Used to get previous node
	public int getPreviousNode()
	{
		return previousNode;
	}

	// Used to get next node
	public int getNextNode()
	{
		return nextNode;
	}

	// Used to get own node
	public int getOwnNode()
	{
		return ownNode;
	}

	// Set the ip and node of the previous node.
	public void setPreviousNode(int previousNode, String previousIP)throws RemoteException, ClassNotFoundException
	{	
		this.previousNode = previousNode;
		this.previousIP = previousIP;
		System.out.println("set previous Node");
		System.out.println("previousNode: "+this.previousNode);
		System.out.println("previousIP: "+this.previousIP);
	}

	// Set the ip and node of the next node.
	public void setNextNode(int nextNode, String nextIP)throws RemoteException, ClassNotFoundException
	{
		this.nextNode = nextNode;
		this.nextIP = nextIP;
		System.out.println("set next Node");
		System.out.println("nextNode: "+this.nextNode);
		System.out.println("nextIP: "+this.nextIP);
	}	
	
	//when entering the system the local files will be replicated
	public void replicateLocalFiles() throws RemoteException, ClassNotFoundException, MalformedURLException, NotBoundException, UnknownHostException{
		File[] fileArray = new File("C:/temp/local/").listFiles();
		TreeMap<Integer, InetAddress> me = new TreeMap<Integer, InetAddress>();
		InetAddress address = InetAddress.getLocalHost();
	 	address = InetAddress.getByName(address.getHostAddress());
		me.put(ownNode, address);
		FileInfo fileInfo;
		for(File file : fileArray){
			if(file.isFile()){
				fileInfo = new FileInfo(file.getName(), me);
				localFiles.add(fileInfo);
			}
		}
		
		Iterator<FileInfo> it = localFiles.iterator();
		
		if(previousNode == ownNode){
			return;
		}
		
		String ipToSend;
		String fileName;
		FileInfo fi;
		
		TreeMap<Integer,InetAddress> owner;
		
		while(it.hasNext()){
			fi = it.next();
			fileName = fi.getNameFile();
			owner = cf.searchFile(fileName);
			
			if(ownNode == owner.firstKey()){
				ipToSend=previousIP;
			} else {
				ipToSend=owner.get(owner.firstKey()).toString().substring(1);
			}
			
			System.out.println("send file " + fileName + " to " +ipToSend);
			nf = (NodeInterface) Naming.lookup("//" + ipToSend + "/Node");
			nf.newEntryReplicatedFiles(fi);
			new Thread(new TCPSender(ipToSend,fileName, true)).start();	
		}
	}
	
	public void newEntryReplicatedFiles(FileInfo fi) throws RemoteException, UnknownHostException {
		System.out.print("newEntryReplicatedFiles: ");
		System.out.println(fi.getNameFile());
		TreeMap<Integer, InetAddress> me = new TreeMap<Integer, InetAddress>();
		InetAddress address = InetAddress.getLocalHost();
	 	address = InetAddress.getByName(address.getHostAddress());
		me.put(ownNode, address);
		fi.setReplicateNode(me);
		replicatedFiles.add(fi);
		System.out.println("FileInfo: " + fi.getNameFile());
	}
	
	//This method 
	public void updateFiles() throws RemoteException, MalformedURLException, NotBoundException, UnknownHostException, ClassNotFoundException {
		System.out.println("updateFiles");
		int totalRepFiles = replicatedFiles.size();
		ArrayList<Integer> filesToRemove = new ArrayList<Integer>();
		int ownerFile;
		for (int i = 0; i < totalRepFiles; i++) {
			ownerFile = cf.searchFile(replicatedFiles.get(i).getNameFile()).firstKey();
			int hashFile = Math.abs((int) Integer.toUnsignedLong(replicatedFiles.get(i).getNameFile().hashCode()) % 32768);

			if(replicatedFiles.get(i).getOriginalOwnerNode().firstKey() != ownerFile)
			{
				if (hashFile > nextNode && hashFile < ownNode) {
					nf = (NodeInterface) Naming.lookup("//" + nextIP + "/Node");
					nf.newEntryReplicatedFiles(replicatedFiles.get(i));
					filesToRemove.add(i);
					Thread thread1 = new Thread(new TCPSender(nextIP,replicatedFiles.get(i).getNameFile(), false));
					thread1.start();
					try {
						thread1.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				else if (hashFile > nextNode || hashFile < ownNode) {
					nf = (NodeInterface) Naming.lookup("//" + nextIP + "/Node");
					nf.newEntryReplicatedFiles(replicatedFiles.get(i));
					filesToRemove.add(i);
					Thread thread2 = new Thread(new TCPSender(nextIP,replicatedFiles.get(i).getNameFile(), false));
					thread2.start();
					try {
						thread2.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}			
			}
		}
		
		for(int i = filesToRemove.size(); i>0; i--) {
			deleteFile(replicatedFiles.get(filesToRemove.get(i)));
			replicatedFiles.remove(filesToRemove.get(i));
		}
	}

	public void replicateNewFiles() throws RemoteException, ClassNotFoundException, MalformedURLException, NotBoundException, UnknownHostException {
		if(previousNode!=ownNode && previousNode!=-1) {
			ArrayList<FileInfo> templocalFiles = new ArrayList<FileInfo>();
			File[] fileArray = new File("C:/temp/local/").listFiles();
			FileInfo fi;
			TreeMap<Integer, InetAddress> me = new TreeMap<Integer, InetAddress>();
			InetAddress address = InetAddress.getLocalHost();
		 	address = InetAddress.getByName(address.getHostAddress());
			me.put(ownNode, address);
			for(File file : fileArray){
				if(file.isFile()){
					fi = new FileInfo(file.getName(), me);
					templocalFiles.add(fi);
				}
			}
			int tempTotalLocalFiles = templocalFiles.size();
			ArrayList<String> tempFileNames = new ArrayList<String>();
			for(int i=0; i < localFiles.size(); i++)
				tempFileNames.add(localFiles.get(i).getNameFile());

			for(int i=0; i < tempTotalLocalFiles; i++) {
				if (!tempFileNames.contains(templocalFiles.get(i).getNameFile())) {
					System.out.println("========================>  "+templocalFiles.get(i).getNameFile());
					String ipToSend;
					TreeMap<Integer,InetAddress> owner = cf.searchFile(templocalFiles.get(i).getNameFile());
					if(ownNode == owner.firstKey()){
						ipToSend=previousIP;
					} else {
						ipToSend=owner.get(owner.firstKey()).toString().substring(1);
					}
					new Thread(new TCPSender(ipToSend,templocalFiles.get(i).getNameFile(), true)).start();
				}
			}
			tempFileNames.clear();
			for(int i=0; i < templocalFiles.size(); i++)
				tempFileNames.add(templocalFiles.get(i).getNameFile());
			
			for(int i=0; i < localFiles.size(); i++) {
				if(!tempFileNames.contains(localFiles.get(i).getNameFile())) {
					System.out.println(localFiles.get(i).getNameFile()+" not found, deleting replicated...");
					String ipToSend;
					TreeMap<Integer,InetAddress> owner = cf.searchFile(localFiles.get(i).getNameFile());
					if(ownNode == owner.firstKey()){
						ipToSend=previousIP;
					} else {
						ipToSend=owner.get(owner.firstKey()).toString().substring(1);
					}
					//implement check for downloads here?
					
					nf = (NodeInterface) Naming.lookup("//" + ipToSend + "/Node");
					System.out.println("deleting "+localFiles.get(i).getNameFile()+" at "+ipToSend);
					nf.deleteFile(localFiles.get(i));
				}
			}
			localFiles = templocalFiles;
		} else {
			System.out.println("no other nodes...");
			if(!localFiles.isEmpty())
				localFiles.removeAll(localFiles);
		}
	}

	public void sendReplicatedFilesToPrevious() throws UnknownHostException, RemoteException, MalformedURLException, NotBoundException {
		int totalRepFiles = replicatedFiles.size();
		nf = (NodeInterface) Naming.lookup("//" + previousIP + "/Node");
		for(int i = 0; i < totalRepFiles; i++)
		{
			nf.newEntryReplicatedFiles(replicatedFiles.get(i));
			new Thread(new TCPSender(previousIP,replicatedFiles.get(i).getNameFile(), false)).start();
		}		
	}
	
	public int setupTCPReceiver(String fileName, int fileLength) throws RemoteException{
		TCPReceiver rec = new TCPReceiver(fileName,fileLength);
		Thread thread = new Thread(rec);
		thread.start();
		
		while(rec.SOCKET_PORT==0){
			System.out.print("");
		}
		
		System.out.println("returning " + rec.SOCKET_PORT);
		return rec.SOCKET_PORT;
	}
	
	//check if local files map on new next node
	public void checkOwnedFiles(int node, String ip) throws MalformedURLException, RemoteException, NotBoundException, UnknownHostException {
		int totalFiles = localFiles.size();
		int hashFile;
		
		
		for(int i = 0; i < totalFiles; i++)
		{
			hashFile = Math.abs((int) Integer.toUnsignedLong(localFiles.get(i).getNameFile().hashCode()) % 32768);
			
			//normal case
			if (hashFile >= node && hashFile < nextNode) {
				nf = (NodeInterface) Naming.lookup("//" + ip + "/Node");
				nf.newEntryReplicatedFiles(localFiles.get(i));
				Thread thread1 = new Thread(new TCPSender(ip,localFiles.get(i).getNameFile(), true));
				thread1.start();
				try {
					thread1.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				nf = (NodeInterface) Naming.lookup("//" + previousIP + "/Node");
				nf.deleteFile(localFiles.get(i));
			}
			// case: --nextNode-------------ownNode--------node------
			else if (ownNode < node && ownNode > nextNode){
				if (hashFile >= node || hashFile < nextNode){
					nf = (NodeInterface) Naming.lookup("//" + ip + "/Node");
					nf.newEntryReplicatedFiles(localFiles.get(i));
					Thread thread2 = new Thread(new TCPSender(ip,localFiles.get(i).getNameFile(), true));
					thread2.start();
					
					try {
						thread2.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					nf = (NodeInterface) Naming.lookup("//" + previousIP + "/Node");
					nf.deleteFile(localFiles.get(i));
				}
			}
		}
	}
	
	public void deleteFile(FileInfo fileInfo) throws RemoteException{
		//if(replicatedFiles.contains(fileInfo)) {
		System.out.println("Deleting replicated file: "+fileInfo.getNameFile());
		File file = new File("C:/temp/replicated/"+fileInfo.getNameFile());
		file.delete();
		//}
		return;
	}
	
	public void startAgentFileList(AgentFileList agent)throws RemoteException, MalformedURLException, NotBoundException{
		agent.setNode(this);
		Thread thread = new Thread(agent);
		System.out.println("print 1");
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("print 2");
		NodeInterface nf = (NodeInterface) Naming.lookup("//"+nextIP+"/Node");
		nf.startAgentFileList(agent);
	}
	
	/*TO COMPLETE
	public void constructReplicatedList() throws RemoteException {
		System.out.print(".");
		File[] fileArray = new File("C:/temp/replicated/").listFiles();
		ArrayList<FileInfo> replicatedFilesTemp = new ArrayList<FileInfo>();
		FileInfo fi;
		for(File file : fileArray){
			if(file.isFile()){
				fi = new FileInfo(file.getName(), );
				replicatedFilesTemp.add(fi);
			}
		}
		replicatedFiles = replicatedFilesTemp;
	}	*/

}