package tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;

public class Compare2File {

	public static void main(String [] args)
	{
		String folder = "/home/hanzhe/Public/result_hz/wiki_count2/predicate/";
		Compare(folder + "predicateId2", folder + "predicateId.Normed");
	}
	public static void Compare(String file1, String file2)
	{
		BufferedReader br1 = uFunc.getBufferedReader(file1);
		BufferedReader br2 = uFunc.getBufferedReader(file2);
		String line1 = "";
		String line2 = "";
		Scanner sc = new Scanner(System.in);
		while(true)
		{
			try {
				line1 = br1.readLine();
				line2 = br2.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
			if(line1 == null || line2 == null)
				break;
			if(line1.equals(line2) == false)
			{
				System.out.println(line1 + "\n" + line2 + "\n");
				sc.nextLine();
			}
		}
	}
}
