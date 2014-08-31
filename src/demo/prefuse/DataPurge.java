package demo.prefuse;

import java.io.BufferedReader;
import java.io.IOException;

import database.Zhwiki;
import de.tudarmstadt.ukp.wikipedia.api.Category;
import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiTitleParsingException;
import dumps.Entity;
import dumps.category.SubCategory;
import extract.Extract;
import tools.uFunc;

public class DataPurge {

	public static String SelectedTriplesPath =
			"/home/hanzhe/github/hzWikitaxonomy/data/SelcTripls";
	
	public static void main(String [] args)
	{
		Purging(Extract.TriplePath);
	}

	private static void Purging(String triplePath) {
		// TODO Auto-generated method stub
		BufferedReader br = uFunc.getBufferedReader(triplePath);
		uFunc.deleteFile(SelectedTriplesPath);
		String oneLine = "";
		String output = "";
		int outNr = 0;
		int PageLines = 0;
		int lastId = 0;
		boolean lastInfo = false;
		try {
			while((oneLine = br.readLine()) != null)
			{
				String [] ss = oneLine.split("\t");
				if(ss[0].startsWith("null"))
					continue;
				int id = Integer.parseInt(ss[0]);
				if(id != lastId)
				{
					if(PageLines > 10)
					{
						if(("" + id).startsWith("10"))
							System.out.println(id + "\t" + Entity.getEntityTitle(id));
					}
					PageLines = 0;
				}
				else
				{
					if(PageLines > 10)
						continue;
					PageLines ++;
				}
				String title = Entity.getEntityTitle(id);
				
				if(title == null || title.equals(""))
					continue;
				//System.out.println(oneLine +"\n" + outNr);
				String tmp = title + ss[1] + ss[2];
				if(isInfo(oneLine, lastId, lastInfo))
				{
					//System.out.println(oneLine +"\n" + outNr);
					output += oneLine + "\n";
					outNr ++;
					if(outNr % 1000 == 0)
					{
						uFunc.addFile(output, SelectedTriplesPath);
						//System.out.println(oneLine +"\n" + outNr);
						output = "";
						if(outNr >= 10000)
							break;
					}
					lastInfo = true;
				}
				else{
					lastInfo = false;
				}
				lastId = id;
			}
			uFunc.addFile(output, SelectedTriplesPath);
			System.out.println("total triple Nr :" + outNr);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static boolean isInfo(String oneLine, int lastId, boolean lastInfo) {
		// TODO Auto-generated method stub
		String [] ss = oneLine.split("\t");
		int pageid = 0;
		if(ss[0].startsWith("null") == false)
		{
			pageid = Integer.parseInt(ss[0]);
			String title = Entity.getEntityTitle(pageid);
			if(title.contains("浙江") || ss[2].contains("浙江")||
					title.contains("杭州") || ss[2].contains("杭州")||
					title.contains("萧山") || ss[2].contains("萧山")||
					title.contains("浙江") || ss[2].contains("浙江"))
			{
				return true;
			}
			
		}
		return false;
	}
}
