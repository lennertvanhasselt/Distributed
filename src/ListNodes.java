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
}
