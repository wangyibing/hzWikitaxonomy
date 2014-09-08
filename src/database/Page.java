package database;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import tools.uFunc;

public class Page {


	public static String getTitles(int pageid)
	{
		initId2Tits();
		if(Id2TitsMap.containsKey(pageid))
			return Id2TitsMap.get(pageid);
		return null;
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
