package tools;

import java.io.BufferedReader;
import java.io.IOException;

import database.Zhwiki;

public class DataSampling {

	public static void main(String [] args)
	{
		String dumpsFile = 
				"/home/hanzhe/Public/result_hz/zhwiki/Infobox/Triple/Triple.txt";
		String tarPath = 
				"/home/hanzhe/Public/result_hz/wiki_count2/predicate/dumpsTriples";
		int lineNr = 10000;
		DumpsTripleSampling(dumpsFile, tarPath, lineNr);
	}

	private static void DumpsTripleSampling(String srcFile, 
			String tarPath, int lineNr) {
		// TODO Auto-generated method stub
		BufferedReader br = uFunc.getBufferedReader(srcFile);
		uFunc.deleteFile(tarPath);
		String oneLine = "";
		int lNr = 0;
		String output = "";
		Zhwiki.init();
		try {
			while((oneLine = br.readLine()) != null)
			{
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
					if(Zhwiki.getTitle(objId) == null)
					{
						System.out.println("objId error:" + oneLine);
						continue;
					}
					ss[2] = "[[" + Zhwiki.getTitle(objId) + "]]";
				}
					
				String info = ss[0] + "\t" + ss[1] + "\t" + ss[2] + "\n";
				output += info;
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
