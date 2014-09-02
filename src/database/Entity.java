package database;
import java.io.BufferedReader;
import java.util.HashMap;

import tools.uFunc;

import com.spreada.utils.chinese.ZHConverter;




public class Entity {
	public static String CanonicalPath_titles = 
			"data/EntityTitles";

	// <13, 数学;数学科学;数学系;>
	private static HashMap<Integer, String> Id2TitMap = 
			new HashMap<Integer, String>();
	private static boolean IdTitInited = false;
	// <数学, 13>
	private static HashMap<String, Integer> TitsIdMap = 
			new HashMap<String, Integer>();
	private static boolean TitIdInited = false;

	
	private static ZHConverter simConvt = 
			ZHConverter.getInstance(ZHConverter.SIMPLIFIED);
	

	public static int getEntityId(String title)
	{
		initTitsIdMap();
		title = title.replaceAll("_| ", "");
		if(TitsIdMap.containsKey(simConvt.convert(title)) == false)
		{
			title = title.replaceAll("《|》", "");
			if(TitsIdMap.containsKey(simConvt.convert(title)) == true)
				return TitsIdMap.get(simConvt.convert(title));
			return 0;
			
		}
		return TitsIdMap.get(simConvt.convert(title));
	}
	
	public static String getEntityTitle(int pageid)
	{
		if(pageid == 0)
			return null;
		initId2TitMap();
		if(Id2TitMap.containsKey(pageid) == false){
			if(RediPage.getTargetPageid(pageid) <= 0)
			{
				System.out.println("Entity.java:page title not exist:" + pageid);
			}
			pageid = RediPage.getTargetPageid(pageid);
			if(Id2TitMap.containsKey(pageid) == false)
			{
				System.out.println("Entity.java:page title not exist:" + pageid);
				return null;
			}
			else
			{
				return Id2TitMap.get(pageid);
			}
		}
		else
			return Id2TitMap.get(pageid);
	}
 
	private static void initId2TitMap() {
		if(IdTitInited == true)
			return;
		String oneLine = "";
		try{
			BufferedReader reader = uFunc.getBufferedReader(
					CanonicalPath_titles);
			while((oneLine = reader.readLine())!= null){
				String [] ts = oneLine.split("\t");
				if(ts.length < 3)
					continue;
				if(ts[2].equals("title"))
				{
					int Eid = Integer.parseInt(ts[0]);
					// 抽取的title都不含空字符，已经简体了
					String title = uFunc.Simplify(ts[1]);
					Id2TitMap.put(Eid, title);
				}
			}
			System.out.println("Id2TitMap.size:"+ 
					Id2TitMap.size());
		}catch(Exception e){
			e.printStackTrace();
		}
		IdTitInited = true;
	}

	
	private static void initTitsIdMap() {
		if(TitIdInited == true)
			return;
		String oneLine = "";
		try{
			BufferedReader reader = uFunc.getBufferedReader(CanonicalPath_titles);
			while((oneLine = reader.readLine())!= null){
				String [] ts = oneLine.split("\t");
				if(ts.length < 3)
					continue;
				if(ts[2].equals("title"))
				{
					int Eid = Integer.parseInt(ts[0]);
					// 抽取的title都不含空字符，已经简体了
					String title = uFunc.Simplify(ts[1]).replaceAll("_| ", "").toLowerCase();
					if(TitsIdMap.containsKey(title))
					{
						int id = TitsIdMap.get(title);
						if(id != Eid && RediPage.getTargetPageid(Eid) <= 0
								&& RediPage.getTargetPageid(id) <= 0)
						{
							//System.out.println("Entity.java:" + title + ":" + id + ";" + Eid);
						}
					}
					TitsIdMap.put(title, Eid);
				}
			}
			System.out.println("TitsIdMap.size:"+ 
					TitsIdMap.size());
		}catch(Exception e){
			e.printStackTrace();
		}
		TitIdInited = true;
	}
}
