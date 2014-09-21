package tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

public class StopWords {

	public static boolean isSW(String string)
	{
		if(inited == false)
			initSW();
		if(sw.containsKey(string))
			return true;
		return false;
	}
	private static void initSW() {
		// TODO Auto-generated method stub
		if(inited)
			return;
		BufferedReader br = uFunc.getBufferedReader(path);
		String oneLine = "";
		try {
			while((oneLine = br.readLine()) != null)
			{
				sw.put(oneLine, 0);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("stopwords loaded, size:" + sw.size());
		inited = true;
	}
	static HashMap<String, Integer> sw = 
			new HashMap<String, Integer>();
	static boolean inited = false;
	static String path = "data/StopWords.txt";
}
