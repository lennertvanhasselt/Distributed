
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.util.TreeMap;

public class ListNodes {
	
	static TreeMap<Integer,InetAddress> table;
	
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
	
	public static void AddToTable(int hashed, InetAddress IP) throws ClassNotFoundException
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
	
	public static boolean keyInTable(int hash) throws ClassNotFoundException
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
	
	public synchronized static boolean ipInTable(InetAddress IP) throws ClassNotFoundException
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
	
	public synchronized static InetAddress getFileIP(int hash) throws ClassNotFoundException
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
	
	public synchronized static boolean deleteNode(int hash) throws ClassNotFoundException
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


