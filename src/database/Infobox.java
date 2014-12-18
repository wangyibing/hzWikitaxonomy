package database;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import tools.uFunc;



public class Infobox {
	private static String infoboxIdListPath = 
			"/home/hanzhe/Public/result_hz/zhwiki/Infobox/Triple/tripleIdList.txt";
	private static String EnNamePath = 
			"/home/hanzhe/Public/result_hz/enwiki/Infobox/InfoboxNameList_category.txt";
	private static String ZhNamePath = 
			"/home/hanzhe/Public/result_hz/zhwiki/Infobox/InfoboxNameList_category.txt";
	private static String SimEnNamePath = 
			"/home/hanzhe/Public/result_hz/simplewiki/Infobox/SimInfoboxNameList_category.txt";

	
	public static boolean containsInfobox(int pageid)
	{
		if(infoboxListInited == false)
			LoadInfoboxNameList();
		return InfoboxIdList.containsKey(pageid);
	}

	public static boolean isInfoboxName(String name)
	{
		if(nameListInited == false)
			LoadNameList();
		String n = name.replaceAll("\\s+", "_").toLowerCase();
		if(n.contains("collaps"))
			return false;
		if(n.contains("box") && n.contains("metadata") == false)
			return true;
		if(n.equals("toccolours") || n.equals("wikitable"))
			return true;
		if(InfoboxNameList.containsKey(n))
			return true;
		return false;
	}

	public static boolean isNotInfobox(int pageid)
	{
		if(initedNotInfobox == false)
			InitNotInfobox();
		return NO.containsKey(pageid);
	}

	private static void InitNotInfobox() {
		// TODO Auto-generated method stub
		if(initedNotInfobox == true)
			return;
		String oneLine = "";
		BufferedReader br = 
				uFunc.getBufferedReader(NoInfoboxIdListPath);
		try {
			while((oneLine = br.readLine()) != null)
			{
				try{
					Integer id = Integer.parseInt(oneLine);
					NO.put(id, true);
				}catch(Exception e1){
					System.out.println("NoInfoboxIdList init error:" + oneLine );
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("NoInfoboxIdList size:" + NO.size());
		initedNotInfobox = true;
	}

	private static HashMap<Integer, Boolean> NO = 
			new HashMap<Integer, Boolean>();
	private static String NoInfoboxIdListPath = 
			"data/pageinfo/NoInfoboxIdList";
	private static boolean initedNotInfobox = false;


	/** from zhwiki, enwiki, simplewiki
	 * already to lowercase and simple chinese
	 */
	private final static HashMap<String, Integer> InfoboxNameList =
			new HashMap<String, Integer>();
	private final static HashMap<String, Integer> prefix =
			new HashMap<String, Integer>();
	private static boolean nameListInited = false;
	
	/**
	 * already convert to lowercase and simple chinese
	 */
	private static void LoadNameList() {
		if(nameListInited == true)
			return;
		LoadInfoboxNameList(EnNamePath);
		LoadInfoboxNameList(ZhNamePath);
		LoadInfoboxNameList(SimEnNamePath);
		nameListInited = true;
	}
	private static void LoadInfoboxNameList(
			String path) {
		BufferedReader br = uFunc.getBufferedReader(path);
		String oneLine = "";
		try {
			while((oneLine = br.readLine()) != null){
				if(oneLine.toLowerCase().startsWith("template:")){
					String name = uFunc.Simplify(
							oneLine.substring(9).replaceAll("\\s+", "_").toLowerCase());
					if(name.contains("/")){
						name = name.substring(0, name.indexOf("/"));
					}
					//if(name.contains("box") == false)
					//	System.out.println("InfoboxNameList.java:" + name);
					for(String pre : name.split("_| "))
					{
						if(pre.contains("box") == false)
							continue;
						int freq = 1;
						if(prefix.containsKey(pre)){
							freq += prefix.remove(pre);
						}
						prefix.put(pre, freq);
					}
							
					InfoboxNameList.put(name, 0);
				}
			}
			System.out.println("InfoboxNameList size:" + 
					InfoboxNameList.size());
			uFunc.SaveHashMap(prefix, uFunc.InfoFolder + "/infoboxNamePrefix");
			infoboxListInited = true;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	
	private static HashMap<Integer, Integer> InfoboxIdList =
			new HashMap<Integer, Integer>();
	private static boolean infoboxListInited = false;
	
	private static void LoadInfoboxNameList() {
		if(infoboxListInited == true)
			return;
		BufferedReader br = uFunc.getBufferedReader(infoboxIdListPath);
		String oneLine = "";
		try {
			while((oneLine = br.readLine())!=null){
				InfoboxIdList.put(Integer.parseInt(oneLine), 0);
			}
			System.out.println("infoboxIdList size:" + 
					InfoboxIdList.size());
			infoboxListInited = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
