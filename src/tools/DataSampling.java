package tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import database.DisPage;
import database.Entity;
import database.RediPage;
import database.Zhwiki;

public class DataSampling {

	public static void main(String [] args)
	{
		String dumpsFile = 
				"/home/hanzhe/Public/result_hz/zhwiki/Infobox/Triple/Triple.txt";
		String tarPath = 
				"/home/hanzhe/Public/result_hz/wiki_count2/predicate/dumpsTriples";
		int lineNr = 100000;
		DumpsTripleSampling("/home/hanzhe/Public/result_hz/wiki_count2/predicate/predicateId2",
				"/home/hanzhe/Public/result_hz/wiki_count2/predicate/predicateId2.sample10000", lineNr);
	}

	private static void DumpsTripleSampling2(String srcFile, String tarPath,
			int lineNr) {
		// TODO Auto-generated method stub
		BufferedReader br = uFunc.getBufferedReader(srcFile);
		String oneLine = "";
		String output = "";
		int linenr = 0;
		HashMap<String, Integer> predId = new HashMap<String, Integer>();
		try {
			boolean flag = false;
			int extra = 0;
			int pagid = 0;
			while((oneLine = br.readLine()) != null)
			{
				if(oneLine.contains("3686542"))
				{
					flag = true;
				}
				if(flag)
				{
					extra ++;
					if(oneLine.split("\t").length < 2)
						continue;
					String st = oneLine.split("\t")[0];
					st = st.substring(2, st.length() - 2);
					int pageid = Integer.parseInt(st);
					if(pageid != pagid)
					{
						System.out.println(pageid);
						pagid = pageid;
					}
				}
				if(oneLine.equals(""))
				{
					linenr ++;
					oneLine = br.readLine();
					try{
						long pred = Long.parseLong(oneLine);	
						if(pred > 0)
						{
							int freq = 1;
							if(predId.containsKey(oneLine))
							{
								freq += predId.remove(oneLine);
							}
							predId.put(oneLine, freq);
							if(freq > 2)
								break;
						}
						if(predId.size() % 1000000 == 0)
							System.out.println("size:" + predId.size());
						if(linenr % 1000000 == 0)
							System.out.println("linenr:" + linenr);
					}catch(Exception e){
						System.out.println(oneLine);
					}
				}
			}System.out.println(extra);
			System.out.println("linenr:" + linenr);
			uFunc.SaveHashMap(predId, "data/info/predidfreq");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void DumpsTripleSampling(String srcFile, 
			String tarPath, int lineNr) {
		// TODO Auto-generated method stub
		BufferedReader br = uFunc.getBufferedReader(srcFile);
		uFunc.deleteFile(tarPath);
		String oneLine = "";
		int lNr = 0;
		String output = "";
		try {
			while((oneLine = br.readLine()) != null)
			{
				while(oneLine.startsWith("null"))
					oneLine = oneLine.substring(4);
				/*
				String [] ss = oneLine.split("\t");
				if(ss.length < 3)
					continue;
				if(ss[1].equals("category"))
					continue;
				if(ss[0].matches("\\[\\[[0-9]{1,}\\]\\]") == false)
				{
					System.out.println("error:" + oneLine);
					continue;
				}
				ss[0] = ss[0].substring(2, ss[0].length() - 2);
				if(ss[2].matches("\\[\\[[0-9]{1,}\\]\\]"))
				{
					int objId = Integer.parseInt(ss[2].substring(2, 
							ss[2].length() - 2));
					if(Entity.getTitle(objId) == null && 
							RediPage.getTargetPageid(objId) <= 0 &&
							DisPage.GetTitle(objId) == null)
					{
						//System.out.println("objId error:" + oneLine);
						continue;
					}
					String title = Entity.getTitle(objId);
					if(title == null)
						title = Entity.getTitle(RediPage.getTargetPageid(objId));
					if(title == null)
						title = DisPage.GetTitle(objId);
					if(title == null)
						continue;
					ss[2] = "[[" + title + "]]";
				}
				else if(ss[2].matches("\\[.+\\]"))
					ss[2] = ss[2].substring(1, ss[2].length() -1);
				String info = ss[0] + "\t" + ss[1] + "\t" + ss[2] + "\n";
				output += info;
				*/
				output += oneLine + "\n";
				lNr ++;
				if(lNr >= lineNr)
					break;
				if(lNr % 1000 == 0)
				{
					uFunc.addFile(output, tarPath);
					output = "";
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		uFunc.addFile(output, tarPath);
	}
}
