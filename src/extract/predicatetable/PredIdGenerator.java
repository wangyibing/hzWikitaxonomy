package extract.predicatetable;

import tools.uFunc;

public class PredIdGenerator {

	static int PageId = 0;
	static int PagePredNr = 0;
	static int MaxPagePredNr = 0;
	
	public static void generator(String predicate, int pageid,
			String outputpath)
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
		for(int i = 0; i < 3 - prediId.length(); i ++)
			prediId = "0" + prediId;
		prediId = PageId + prediId;
		output += prediId + "\t" + predicate + "\n";
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
		System.out.println("max page's predicate nr is:" + MaxPagePredNr);
	}
	static String output = "";
	static int outNr = 0;
}

