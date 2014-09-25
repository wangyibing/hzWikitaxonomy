package database;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import tools.uFunc;

public class Page {
	public static String getCategories(int pageid)
	{
		initId2Cate();
		if(Id2Cates.containsKey(pageid))
			return Id2Cates.get(pageid);
		return null;
	}
	public static String getTitles(int pageid)
	{
		initId2Tits();
		if(Id2TitsMap.containsKey(pageid))
			return Id2TitsMap.get(pageid);
		return null;
	}

	public static String CateidPageidPair = 
			"/home/hanzhe/Public/result_hz/wiki_count2/pageinfo/CateidPageidPair";
	private static HashMap<Integer, String> Id2Cates = 
			new HashMap<Integer, String>();
	private static boolean Id2CatesInited = false;
	private static void initId2Cate() {
		// TODO Auto-generated method stub
		if(Id2CatesInited)
			return;
		BufferedReader br = uFunc.getBufferedReader(
				CateidPageidPair);
		String oneLine = "";
		int pageid = 0;
		String cates = "";
		try {
			while((oneLine = br.readLine()) != null)
			{
				String [] ss = oneLine.split("\t");
				int id = Integer.parseInt(ss[1]);
				if(id != pageid)
				{
					if(cates.equals("") == false)
					{
						Id2Cates.put(pageid, cates);
					}
					pageid = id;
					cates = "";
				}
				else{
					cates += ss[0] + ";";
				}
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(cates.equals("") == false)
		{
			Id2Cates.put(pageid, cates);
		}
		Id2CatesInited = true;
	}
	private static boolean Id2TItsInited = false;
	private static HashMap<Integer, String> Id2TitsMap = 
			new HashMap<Integer, String>();
	private static void initId2Tits() {
		// TODO Auto-generated method stub
		if(Id2TItsInited == true)
			return;
		BufferedReader br = uFunc.getBufferedReader(Entity.CanonicalPath_titles);
		String oneLine = "";
		try {
			while((oneLine = br.readLine()) != null)
			{
				String [] ss = oneLine.split("\t");
				int pageid = Integer.parseInt(ss[0]);
				String tits = "";
				if(Id2TitsMap.containsKey(pageid))
				{
					tits = Id2TitsMap.remove(pageid) + "####" + ss[1];
				}
				else tits = ss[1];
				Id2TitsMap.put(pageid, tits);
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Id2TItsInited = true;
	}
}
