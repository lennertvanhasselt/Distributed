
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
	public TreeMap<Integer, InetAddress> GetMap() throws ClassNotFoundException
	
	
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
	public void AddToTable(int hashed, InetAddress IP) throws ClassNotFoundException
	{
		try{
			FileInputStream fileIn = new FileInputStream("/temp/table.ser");
	        ObjectInputStream in = new ObjectInputStream(fileIn);
	        table = (TreeMap) in.readObject();
	        in.close();
	        fileIn.close();
	        
	        table.put(hashed,IP);
			System.out.println(table.get(hashed));
			
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
	// Gives the key where the hash is located and retursn the IP of this node.	
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
				key = table.higherKey(hash-1);
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
				return true;
			}
	        else
	        	return false;
	        
		} catch(IOException i) {
	          i.printStackTrace();
	          return false;
	    }
		
		
	}
	
}


