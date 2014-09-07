package extract.pageinfo;

import java.io.BufferedReader;
import java.io.IOException;

import tools.QsortPair;
import tools.uFunc;

public class CateidPageidPair {
	public static String CateidPageidPair = 
			"/home/hanzhe/Public/result_hz/wiki_count2/pageinfo/CateidPageidPair";
	public static String CateidCateTitlePair = 
			"/home/hanzhe/Public/result_hz/wiki_count2/pageinfo/CateidCateTitlePair";
	public static String categorySrc = 
			"/home/hanzhe/Public/result_hz/wiki_count2/SourceFiles/Category.txt";
	public static String CatePageSrc = 
			"/home/hanzhe/Public/result_hz/wiki_count2/SourceFiles/category_pages.txt";
	//private static String info;
	private static String i = "CateidPageidPair";

	public static void main(String [] args)
	{
		uFunc.AlertOutput = "data/info/CategoryInfo";
		GetCateidCateTitlePair(categorySrc, CateidCateTitlePair);
		QsortPair.SortPair(CatePageSrc, 
				CateidPageidPair, 
				true, true, 3300000);
	}

	private static void GetCateidCateTitlePair(String srcPath,
			String cateidCateTitlePair2) {
		// TODO Auto-generated method stub
		String oneLine = "";
		int outNr = 0;
		String output = "";
		uFunc.deleteFile(cateidCateTitlePair2);
		BufferedReader br = uFunc.getBufferedReader(srcPath);
		try {
			while ((oneLine = br.readLine()) != null)
			{
				String [] ss = oneLine.split("\t");
				if(ss.length < 3)
				{
					uFunc.Alert(true, i, "split error:" + oneLine);
				}
				else{
					output += ss[0] + "\t" + ss[2] + "\n";
					outNr ++;
					if(outNr % 1000 == 0)
					{
						uFunc.addFile(output, cateidCateTitlePair2);
						output = "";
					}
				}
			}
			System.out.println("outNr:" + outNr);
			uFunc.addFile(output, cateidCateTitlePair2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
