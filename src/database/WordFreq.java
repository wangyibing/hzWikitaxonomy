package database;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import tools.uFunc;

/**
 * wordFreq in text
 * @author hanzhe
 *
 */
public class WordFreq {

	public static String path = "data/word2vec/wordFreq";
	
	public static void main(String []args)
	{
		GetFile("/home/hanzhe/Public/result_hz/QA/splitedTxtMerge");
	}
	
	public static int Get(String word)
	{
		if(initedWF  == false)
			initWF();
		if(wf.containsKey(word))
			return wf.get(word);
		return 0;
	}

	private static boolean initedWF = false;
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

	static HashMap<String, Integer> wf = new HashMap<String, Integer>();
	private static void GetFile(String src) {
		// TODO Auto-generated method stub
		if(initedWF) return ;
		BufferedReader br = uFunc.getBufferedReader(src);
		String oneLine = "";
		try {
			int pageNr = 0;
			while((oneLine = br.readLine()) != null)
			{
				if(oneLine.startsWith("#Page")){
					pageNr ++;
					if(pageNr % 1000 == 0) 
						System.out.println(pageNr + " passed!");
				}
				if(oneLine.startsWith("#"))
					continue;
				for(String word : oneLine.split(" "))
				{
					if(word.contains("/"))
						word = word.substring(0, word.indexOf("/"));
					int freq = 1;
					if(wf.containsKey(word))
						freq += wf.remove(word);
					wf.put(word, freq);
				}
			}
			System.out.println("wodfreq inited! size:" + wf.size());
			uFunc.SaveHashMap(wf, path);
			initedWF = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
