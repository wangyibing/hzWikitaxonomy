package database;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import tools.uFunc;

public class InfoboxIdList {
	public static String infoboxIdListPath = 
			"/home/hanzhe/Public/result_hz/zhwiki/Infobox/Triple/tripleIdList.txt";
	
	public static HashMap<Integer, Integer> InfoboxIdList =
			new HashMap<Integer, Integer>();
	private static boolean infoboxListInited = false;
	
	public static void LoadInfoboxNameList() {
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
