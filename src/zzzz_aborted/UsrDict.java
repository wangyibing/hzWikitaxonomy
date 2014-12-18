package zzzz_aborted;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Vector;

import tools.StopWords;
import tools.uFunc;

public class UsrDict {
	public static void main(String [] args)
	{
		EntityTits2Usrdict("data/pageinfo/EntityTitles",
				"/home/hanzhe/Public/result_hz/wiki_count2/pageinfo/UsrDict");
	}

	/**
	 * 723	松山區	Disa
	 * 723	松山区	redititle
	 * @param src
	 * @param tar
	 */
	public static void EntityTits2Usrdict(String src, String tar)
	{
		uFunc.deleteFile(tar);
		String oneLine = "";
		int lineNr = 0;
		String output = "";
		BufferedReader br = uFunc.getBufferedReader(src);
		HashMap<String, Integer> titles = 
				new HashMap<String, Integer>();
		try {
			String lastId = "";
			String type = "";
			while((oneLine = br.readLine()) != null)
			{
				oneLine = uFunc.Simplify(oneLine);
				String [] ss = oneLine.split("\t");
				if(lastId.equals(ss[0]) == false)
				{
					if(ss[2].equals("title"))
						type = "title";
					else if(ss[2].equals("Disa"))
						type = "disa";
					else if(ss[2].equals("redi"))
						type = "redi";
					else type = "";
					//System.out.println("!:" + lastId + "\t" + ss[0] + "\t" + type);
					lastId = ss[0];
				}
				Vector<String> names = new Vector<String>();
				if(ss[1].contains(":") && uFunc.hasChineseCharactor(ss[1]))
				{
					for(String n : ss[1].split("\\:"))
					{
						names.add(n);
					}
				}
				
				names.add(ss[1]);
				for(String name : names)
				{
					name = name.replaceAll("_", " ");
					name = uFunc.UnifiedSentenceZh2En(name);
					if(name.matches(".+\\(.+\\)$"))
					{
						//System.out.println(name);
						name = name.substring(0, name.lastIndexOf("("));
					}
					name = uFunc.ReplaceBoundSpace(name);
					if(titles.containsKey(name) == false)
						titles.put(name, 0);
				}
				
				lineNr ++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("lineNr:" + lineNr);
		System.out.println("size:" + titles.size());
		
		Iterator<Entry<String, Integer>> it = titles.entrySet().iterator();
		int outNr = 0;
		while(it.hasNext())
		{
			Entry<String, Integer> next = it.next();
			String name = next.getKey();
			if(name.equals("") || StopWords.isSW(name))
				continue;
			System.out.println(name);
			output += name + "\n";
			outNr ++;
			if(outNr % 1000 == 0)
			{
				uFunc.addFile(output, tar);
				output = "";
			}
		}
		uFunc.addFile(output, tar);
	}
}
