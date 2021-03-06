
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.util.TreeMap;

// Mostly used to keep and update the list of nodes.
public class ListNodes {
	
	private TreeMap<Integer,InetAddress> table;

	//Creating a new TreeMap which includes the hashed name and the IP.
	public ListNodes() throws ClassNotFoundException, IOException
	{
		table = new TreeMap<Integer,InetAddress>();
		try {
			//Check if there already is a file
			FileInputStream fileIn = new FileInputStream("/temp/table.ser");
	        ObjectInputStream in = new ObjectInputStream(fileIn);
	        table = (TreeMap) in.readObject();
	        in.close();
	        fileIn.close();
		} catch(FileNotFoundException i) {
			//if file is not found, make a new file.
	        try{
	        	FileOutputStream fileOut = new FileOutputStream("/temp/table.ser");
	        	ObjectOutputStream out = new ObjectOutputStream(fileOut);
	        	out.writeObject(table);
	        	out.close();
	        	fileOut.close();
	        }catch(IOException j){
	        	j.printStackTrace();
	        }
		}
	}
	
	// Returning the TreeMap of Nodes.
	public TreeMap<Integer, InetAddress> getMap() throws ClassNotFoundException
	{
		try{
			FileInputStream fileIn = new FileInputStream("/temp/table.ser");
	        ObjectInputStream in = new ObjectInputStream(fileIn);
	        table = (TreeMap) in.readObject();
	        in.close();
	        fileIn.close();
	        
	        return table;
	        
		} catch(IOException i) {
	          i.printStackTrace();
	          return null;
	    }
	}
	
	// Adding another entry to the TreeMap.
	public void addToTable(int hashed, InetAddress IP) throws ClassNotFoundException
	{
		try{
			FileInputStream fileIn = new FileInputStream("/temp/table.ser");
	        ObjectInputStream in = new ObjectInputStream(fileIn);
	        table = (TreeMap) in.readObject();
	        in.close();
	        fileIn.close();
	        
	        table.put(hashed,IP);
			System.out.println("added: "+hashed+" "+table.get(hashed));
			
			FileOutputStream fileOut = new FileOutputStream("/temp/table.ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(table);
			out.close();
			fileOut.close();
			return;
		} catch(IOException i) {
	          i.printStackTrace();
	          return;
	    }
	}
	
	// Returns true if the given hash is already located in the TreeMap.
	public boolean keyInTable(int hash) throws ClassNotFoundException
	{
		try{
			FileInputStream fileIn = new FileInputStream("/temp/table.ser");
	        ObjectInputStream in = new ObjectInputStream(fileIn);
	        table = (TreeMap) in.readObject();
	        in.close();
	        fileIn.close();
	        
	        return table.containsKey(hash);
	        
		} catch(IOException i) {
	          i.printStackTrace();
	          return false;
	    }
		
	}

	// Returns true if the given IP is already located in the TreeMap.
	public synchronized boolean ipInTable(InetAddress IP) throws ClassNotFoundException
	{
		try{
			FileInputStream fileIn = new FileInputStream("/temp/table.ser");
	        ObjectInputStream in = new ObjectInputStream(fileIn);
	        table = (TreeMap) in.readObject();
	        in.close();
	        fileIn.close();
	        
	        return table.containsValue(IP);
	        
		} catch(IOException i) {
	          i.printStackTrace();
	          return false;
	    }
		
		
	}
	
	// Gives the key where the hash is located and returns the IP of this node.	
	public synchronized TreeMap<Integer,InetAddress> getFileIP(int hash) throws ClassNotFoundException
	{
		try{
			int key;
			FileInputStream fileIn = new FileInputStream("/temp/table.ser");
	        ObjectInputStream in = new ObjectInputStream(fileIn);
	        table = (TreeMap) in.readObject();
	        in.close();
	        fileIn.close();
	        TreeMap<Integer,InetAddress> temp = new TreeMap<Integer, InetAddress>();
	        
			if(hash <= table.lastKey())
				key = table.higherKey(hash-1);//-1 cause the file could be same hash then node
			else
				key = table.firstKey();
			System.out.println(hash+" found at "+key);
			InetAddress IP = table.get(key);
			temp.put(key, IP);
			return temp;
	        
		} catch(IOException i) {
	          i.printStackTrace();
	          return null;
	    }
	}
	
	// A node will be deleted from the TreeMap and returns true when it was removed successful and the other way around.
	public synchronized boolean deleteNode(int hash) throws ClassNotFoundException
	{
		try{
			FileInputStream fileIn = new FileInputStream("/temp/table.ser");
	        ObjectInputStream in = new ObjectInputStream(fileIn);
	        table = (TreeMap) in.readObject();
	        in.close();
	        fileIn.close();
	        
	        if (table.containsKey(hash))
			{
				table.remove(hash);
				FileOutputStream fileOut = new FileOutputStream("/temp/table.ser");
				ObjectOutputStream out = new ObjectOutputStream(fileOut);
				out.writeObject(table);
				out.close();
				fileOut.close();
				return true;
			}
	        else
	        	return false;
	        
		} catch(IOException i) {
	          i.printStackTrace();
	          return false;
	    }		
	}
	
	// Returns how many nodes are currently working in the system
	public int getNumberOfNodes() throws IOException, ClassNotFoundException
	{
		FileInputStream fileIn = new FileInputStream("/temp/table.ser");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        table = (TreeMap) in.readObject();
        in.close();
        fileIn.close();
        
        return table.size();
	} 
	
	// Used to return a TreeMap which includes the name and IP of the previous and the next entry of the list.
	public TreeMap<Integer, InetAddress> getPreviousNext(int node) throws IOException, ClassNotFoundException
	{
		FileInputStream fileIn = new FileInputStream("/temp/table.ser");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        table = (TreeMap) in.readObject();
        in.close();
        fileIn.close();
		
		TreeMap<Integer,InetAddress> prevNext =new TreeMap<Integer,InetAddress>();
		int previousNode;
		int nextNode;
		//if node is alone, he is previous and next
		if(table.size()==1){
			previousNode=node;
			nextNode=node;
		//node is last in table
		} else if(table.higherKey(node)==null) {
			previousNode=table.firstKey();
			nextNode=table.lowerKey(node);
		//node is first in table
		} else if(table.lowerKey(node)==null) {
			previousNode=table.higherKey(node);
			nextNode=table.lastKey();
		//node has normal next and previous
		} else {
			previousNode = table.higherKey(node);
			nextNode = table.lowerKey(node);
		}
		prevNext.put(previousNode,table.get(previousNode));
		prevNext.put(nextNode,table.get(nextNode));
		return prevNext;		
	}
}


