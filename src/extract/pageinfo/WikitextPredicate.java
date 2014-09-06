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
		String oneLine = "";
		try {
			//*********Get Fir predicate begin************
			oneLine = brP.readLine();
			long lastPId = Long.parseLong(oneLine);
			pageidP = Integer.parseInt(
					oneLine.substring(0, oneLine.length() - 3));
			lastPred = new myPredicate(lastPId, pageidP);
			lastPred.CompleteInfo(brP);
			//********* Get Fir predicate end ************
		} catch (IOException e) {
			e.printStackTrace();
		}
		brD = uFunc.getBufferedReader(DumpsTripleFile);
		try {
			lastLineD = brD.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		while(true)
		{
			if(lastLineD == null && lastPred == null)
				break;
			if(pageidP < pageidD)
			{
				GetNextPagePredInfo();
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

	private static void Align() {
		// TODO Auto-generated method stub
		
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
		SaveLastPagePred();
		if(lastPred == null)
		{
			info = "lastPred is null" + pageidP;
			uFunc.Alert(true, i, info);
			return;
		}
		PagePred.clear();
		PagePred.add(lastPred);
		String oneLine = ""; 
		myPredicate next = null;
		try {
			while((oneLine = brP.readLine()) != null)
			{
				long lastPredId = Long.parseLong(oneLine);
				int lastPageId = Integer.parseInt(
						oneLine.substring(0, oneLine.length() - 3));
				next = new myPredicate(lastPredId, lastPageId);
				next.CompleteInfo(brP);
				if(next.Pageid != lastPred.Pageid)
					break;
			}
			if(next != null)
				lastPred = next;
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
	}


	private static String outputPath;
	private static String output;
	private static int outNr = 0;
	private static void SaveLastPagePred() {
		for(int j = 0; j < PagePred.size(); j ++)
		{
			myPredicate pred = PagePred.get(j);
			output += pred.toString();
			outNr  ++;
			if(outNr % 200 == 0)
			{
				uFunc.addFile(output, outputPath);
				output = "";
			}
		}
		PagePred.clear();
	}
}
