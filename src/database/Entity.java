package database;
import java.io.BufferedReader;
import java.util.HashMap;

import tools.uFunc;

import com.spreada.utils.chinese.ZHConverter;




public class Entity {
	public static String CanonicalPath_title = 
			"data/EntityTitle";
	public static String CanonicalPath_titles = 
			"data/EntityTitles";

	// <13, 数学;数学科学;数学系;>
	private static HashMap<Integer, String> EntityIdTitMap = 
			new HashMap<Integer, String>();
	private static boolean IdTitInited = false;
	// <数学, 13>
	private static HashMap<String, Integer> EntityTitsIdMap = 
			new HashMap<String, Integer>();
	private static boolean TitIdInited = false;
	// <数学, 013>
	private static HashMap<String, Integer> titles = 
			new HashMap<String, Integer>();
	private static boolean titlesInited = false;

	private static String EntityIdTitPath = 
			"/home/hanzhe/Public/result_hz/wiki_count/EntityId/EntityId_unif.txt";
	private static String EntityIdTitsPath = 
			"/home/hanzhe/Public/result_hz/wiki_count/EntityId/PageIdTits.txt";
	
	
	private static ZHConverter simConvt = 
			ZHConverter.getInstance(ZHConverter.SIMPLIFIED);
	

	public static int getEntityId(String title)
	{
		initEntityTitsIdMap();
		title = title.replaceAll("_| ", "");
		if(EntityTitsIdMap.containsKey(simConvt.convert(title)) == false)
		{
			title = title.replaceAll("《|》", "");
			if(EntityTitsIdMap.containsKey(simConvt.convert(title)) == true)
				return EntityTitsIdMap.get(simConvt.convert(title));
			return 0;
			
		}
		return EntityTitsIdMap.get(simConvt.convert(title));
	}
	
	public static String getEntityTitle(int pageid)
	{
		if(pageid == 0)
			return null;
		initEntityIdTitMap();
		if(EntityIdTitMap.containsKey(pageid) == false){
			if(RediPage.getTargetPageid(pageid) <= 0)
			{
				System.out.println("Entity.java:page title not exist:" + pageid);
			}
			pageid = RediPage.getTargetPageid(pageid);
			if(EntityIdTitMap.containsKey(pageid) == false)
			{
				System.out.println("Entity.java:page title not exist:" + pageid);
				return null;
			}
			else
			{
				return EntityIdTitMap.get(pageid);
			}
		}
		else
			return EntityIdTitMap.get(pageid);
	}
 
	private static void initEntityIdTitMap() {
		if(IdTitInited == true)
			return;
		String oneLine = "";
		try{
			BufferedReader reader = uFunc.getBufferedReader(EntityIdTitPath);
			while((oneLine = reader.readLine())!= null){
				String [] ts = oneLine.split("\t");
				if(ts.length <2)
					continue;
				int Eid = Integer.parseInt(ts[0]);
				// 抽取的title都不含空字符，已经简体了
				String title = uFunc.Simplify(ts[1]);
				EntityIdTitMap.put(Eid, title);
			}
			System.out.println("EntityIdTitMap.size:"+ 
			EntityIdTitMap.size());
		}catch(Exception e){
			e.printStackTrace();
		}
		IdTitInited = true;
	}

	private static void initEntityTitsIdMap() {
		if(TitIdInited == true)
			return;
		String oneLine = "";
		try{
			BufferedReader reader = uFunc.getBufferedReader(EntityIdTitsPath);
			while((oneLine = reader.readLine())!= null){
				String [] ts = oneLine.split("\t");
				if(ts.length <2)
					continue;
				int Eid = Integer.parseInt(ts[0]);
				// 抽取的title都不含空字符，已经简体了
				String title = uFunc.Simplify(ts[1]).replaceAll("_| ", "").toLowerCase();
				if(EntityTitsIdMap.containsKey(title))
				{
					int id = EntityTitsIdMap.get(title);
					if(id != Eid && RediPage.getTargetPageid(Eid) <= 0
							&& RediPage.getTargetPageid(id) <= 0)
					{
						//System.out.println("Entity.java:" + title + ":" + id + ";" + Eid);
					}
				}
				EntityTitsIdMap.put(title, Eid);
			}
			System.out.println("EntityTitsIdMap.size:"+ 
					EntityTitsIdMap.size());
		}catch(Exception e){
			e.printStackTrace();
		}
		TitIdInited = true;
	}

	
	
}
