package extract.predicatetable;

import com.tag.myElement;

import tools.uFunc;
import triple.extract.TripleGenerator;

public class PredIdGenerator {

	static int PageId = 0;
	static int PagePredNr = 0;
	static int MaxPagePredNr = 0;
	
	public static void generator(int pageid,
			String outputpath, myElement upperTitle, String triple)
	{
		if(pageid != PageId)
		{
			if(PagePredNr > MaxPagePredNr)
				MaxPagePredNr = PagePredNr;
			PagePredNr = 1;
			PageId = pageid;
		}
		else
		{
			PagePredNr ++;
		}
		String prediId = PagePredNr + "";
		int zeroNr = 3 - prediId.length();
		for(int i = 0; i < zeroNr; i ++)
			prediId = "0" + prediId;
		prediId = PageId + prediId;
		output += prediId +  "\n";
		String upperInfo = TripleGenerator.
				getStringFromMyelement(upperTitle, true);
		output += "UpperTitle:" + upperInfo + "\n";
		output += triple;
		output += "\n";
		outNr ++;
		if(outNr % 1000 == 0)
		{
			uFunc.addFile(output, outputpath);
			output = "";
			if(outNr % 500000 == 0)
			{
				System.out.println(outNr + " predicate find!");
			}
		}
	}
	
	public static void close(String outputpath){
		uFunc.addFile(output, outputpath);
		if(PagePredNr > MaxPagePredNr)
			MaxPagePredNr = PagePredNr; 
		System.out.println("max page's predicate nr is:" + MaxPagePredNr);
	}
	static String output = "";
	static int outNr = 0;
}

