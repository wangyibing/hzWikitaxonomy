package database;

import java.io.BufferedReader;
import java.util.HashMap;

import tools.uFunc;

public class InfoboxNameList {
	private static String EnNamePath = 
			"/home/hanzhe/Public/result_hz/enwiki/Infobox/InfoboxNameList_category.txt";
	private static String ZhNamePath = 
			"/home/hanzhe/Public/result_hz/zhwiki/Infobox/InfoboxNameList_category.txt";
	private static String SimEnNamePath = 
			"/home/hanzhe/Public/result_hz/simplewiki/Infobox/SimInfoboxNameList_category.txt";

	/** from zhwiki, enwiki, simplewiki
	 * already to lowercase and simple chinese
	 */
	private final static HashMap<String, Integer> InfoboxNameList =
			new HashMap<String, Integer>();
	private final static HashMap<String, Integer> prefix =
			new HashMap<String, Integer>();
	private static boolean infoboxListInited = false;
	


	public static boolean isInfoboxName(String name)
	{
		if(infoboxListInited == false)
			LoadInfoboxNameList();
		String n = name.replaceAll("\\s+", "_").toLowerCase();
		if(n.contains("box") && n.contains("metadata") == false)
			return true;
		if(n.equals("toccolours") || n.equals("wikitable"))
			return true;
		if(InfoboxNameList.containsKey(n))
			return true;
		return false;
	}
	
	/**
	 * already convert to lowercase and simple chinese
	 */
	private static void LoadInfoboxNameList() {
		if(infoboxListInited == true)
			return;
		LoadInfoboxNameList(EnNamePath);
		LoadInfoboxNameList(ZhNamePath);
		LoadInfoboxNameList(SimEnNamePath);
		infoboxListInited = true;
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

}
