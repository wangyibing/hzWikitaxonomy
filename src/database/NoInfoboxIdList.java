package database;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import tools.uFunc;

public class NoInfoboxIdList {
	public static boolean isNot(int pageid)
	{
		if(inited == false)
			Init();
		return NO.containsKey(pageid);
	}

	private static void Init() {
		// TODO Auto-generated method stub
		if(inited == true)
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
					System.out.println("NoInfoboxIdList init error:"
							+ oneLine );
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("NoInfoboxIdList size:" + NO.size());
		inited = true;
	}

	private static HashMap<Integer, Boolean> NO = 
			new HashMap<Integer, Boolean>();
	private static String NoInfoboxIdListPath = 
			"data/pageinfo/NoInfoboxIdList";
	private static boolean inited = false;
}
