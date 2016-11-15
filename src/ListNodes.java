
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.util.TreeMap;

// This includes all functions that are needed for the correct working of ClientInfo
public class ListNodes {
	
	private TreeMap<Integer,InetAddress> table;

	//Creating a new TreeMap with the hashvalue and IP.
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
	
	// Returning the values of the TreeMap.
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
		
		 //we could return the table or all values independently 
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

	//Returns true if the given IP is already located in the TreeMap.
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
	public synchronized InetAddress getFileIP(int hash) throws ClassNotFoundException
	{
		try{
			FileInputStream fileIn = new FileInputStream("/temp/table.ser");
	        ObjectInputStream in = new ObjectInputStream(fileIn);
	        table = (TreeMap) in.readObject();
	        in.close();
	        fileIn.close();
	        
	        int key;
			if(hash <= table.lastKey())
				key = table.higherKey(hash-1);//-1 cause the file could be same hash then node
			else
				key = table.firstKey();
			System.out.println(hash+" found at "+key);
			InetAddress IP = table.get(key);
			return IP;
	        
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
	
	public int getNumberOfNodes() throws IOException, ClassNotFoundException
	{
		FileInputStream fileIn = new FileInputStream("/temp/table.ser");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        table = (TreeMap) in.readObject();
        in.close();
        fileIn.close();
        
        return table.size();
	} 
	
	public TreeMap<Integer, InetAddress> getPreviousNext(int node) throws IOException, ClassNotFoundException{
		
		FileInputStream fileIn = new FileInputStream("/temp/table.ser");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        table = (TreeMap) in.readObject();
        in.close();
        fileIn.close();
		
		TreeMap<Integer,InetAddress> prevNext =new TreeMap<Integer,InetAddress>();
		int previousNode;
		int nextNode;
		if(table.size()==1)
		{
			previousNode=node;
			nextNode=node;
		} else if(table.higherKey(node)==null) {
			previousNode=table.firstKey();
			nextNode=table.lowerKey(node);
		} else if(table.lowerKey(node)==null) {
			previousNode=table.higherKey(node);
			nextNode=table.lastKey();
		} else {
			previousNode = table.higherKey(node);
			nextNode = table.lowerKey(node);
		}
			
			
		
		prevNext.put(previousNode,table.get(previousNode));
		prevNext.put(nextNode,table.get(nextNode));
		return prevNext;		
	}
}


