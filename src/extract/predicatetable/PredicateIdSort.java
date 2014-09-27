package extract.predicatetable;

import java.io.BufferedReader;
import java.io.IOException;

import tools.QsortPair;
import tools.uFunc;

public class PredicateIdSort {
	public static void main(String [] args)
	{
		Sort("/home/hanzhe/Public/result_hz/wiki_count2/predicate/predicateId",
				"/home/hanzhe/Public/result_hz/wiki_count2/predicate/predicateId.sorted");
	}

	public static void Sort(String pIdFile, String tarFile)
	{
		uFunc.deleteFile(tarFile);
		uFunc.deleteFile(tarFile + ".tmp");
		uFunc.deleteFile(tarFile + ".tmp2");
		BufferedReader br = uFunc.getBufferedReader(pIdFile);
		String oneLine = "";
		String output = "";
		int RcdNr = 0;
		try {
			while((oneLine = br.readLine()) != null)
			{
				if(oneLine.equals(""))
					break;
				
				long pId = Long.parseLong(oneLine);
				
				String info = "";
				while((oneLine = br.readLine()) != null)
				{
					if(oneLine.equals(""))
						break;
					while(oneLine.endsWith("##"))
						oneLine = oneLine.substring(0, oneLine.length() - 2);
					info += oneLine.replaceAll("\t", "\\$\\$\\$") + "####";
				}
				output += pId + "\t" + info + "\n";
				RcdNr ++;
				if(RcdNr % 1000 == 0)
				{
					uFunc.addFile(output, tarFile + ".tmp");
					output = "";
				}
			}
			uFunc.addFile(output, tarFile + ".tmp");
			System.out.println("PreId format finided");
			
			QsortPair.SortPair(tarFile + ".tmp", tarFile + ".tmp2", false,
					true, RcdNr + 100);
			System.out.println("Qsort format finided");
			
			br = uFunc.getBufferedReader(tarFile + ".tmp2");
			output = "";
			RcdNr = 0;
			while((oneLine = br.readLine()) != null)
			{
				String [] ss = oneLine.split("\t");
				long pId = Long.parseLong(ss[0]);
				output += pId + "\n";
				for(String triple : ss[1].split("####"))
				{
					// col may have single or 3 eles
					// "UpperTitle:****" or "pageid\tpredicate\tobject"
					String [] col = triple.split("\\$\\$\\$");
					output += col[0];
					for(int i = 1; i < col.length; i ++)
						output += "\t" + col[i];
					output += "\n";
				}
				output += "\n";
				RcdNr ++;
				if(RcdNr % 1000 == 0)
				{
					uFunc.addFile(output, tarFile);
					output = "";
				}
			}
			uFunc.addFile(output, tarFile);
			uFunc.deleteFile(tarFile + ".tmp");
			uFunc.deleteFile(tarFile + ".tmp2");
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
