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
	public ArrayList<FileInfo> totalFileList;
	public ArrayList<String> deletedFiles;
	public Boolean serverSet = false;
	public Thread lockThread;
	public FileLocker filelocker;

	public Node() throws ClassNotFoundException, IOException, RemoteException {	
		mainServer = "";
		check = false;
		localFiles = new ArrayList<FileInfo>();
		replicatedFiles = new ArrayList<FileInfo>();
		deletedFiles = new ArrayList<String>();
		totalFileList = new ArrayList<FileInfo>();
	}

	// The menu that will appear on the console ones the connection with the server is established.
	public int menu(Scanner scan)
	{
		System.out.println("                  Menu                  ");
		System.out.println("----------------------------------------");
		System.out.println("1) Search file");
		System.out.println("2) check Previous - next node");
		System.out.println("3) replicate local files");
		System.out.println("4) Exit and delete node");
		System.out.println("5) initial start agent");
		System.out.println("6) get Total files"); 
		System.out.println("Give your choice: ");
		int menuChoice = scan.nextInt();
		return menuChoice;	
	}

	// The option where the user can give a filename and receive the IP of the node who has that file.
	// When the node has received the IP of the location it will start to ping this node to make sure it has connection with it.
	// If there is no connection and it can't find the node, an update of the network will take place with the node deleted. 
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
			// When more than 2 nodes in system.
			if(previousNode != nextNode) {
				sendReplicatedFilesToPrevious();
			}
			// When more than 1 node in the system the previous and next has to be adjust
			if (ownNode != previousNode && ownNode != nextNode) {
				contactedNode = previousNode;
				System.out.println(previousIP);
				nf = (NodeInterface) Naming.lookup("//" + previousIP + "/Node");
				
				nf.setNextNode(nextNode, nextIP);
				contactedNode = nextNode;
				nf = (NodeInterface) Naming.lookup("//" + nextIP + "/Node");
				nf.setPreviousNode(previousNode, previousIP);
				deleteLocalFiles();
			}
			// Confirm that the node was deleted
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
				AgentFileList agent = new AgentFileList();
 				NodeInterface nf = (NodeInterface) Naming.lookup("//"+nextIP+"/Node");
 				nf.startAgentFileList(agent);

			// When 2 nodes are located in the system.
			} else if(nextNode == previousNode) {
				if (nextNode < ownNode) {
					
					// The new node will become the nextNode of the own node
					// -----------New----------Prev/Next---------------Own---------------
					if (hashed < nextNode) {
						//checkOwnedFiles(hashed, IP);
						nf = (NodeInterface) Naming.lookup("//" + IP + "/Node");
						nf.changePrevNext(nextNode, ownNode, nextIP, ownIP);
						nextNode = hashed;
						nextIP = IP;
						System.out.println("nextNode: "+this.nextNode);
						System.out.println("nextIP: "+this.nextIP);
						//updateFiles();
					}
					
					// The new node will become the previousNode of the own node
					// -----------Prev/Next----------New---------------Own---------------
					else if (hashed > nextNode && hashed < ownNode) {
						previousNode = hashed;
						previousIP = IP;
						System.out.println("previousNode: "+this.previousNode);
						System.out.println("previousIP: "+this.previousIP);
					}
					
					// The new node will become the previousNode of the own node because of hashvalue+1 
					// -----------Prev/Next-New----------Own------------------------------
					else if (hashed == nextNode && hashed < ownNode) {
						hashed++;
						previousNode = hashed;
						previousIP = IP;
						System.out.println("previousNode: "+this.previousNode);
						System.out.println("previousIP: "+this.previousIP);
					}
					
					// The new node will become the nextNode of the own node
					// -----------Prev/Next-------------Own----------New------------------
					else if (hashed > ownNode) {
						//checkOwnedFiles(hashed, IP);
						nf = (NodeInterface) Naming.lookup("//" + IP + "/Node");
						nf.changePrevNext(nextNode, ownNode, nextIP, ownIP);
						nextNode = hashed;
						nextIP = IP;
						System.out.println("nextNode: "+this.nextNode);
						System.out.println("nextIP: "+this.nextIP);
						//updateFiles();
					}
					
					// The new node will become the nextNode of the own node because of hashvalue+1 
					// -----------Prev/Next-------------Own-New---------------------------
					else if (hashed == ownNode) {
						hashed++;
						//checkOwnedFiles(hashed, IP);
						nf = (NodeInterface) Naming.lookup("//" + IP + "/Node");
						nf.changePrevNext(nextNode, ownNode, nextIP, ownIP);
						nextNode = hashed;
						nextIP = IP;
						System.out.println("nextNode: "+this.nextNode);
						System.out.println("nextIP: "+this.nextIP);
						//updateFiles();
					}
				} else  {
					
					// The new node will become the previousNode of the own node
					// -----------New-------------------------Own-------Prev/Next--------
					if (hashed < ownNode) {
						previousNode = hashed;
						previousIP = IP;
						System.out.println("previousNode: "+this.previousNode);
						System.out.println("previousIP: "+this.previousIP);
					}
					
					// The new node will become the nextNode of the own node
					// ---------------Own-----------------New-----------Prev/Next--------
					else if (hashed > ownNode && hashed < nextNode) {
						//checkOwnedFiles(hashed, IP);
						nf = (NodeInterface) Naming.lookup("//" + IP + "/Node");
						nf.changePrevNext(nextNode, ownNode, nextIP, ownIP);
						nextNode = hashed;
						nextIP = IP;
						System.out.println("nextNode: "+this.nextNode);
						System.out.println("nextIP: "+this.nextIP);
						//updateFiles();
					}
					
					// The new node will become the nextNode of the own node because of hashvalue +1
					// ---------------Own-New--------------------------Prev/Next--------
					else if (hashed == ownNode && hashed < nextNode) {
						hashed++;
						//checkOwnedFiles(hashed, IP);
						nf = (NodeInterface) Naming.lookup("//" + IP + "/Node");
						nf.changePrevNext(nextNode, ownNode, nextIP, ownIP);
						nextNode = hashed;
						nextIP = IP;
						System.out.println("nextNode: "+this.nextNode);
						System.out.println("nextIP: "+this.nextIP);
						//updateFiles();
					}
					
					// The new node will become the previousNode of the own node
					// ---------------Own-----------Prev/Next---------------------New---
					else if (hashed > nextNode) {
						previousNode = hashed;
						previousIP = IP;
						System.out.println("previousNode: "+this.previousNode);
						System.out.println("previousIP: "+this.previousIP);
					}
					
					// The new node will become the previousNode of the own node because of hashvalue +1
					// ---------------Own-----------Prev/Next-New-----------------------
					else if (hashed == nextNode) {
						hashed++;
						previousNode = hashed;
						previousIP = IP;
						System.out.println("previousNode: "+this.previousNode);
						System.out.println("previousIP: "+this.previousIP);
					}
				}
				// When only 2 nodes in system and a 3th one is joining we use updateFiles2()
				updateFiles2();
			}
			// When more then 2 nodes are located in the system
			else {
				if (previousNode > ownNode) {
					
					// The new node will become the previousNode of the own node
					// --(or New)---Own-----------Next--------------------------Prev----New----
					if (hashed < ownNode || hashed > previousNode) {
						previousNode = hashed;
						previousIP = IP;
						System.out.println("previousNode: "+this.previousNode);
						System.out.println("previousIP: "+this.previousIP);
					}
					
					// The new node will become the nextNode of the own node
					// -----Own------New-----Next--------------------------Prev--------
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
					
					// The new node will become the previousNode of the own node
					// ---Prev-----------New----------Own---------------Next-----------
					if (hashed > previousNode && hashed < ownNode) {
						previousNode = hashed;
						previousIP = IP;
						System.out.println("previousNode: "+this.previousNode);
						System.out.println("previousIP: "+this.previousIP);
					}
					
					// The new node will become the nextNode of the own node
					// ---Prev---------------------Own-------New--------Next-----------
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
					
					// The new node will become the nextNode of the own node
					// ---(or New)--Next-----------Prev------------Own-------New------------
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
					
					// The new node will become the previousNode of the own node
					// -----Next-----------Prev------New------Own----------------------
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
		
		// When no node is in the system at the moment
		if (nextNode == -1 && previousNode == -1)
		{
			previousIP = ownIP;
			nextIP = ownIP;
			previousNode = ownNode;
			nextNode = ownNode;
		}

		check = true;
	}

	// Changing the previous, next node and ip for this certain node.
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

	// Used to display that someone is checking a file on your node.
	public void checkUpdate() throws RemoteException, ClassNotFoundException {
		System.out.println("Someone is checking your files... weird...");
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
		
		// When only one node is in the system no replications have to be made
		if(previousNode == ownNode){
			return;
		}
		
		String ipToSend;
		String fileName;
		TreeMap<Integer,InetAddress> owner;
		TreeMap<Integer,InetAddress> previousNodeMap=new TreeMap<Integer,InetAddress>();
		
		for(int i = 0; i<localFiles.size(); i++){
			fileName = localFiles.get(i).getNameFile();
			owner = cf.searchFile(fileName);
			
			// When a file from ownNode maps on it's own it has to be send to the previous one
			if(ownNode == owner.firstKey()){
				ipToSend = previousIP;
				previousNodeMap.clear();
				previousNodeMap.put(previousNode, InetAddress.getByName(previousIP));
				localFiles.get(i).setReplicateNode(previousNodeMap);
				
			// When a file doesn't map on ownNode, it has to be send to it's specific location
			}else{
				ipToSend = owner.get(owner.firstKey()).toString().substring(1);
				localFiles.get(i).setReplicateNode(owner);
			}
			
			// Displaying from where to where it will be send
			System.out.println("send file " + fileName + " to " +ipToSend);
			nf = (NodeInterface) Naming.lookup("//" + ipToSend + "/Node");
			nf.newEntryReplicatedFiles(localFiles.get(i));
			new Thread(new TCPSender(ipToSend,fileName, true)).start();	
		}		
	}
	
	public boolean newEntryReplicatedFiles(FileInfo fi) throws RemoteException, UnknownHostException, MalformedURLException, NotBoundException {
		// When a replicated file maps on the original owner, it has to be sended to the previous node
		if(fi.getOriginalOwnerNode().firstKey() == ownNode)
		{
			nf = (NodeInterface) Naming.lookup("//" + previousIP + "/Node");
			nf.newEntryReplicatedFiles(fi);
			new Thread(new TCPSender(previousIP,fi.getNameFile(), true)).start();
			return false;
		}
		// When a replicated file maps on ownNode, it will make a list of replicated files
		else
		{
			System.out.print("newEntryReplicatedFiles: ");
			System.out.println(fi.getNameFile());
			TreeMap<Integer, InetAddress> me = new TreeMap<Integer, InetAddress>();
			InetAddress address = InetAddress.getLocalHost();
		 	address = InetAddress.getByName(address.getHostAddress());
			me.put(ownNode, address);
			fi.setReplicateNode(me);
			replicatedFiles.add(fi);
			System.out.println("FileInfo: " + fi.getNameFile());
			return true; 
		}
	}
	
	//check local files to replicate in system (and keep it updated)
	public void replicateNewFiles() throws RemoteException, ClassNotFoundException, MalformedURLException, NotBoundException, UnknownHostException {
		// When more than 1 node in system
		if(previousNode!=ownNode && previousNode!=-1) {
			ArrayList<FileInfo> templocalFiles = new ArrayList<FileInfo>();
			File[] fileArray = new File("C:/temp/local/").listFiles(); 		//get all files from local
			FileInfo fi;
			TreeMap<Integer, InetAddress> me = new TreeMap<Integer, InetAddress>(); 	
			InetAddress address = InetAddress.getLocalHost();
		 	address = InetAddress.getByName(address.getHostAddress());
			me.put(ownNode, address);		//TreeMap filled with own info
			for(File file : fileArray){		//separate every file to get names
				if(file.isFile()){
					fi = new FileInfo(file.getName(), me);  	
					templocalFiles.add(fi);
				}
			}
			int tempTotalLocalFiles = templocalFiles.size();
			ArrayList<String> tempFileNames = new ArrayList<String>();
			for(int i=0; i < localFiles.size(); i++)
				tempFileNames.add(localFiles.get(i).getNameFile()); 	//fill String ArrayList with all files in localFiles

			String ipToSend;
			//check for new files
			for(int i=0; i < tempTotalLocalFiles; i++) {
				if (!tempFileNames.contains(templocalFiles.get(i).getNameFile())) { 
					System.out.println("========================>  "+templocalFiles.get(i).getNameFile());
					//check where the hash of file is pointing to
					TreeMap<Integer,InetAddress> owner = cf.searchFile(templocalFiles.get(i).getNameFile());
					//if hash points to himself, send to previous
					if(ownNode == owner.firstKey()){
						ipToSend=previousIP;
					} else {
					//else use info gotten from server
						ipToSend=owner.get(owner.firstKey()).toString().substring(1);
					}
					nf = (NodeInterface) Naming.lookup("//" + ipToSend + "/Node");
					//add local file to replicated of node
					nf.newEntryReplicatedFiles(templocalFiles.get(i));
					new Thread(new TCPSender(ipToSend,templocalFiles.get(i).getNameFile(), true)).start();
				}
			}
			tempFileNames.clear();
			//now fill tempFileNames with names of files we found in map /local
			for(int i=0; i < templocalFiles.size(); i++)
				tempFileNames.add(templocalFiles.get(i).getNameFile());
			
			//check for deleted files
			for(int i=0; i < localFiles.size(); i++) {
				if(!tempFileNames.contains(localFiles.get(i).getNameFile())) {
					System.out.println(localFiles.get(i).getNameFile()+" not found, deleting replicated...");
					//check where the hash of file is pointing to
					TreeMap<Integer,InetAddress> owner = cf.searchFile(localFiles.get(i).getNameFile());
					//if hash points to himself, send to previous
					if(ownNode == owner.firstKey()){
						ipToSend=previousIP;
					} else {
						ipToSend=owner.get(owner.firstKey()).toString().substring(1);
					}
					nf = (NodeInterface) Naming.lookup("//" + ipToSend + "/Node");
					System.out.println("deleting "+localFiles.get(i).getNameFile()+" at "+ipToSend);
					//delete file at node
					nf.deleteFile(localFiles.get(i));
				}
			}
			//all changes have been dealt with so we change the old ArrayList to the new one.
			localFiles = templocalFiles;
		} else {
			//Node is alone, if localFiles is not empty, it gets emptied to reset the system
			System.out.println("no other nodes...");
			if(!localFiles.isEmpty())
				localFiles.removeAll(localFiles);
		}
	}
	
	// When the files maps on the original owner it has to be sent to the new owner which is the previousNode
	public void sendReplicatedFilesToPrevious() throws UnknownHostException, RemoteException, MalformedURLException, NotBoundException {
		int totalRepFiles = replicatedFiles.size();
		nf = (NodeInterface) Naming.lookup("//" + previousIP + "/Node");
		for(int i = totalRepFiles-1; i >= 0 ; i--)
		{
			if (nf.newEntryReplicatedFiles(replicatedFiles.get(i)))
				new Thread(new TCPSender(previousIP,replicatedFiles.get(i).getNameFile(), false)).start();
			
			deleteFile(replicatedFiles.get(i));
		}		
	}
	
	// Used to set up an TCP receive point
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
	
	// Used to delete a specific file located at ownNode on the system
	public void deleteFile(FileInfo fileInfo) throws RemoteException{
		//if(replicatedFiles.contains(fileInfo)) {
		String fileName = fileInfo.getNameFile();
		if(!replicatedFiles.remove(fileInfo)){
			int index=-1;
			for(int i=replicatedFiles.size()-1;i>=0;i--) {
				if(replicatedFiles.get(i).getNameFile().equals(fileName))
					index=i;
			}
			replicatedFiles.remove(index);
		}
		System.out.println("Deleting replicated file: "+fileName);
		File file = new File("C:/temp/replicated/"+fileName);
		file.delete();
		//}
		return;
	}
	
	// Used to start the agent
	public void startAgentFileList(AgentFileList agent)throws RemoteException, MalformedURLException, NotBoundException{
		agent.setNode(this);
		Thread thread = new Thread(agent);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// When only 1 node in the system, agent has to be set on hold
		if (ownNode != nextNode && ownNode != previousNode)
			new Thread(new RMIStarter(agent,nextIP)).start();
	}
	
	// Print the totalFileList
	public void printTotalFileList(){
		Iterator<FileInfo> it = totalFileList.iterator();
		int index = 0;
		while(it.hasNext()){
			System.out.println(index + ": file:" +it.next().getNameFile());
			index++;
		}
	}
	
	// Used to update the files when only 2 nodes are in the system and the 3th one joins
	public void updateFiles2() throws RemoteException, ClassNotFoundException, MalformedURLException, NotBoundException, UnknownHostException {
		int totalRepFiles = replicatedFiles.size();
		int ownerNode, originalOwner;
		for (int i = totalRepFiles-1; i >= 0; i--) {
			//get ownerNode of the specific replicated file
			ownerNode = cf.searchFile(replicatedFiles.get(i).getNameFile()).firstKey();
			originalOwner = replicatedFiles.get(i).getOriginalOwnerNode().firstKey();
			// If the ownNode isn't the owner of the replicated file
			if(ownNode != ownerNode){
				// If the originalOwner of the file is the nextNode
				if(originalOwner == nextNode) {
					// And if the owner of the replicated file is the previousNode --> send file to previousNode
					if(ownerNode == previousNode){
						nf = (NodeInterface) Naming.lookup("//" + previousIP + "/Node");
						nf.newEntryReplicatedFiles(replicatedFiles.get(i));
						Thread thread1 = new Thread(new TCPSender(previousIP,replicatedFiles.get(i).getNameFile(), false));
						thread1.start();
						try {
							thread1.join();
							deleteFile(replicatedFiles.get(i));
							replicatedFiles.remove(i);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}						
				}
				// If the originalOwner of the file is the previousNode
				else { 
					// And if the owner of the replicated file is the nextNode or previousNode --> send file to nextNode
					if(ownerNode == nextNode || ownerNode == previousNode) {
						nf = (NodeInterface) Naming.lookup("//" + nextIP + "/Node");
						nf.newEntryReplicatedFiles(replicatedFiles.get(i));
						Thread thread1 = new Thread(new TCPSender(nextIP,replicatedFiles.get(i).getNameFile(), false));
						thread1.start();
						try {
							thread1.join();
							deleteFile(replicatedFiles.get(i));
							replicatedFiles.remove(i);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}			
		}		
	}
	
	// Used to update the files when more than 3 nodes are in the system
	public void updateFiles() throws RemoteException, MalformedURLException, NotBoundException, UnknownHostException, ClassNotFoundException {
		System.out.println("updateFiles");
		int totalRepFiles = replicatedFiles.size();
		int ownerFile;
		for (int i = totalRepFiles-1; i >= 0; i--) {
			ownerFile = cf.searchFile(replicatedFiles.get(i).getNameFile()).firstKey();
			int hashFile = Math.abs((int) Integer.toUnsignedLong(replicatedFiles.get(i).getNameFile().hashCode()) % 32768);
			
			// If the owner of the replicated file is not the originalOwner or ownNode
			if(replicatedFiles.get(i).getOriginalOwnerNode().firstKey() != ownerFile || ownerFile != ownNode)
			{

				// -----------Next----------New--------Prev-------Own---------------
				// Send to the next
				if (hashFile > nextNode && hashFile < ownNode) {
					nf = (NodeInterface) Naming.lookup("//" + nextIP + "/Node");
					nf.newEntryReplicatedFiles(replicatedFiles.get(i));
					Thread thread1 = new Thread(new TCPSender(nextIP,replicatedFiles.get(i).getNameFile(), false));
					thread1.start();
					try {
						thread1.join();
						deleteFile(replicatedFiles.get(i));
						replicatedFiles.remove(i);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				}
				// -----------Next----------New--------Prev----(or New)---Own---------------
				// Send to the next
				else if (hashFile > nextNode || hashFile < ownNode) {
					nf = (NodeInterface) Naming.lookup("//" + nextIP + "/Node");
					nf.newEntryReplicatedFiles(replicatedFiles.get(i));
					Thread thread2 = new Thread(new TCPSender(nextIP,replicatedFiles.get(i).getNameFile(), false));
					thread2.start();
					try {
						thread2.join();
						deleteFile(replicatedFiles.get(i));
						replicatedFiles.remove(i);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}			
			}
		}
		
//		for(int i = filesToRemove.size(); i>0; i--) {
//			deleteFile(replicatedFiles.get(filesToRemove.get(i)));
//			replicatedFiles.remove(filesToRemove.get(i));
//		}
	}
	
	//this method will dowload files from other nodes
	public void downloadFile(int index) throws MalformedURLException, RemoteException, NotBoundException{
		filelocker = new FileLocker(index);
		lockThread = new Thread(filelocker);
		try {
			lockThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("File: " + totalFileList.get(index).getNameFile() + " is locked");
		
		
		Scanner scan = new Scanner(System.in);
		FileInfo fileToDownload = totalFileList.get(index);
		TreeMap<Integer,InetAddress> replicateNode = fileToDownload.getReplicateNode();
		TreeMap<Integer,InetAddress> originalOwnerNode = fileToDownload.getOriginalOwnerNode();
		String ipToReceive;
		
		if(!totalFileList.get(index).getLock()){
			if(replicateNode.firstKey()==ownNode){
				System.out.println("This file is already downloaded");
				System.out.print("Download anyway (y/n): ");  //maybe the file is corrupted on this node, download again
				if(scan.next().equals("y")){
					ipToReceive = replicateNode.firstEntry().getValue().getHostAddress();
					nf = (NodeInterface) Naming.lookup("//"+ipToReceive+"/node");
					nf.sendDownload(index, ipToReceive);
				}
			}else if(originalOwnerNode.firstKey()==ownNode){
				System.out.println("This file is already present on this node, in local");
				System.out.print("Download anyway (y/n): "); //maybe the users wants this file also in the replicated dir
				if(scan.next().equals("y")){
					ipToReceive = replicateNode.firstEntry().getValue().getHostAddress();
					nf = (NodeInterface) Naming.lookup("//"+ipToReceive+"/node");
					nf.sendDownload(index, ipToReceive);
				}
			}else{
				ipToReceive = replicateNode.firstEntry().getValue().getHostAddress();
				nf = (NodeInterface) Naming.lookup("//"+ipToReceive+"/node");
				nf.sendDownload(index, ipToReceive);
			}		
		}else System.out.println("this file is locked");
		

		scan.close();
	}
	
	//this method is called when another node wants to download a file from your node
	//this method will then send the file to the node who is calling this method
	public void sendDownload(int index, String ipToSend)throws RemoteException, MalformedURLException, NotBoundException{
		FileInfo fileToSend = totalFileList.get(index);
		Thread th = new Thread(new TCPSender(ipToSend,fileToSend.getNameFile(),false));
		th.start();
	}
	
	
	// Used to get next ip from node
	public String getNextIP()
	{
		return nextIP;
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

	// Sets the ip and node of the previous node.
	public void setPreviousNode(int previousNode, String previousIP)throws RemoteException, ClassNotFoundException
	{	
		this.previousNode = previousNode;
		this.previousIP = previousIP;
		System.out.println("set previous Node");
		System.out.println("previousNode: "+this.previousNode);
		System.out.println("previousIP: "+this.previousIP);
	}
	
	// Sets the totalFileList.
	public void setTotalFileList(ArrayList<FileInfo> totalFileList){
		this.totalFileList=totalFileList;
	}
	

	// Sets the ip and node of the next node.
	public void setNextNode(int nextNode, String nextIP)throws RemoteException, ClassNotFoundException
	{
		this.nextNode = nextNode;
		this.nextIP = nextIP;
		System.out.println("set next Node");
		System.out.println("nextNode: "+this.nextNode);
		System.out.println("nextIP: "+this.nextIP);
	}
	
	
	// Check if local files map on new next node
	// THIS ONE WAS USED IN A PREVIOUS BUILD AND ISN'T USED AT THE MOMENT
	/*
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
	*/
	
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