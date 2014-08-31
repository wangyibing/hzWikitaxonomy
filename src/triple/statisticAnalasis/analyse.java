package triple.statisticAnalasis;

import java.io.BufferedReader;
import java.io.IOException;

import extract.Extract;
import tools.uFunc;

public class analyse {

	public static void main(String [] args)
	{
		//CalculateWikitextTriple();
		getFirstLines(10000);
	}

	private static void getFirstLines(int maxNr) {
		// TODO Auto-generated method stub
		BufferedReader br = 
				uFunc.getBufferedReader(Extract.TriplePath);
		String oneLine = "";
		int LineNr = 0;
		String output = "";
		try {
			while((oneLine = br.readLine()) != null &&
					LineNr < maxNr)
			{
				if(oneLine.contains("337464") || 
						(oneLine.toLowerCase().contains("NGC") && oneLine.toLowerCase().contains("1868")))
				{
					System.out.println(oneLine);
				}
				else
				{
					continue;
				}
				LineNr ++;
				output += oneLine + "\n";
				if(LineNr % 1000 == 0)
				{
					uFunc.addFile(output, Extract.TriplePath + maxNr);
					output = "";
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		uFunc.addFile(output, Extract.TriplePath + maxNr);
	}

	public static void CalculateWikitextTriple() {
		// TODO Auto-generated method stub
		BufferedReader br = uFunc.getBufferedReader(uFunc.WikitextTriplePath);
		String oneLine = "";
		int cateLineNr = 0;
		int entityNr = 0;
		String lastId = "";
		try {
			while( (oneLine = br.readLine()) != null)
			{
				String [] ss = oneLine.split("\t");
				if(ss.length > 2 && ss[1].equals("category"))
					cateLineNr ++;
				if(ss.length > 2 && lastId.equals(ss[0]) == false)
					entityNr ++;
				lastId = ss[0];
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("category line nr:" + cateLineNr);
		System.out.println("entity nr(contain category info):" + entityNr);
	}
}
