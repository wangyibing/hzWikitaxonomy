package extract.pageinfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import tools.uFunc;

public class WikitextPredicate {
	private static String i = "";
	private static String info = "";

	private static BufferedReader brP;
	private static int pageidP;
	private static Vector<myPredicate> PagePred =  
			new Vector<myPredicate>();
	private static myPredicate lastPred;
	
	private static BufferedReader brD;
	private static int pageidD;
	// "predicate####object"
	private static Vector<String> PageTpls = 
			new Vector<String>();
	private static String lastLineD;

	public static void Extract(String PredIdFile, String DumpsTripleFile,
			String PrediAddFile)
	{
		uFunc.AlertPath = PrediAddFile.substring(0, 
				PrediAddFile.lastIndexOf("/") + 1);
		outputPath = PrediAddFile;
		outNr = 0;
		brP = uFunc.getBufferedReader(PredIdFile);
		//*********Get Fir predicate begin************
		String oneLine = "";
		oneLine = brP.readLine();
		long lastPId = Long.parseLong(oneLine);
		pageidP = Integer.parseInt(
				oneLine.substring(0, oneLine.length() - 3));
		myPredicate pred = new myPredicate(lastPId, pageidP);
		//********* Get Fir predicate end ************
		brD = uFunc.getBufferedReader(DumpsTripleFile);
		lastLineD = brD.readLine();
		
		
		
		while(true)
		{
			if(pageidP < pageidD)
			{
				GetNextPagePredInfo(PrediAddFile);
			}
			else if(pageidP > pageidD)
			{
				GetNextPageDumpTplsInfo();
			}
			// pageidP == pageidD
			else{
				Align();
				GetNextPagePredInfo();
				GetNextPageDumpTplsInfo();
			}
		}
		uFunc.addFile(output, outputPath);
	}

	private static void GetNextPageDumpTplsInfo() {
		if(lastLineD == null)
		{
			info = "lastLineD is null:" + pageidD;
			uFunc.Alert(true, i, info);
			return;
		}
		PageTpls.clear();
		String [] ss ;
		ss = lastLineD.split("\t");
		pageidD = Integer.parseInt(ss[0]);
		PageTpls.add(ss[1] + "####" + ss[2]);
		
		try {
			while((lastLineD = brD.readLine()) != null)
			{
				ss = lastLineD.split("\t");
				int pageid = Integer.parseInt(ss[0]);
				if(pageid != pageidD)
					break;
				PageTpls.add(ss[1] + "####" + ss[2]);
			}
			if(lastLineD == null)
				uFunc.Alert(true, i, "dumpFile reached ending");
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * remember to save past PagePred info!
	 * @param prediAddFile 
	 */
	private static void GetNextPagePredInfo() {
		// TODO Auto-generated method stub
		SaveLastPagePred();
		if(lastLineP == null)
		{
			info = "lastLineP is null" + pageidP;
			uFunc.Alert(true, i, info);
			return;
		}
		PagePred.clear();
		pred.CompleteInfo(brP);
	}


	private static String outputPath;
	private static String output;
	private static int outNr = 0;
	private static void SaveLastPagePred() {
		// TODO Auto-generated method stub
		for(int j = 0; j < PagePred.size(); j ++)
		{
			myPredicate pred = PagePred.get(j);
			output += PredInfo2String(pred);
			outNr  ++;
			if(outNr % 200 == 0)
			{
				uFunc.addFile(output, outputPath);
				output = "";
			}
		}
		PagePred.clear();
	}

	private static String PredInfo2String(myPredicate pred) {
		// TODO Auto-generated method stub
		info = pred.Predid + "\n" + 
				"Contnt:" + pred.Content + "\n" +
				"Link:" + pred.Link + "\n" + 
				"UpperTitle:" + pred.UpperTitle + "\n";
		info += "Objcs:";
		for(String tr : pred.Objs)
			info += "\t" + tr;
		info += "InfoboxName:" + pred.InfoboxName + "\n";
		info += "WikitextCont:" + pred.WikitextContent + "\n";
		info += "\n";
		return info;
	}
}
