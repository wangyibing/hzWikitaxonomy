package extract.pageinfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Scanner;

import database.RediPage;
import database.Zhwiki;
import tools.uFunc;

public class RedirectRepair {
	public static String Folder = 
			"/home/hanzhe/Public/result_hz/wiki_count/pageinfo/";
	public static void main(String [] args)
	{
		Repair(Folder + "RediPageExtractionInfo2", 
				Folder + RediPage.CanonicalPath);
	}

	/**
	 * RediPageExtraction:135915 not standd redirect!!
	 * RediPageExtraction:209574 not standd redirect2!!match 0 times
	 * RediPageExtraction:318599:redirect extract missed4!!
	 * @param infoPath
	 * @param RediFilePath
	 */
	public static void Repair(String infoPath, String RediFilePath)
	{
		RepairFromInfo(infoPath, RediFilePath);
		
	}

	public static void RepairFromInfo(String infoPath, 
			String rediFilePath) {
		// TODO Auto-generated method stub
		BufferedReader br = uFunc.getBufferedReader(infoPath);
		String oneLine = "";
		Scanner sc = new Scanner(System.in);
		Zhwiki zhwiki = new Zhwiki();
		String output = "";
		try {
			int fixNr = 0;
			while((oneLine = br.readLine()) != null)
			{
				int pageid = 0;
				int tarId = 0;
				String title = "";
				String tarTitle = "";
				if(oneLine.startsWith("RediPageExtraction:"))
				{
					int index = 19;
					while(index < oneLine.length() && 
							oneLine.charAt(index) >= '0' &&
							oneLine.charAt(index) <= '9')
						index ++;
					pageid = Integer.parseInt(oneLine.substring(19, index));
					System.out.println("pageid:" + pageid);
					tarId = sc.nextInt();
					if(pageid > 0 && tarId > 0 )
					{
						fixNr ++;
						title = zhwiki.getTitle(pageid);
						tarTitle = zhwiki.getTitle(tarId);
						output += pageid +"\t"+ tarId + "\t" + title + "\t"
								+ tarTitle + "\n";
					}
					else{
						System.out.println("error:" + oneLine);
					}
				}
				else{
					System.out.println("error:" + oneLine);
				}
			}
			uFunc.addFile(output, rediFilePath);
			System.out.println("total fixed Nr of redirect Page:" + fixNr);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
