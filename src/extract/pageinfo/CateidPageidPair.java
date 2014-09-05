package extract.pageinfo;

import java.io.BufferedReader;
import java.io.IOException;

import tools.uFunc;
import database.Zhwiki;
import de.tudarmstadt.ukp.wikipedia.api.Category;
import de.tudarmstadt.ukp.wikipedia.api.Page;

public class CateidPageidPair {
	public static String CateidPageidPair = 
			"data/CateidPageidPair";
	public static String CateidCateTitlePair = 
			"data/CateidCateTitlePair";
	public static String categorySrc = 
			"/home/hanzhe/Public/result_hz/zhwiki/Category/Category.txt";
	private static String info;
	private static String i = "CateidPageidPair";

	public static void main(String [] args)
	{
		uFunc.AlertOutput = "data/info/CategoryInfo";
		GetCateidCateTitlePair(categorySrc, CateidCateTitlePair);
		//GetCateidPageidPair(CateidPageidPair);
	}

	private static void GetCateidCateTitlePair(String srcPath,
			String cateidCateTitlePair2) {
		// TODO Auto-generated method stub
		String oneLine = "";
		int outNr = 0;
		String output = "";
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
			uFunc.addFile(output, cateidCateTitlePair2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * aborted, because "category_pages.txt" from dumps exist!
	 * @param path2
	 */
	private static void GetCateidPageidPair(String path2) {
		// TODO Auto-generated method stub
		Zhwiki.init();
		String output = "";
		int outNr = 0;
		int pageid = 0;
		long time = System.currentTimeMillis();
		for(Page page: Zhwiki.wiki.getArticles())
		{
			pageid = page.getPageId();
			for(Category cate : page.getCategories())
			{
				output += cate.getPageId() + "\t" + pageid + "\n";
				outNr ++;
				if(outNr % 10000 == 0)
				{
					uFunc.addFile(output, path2);
					info = outNr + " pages passed, cost:" + 
							(System.currentTimeMillis() - time)/1000 + "sec";
					time = System.currentTimeMillis();
					uFunc.Alert(true, i , info);
					output = "";
				}
			}
		}
		uFunc.addFile(output, path2);
		uFunc.AlertClose();
	}
}
