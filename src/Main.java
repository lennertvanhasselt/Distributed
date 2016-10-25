import java.util.HashMap;
import java.util.Scanner;
import java.lang.*;
import java.net.InetAddress;

//GROUP 8 FTW zwam

public class Main {

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		String filename;
		
		System.out.println("Name of the node: ");
		filename = sc.nextLine();
		
		int length = filename.length();
		
		char[] fileChars = filename.toCharArray();
		
		int sum=0;
		for(int i=0;i<length;i++)
			sum=sum*97+(i+1)*Character.getNumericValue(fileChars[i]);
		
		sum = Math.abs(sum)%32768;
		
		System.out.println(sum);
		
		HashMap<Integer,InetAddress> Table = new HashMap<Integer,InetAddress>();
		
		
		
		sc.close();
	}

}
