package database;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import tools.uFunc;

public class Predicate {

	public static String path = "data/predicate/predicateId";
	public static int GetId(String word)
	{
		if(initedWF  == false)
			initWF();
		if(wf.containsKey(word))
			return wf.get(word);
		return 0;
	}

	private static boolean initedWF = false;
	static HashMap<String, Integer> wf = new HashMap<String, Integer>();
	private static void initWF() {
		// TODO Auto-generated method stub
		if(initedWF) return ;
		wf.clear();
		BufferedReader br = uFunc.getBufferedReader(path);
		String oneLine = "";
		try {
			while((oneLine = br.readLine()) != null)
			{
				String [] ss = oneLine.split("\t");
				int freq = Integer.parseInt(ss[1]);
				wf.put(ss[0], freq);
			}
			initedWF = true;
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("wordfreq size:" + wf.size());
	}

}
