
import java.net.InetAddress;
import java.util.TreeMap;

// This includes all functions that are needed for the correct working of ClientInfo
public class ListNodes {
	
	static TreeMap<Integer,InetAddress> table;
	
	// Creating a new TreeMap with the hashvalue and IP.
	public ListNodes()
	{
		table = new TreeMap<Integer,InetAddress>();
	}
	
	// Returning the values of the TreeMap.
	public TreeMap<Integer, InetAddress> GetMap()
	{
		return table; //we could return the table or all values independently 
	}
	
	// Adding another entry to the TreeMap.
	public static void AddToTable(int hashed, InetAddress IP)
	{
		table.put(hashed,IP);
		System.out.println(table.get(hashed));
		return;
	}
	
	// Returns true if the given hash is already located in the TreeMap.
	public static boolean keyInTable(int hash)
	{
		return table.containsKey(hash);
	}
	
	// Returns true if the given IP is already located in the TreeMap.
	public synchronized static boolean ipInTable(InetAddress IP)
	{
		return table.containsValue(IP);
	}
	
	// Gives the key where the hash is located and retursn the IP of this node.
	public synchronized static InetAddress getFileIP(int hash)
	{
		int key;
		if(hash < table.lastKey())
			key = table.higherKey(hash);
		else
			key = table.firstKey();
		System.out.println(hash+" found at "+key);
		InetAddress IP = table.get(key);
		return IP;
	}
	
	// A node will be deleted from the TreeMap and returns true when it was removed successful and the other way around.
	public synchronized static boolean deleteNode(int hash)
	{
		if (table.containsKey(hash))
			{
				table.remove(hash);
				return true;
			}
		else
			return false;
	}
	
}


