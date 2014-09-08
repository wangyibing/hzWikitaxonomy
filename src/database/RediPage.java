package database;
import java.io.BufferedReader;
import java.util.HashMap;

import tools.uFunc;


public class RediPage {

	public static String CanonicalPath = 
			"data/pageinfo/RediPage";

	public static int getTargetPageid(int redirectPageid)
	{
		if(RedirectPageInited == false)
			LoadRedirectPageMap();
		if(RedirectPage.containsKey(redirectPageid) == true)
			return RedirectPage.get(redirectPageid);
		return 0;
	}

	private static HashMap<Integer, Integer> RedirectPage =
			new HashMap<Integer, Integer>();
	private static boolean RedirectPageInited = false;
	private static void LoadRedirectPageMap() {
		String oneLine = "";
		try{
			BufferedReader reader = uFunc.getBufferedReader(CanonicalPath);
			
			while((oneLine = reader.readLine())!= null){
				String [] ts = oneLine.split("\t");
				if(ts.length <2)
					continue;
				int rPageId = Integer.parseInt(ts[0]);
				ts[1] = ts[1].replaceAll("\\s", "");
				int dPageId = Integer.parseInt(ts[1]);
				RedirectPage.put(rPageId, dPageId);
			}
			System.out.println("RedirectPage inited! size:" + RedirectPage.size());
		}catch(Exception e){
			e.printStackTrace();
		}
		RedirectPageInited = true;
	}
}
