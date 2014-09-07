package database;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import tools.uFunc;

public class Category {
	private static String info;
	private static String i = "Category";
	
	public static String CategoryTitleFile = 
			"data/pageinfo/CateidCateTitlePair";

	public static String GetSubCates(int cateId)
	{
		
	}
	
	
	private static HashMap<Integer, String> CateId2Title = 
			new HashMap<Integer, String>();
	private static boolean id2titInited = false;
	
	public static String GetCateName(int cateId)
	{
		if(id2titInited == false)
			InitId2Tit();
		if(CateId2Title.containsKey(cateId))
			return CateId2Title.get(cateId);
		return null;
	}
	private static void InitId2Tit() {
		// TODO Auto-generated method stub
		if(id2titInited == true)
			return;
		BufferedReader br = uFunc.getBufferedReader(CategoryTitleFile);
		String oneLine = "";
		try {
			while((oneLine = br.readLine()) != null)
			{
				String [] ss = oneLine.split("\t");
				int cateid = Integer.parseInt(ss[0]);
				
				if(CateId2Title.containsKey(cateid))
				{
					info = "cateid repeated:" + cateid + "\t" + ss[1] 
							+ ";" + CateId2Title.get(cateid);
					uFunc.Alert(true, i , info);
				}
				CateId2Title.put(cateid, ss[1]);
			}
			System.out.println("category id2title map inited, size:" + 
					CateId2Title.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		id2titInited = true;
	}
}
