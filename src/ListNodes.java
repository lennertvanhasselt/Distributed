
import java.net.InetAddress;
import java.util.TreeMap;

public class ListNodes {
	
	static TreeMap<Integer,InetAddress> table;
	
	public ListNodes()
	{
		table = new TreeMap<Integer,InetAddress>();
	}
	
	public TreeMap<Integer, InetAddress> GetMap()
	{
		return table; //we could return the table or all values independently 
	}
	
	public static void AddToTable(int hashed, InetAddress IP)
	{
		table.put(hashed,IP);
		System.out.println(table.get(hashed));
		return;
	}
	
	public static boolean keyInTable(int hash)
	{
		return table.containsKey(hash);
	}
	
	public synchronized static boolean ipInTable(InetAddress IP)
	{
		return table.containsValue(IP);
	}
	
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


