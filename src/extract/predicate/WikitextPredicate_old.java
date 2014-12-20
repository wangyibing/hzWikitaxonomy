package extract.predicate;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Vector;

import com.tag.myPredicate;

import database.Entity;
import tools.uFunc;

public class WikitextPredicate_old {
	private static String i = "";
	private static String info = "";

	private static BufferedReader brP;
	private static int pageidP;
	private static Vector<myPredicate> PagePred =  
			new Vector<myPredicate>();
	private static myPredicate lastPred;
	private static int pageNrP = 0;
	private static int TripleNrP = 0;
	private static int AlignNrP = 0;
	
	private static BufferedReader brD;
	private static int pageidD;
	private static int pageNrD = 0;
	private static int TripleNrD = 0;
	private static int AlignNr = 0;
	// "predicate####object"
	private static Vector<String> PageTpls = 
			new Vector<String>();
	private static String lastLineD;
	private static String infoboxName;
	private static long time;
	private static long AlignTime = 0;

	public static void Extract(String PredIdFile, String DumpsTripleFile,
			String PrediAddFile)
	{
		uFunc.AlertPath = "data/info/WikitextPredicateInfo";
		uFunc.deleteFile(uFunc.AlertPath);
		time = System.currentTimeMillis();
		outputPath = PrediAddFile;
		uFunc.deleteFile(outputPath);
		outputDMissPath = DumpsTripleFile + ".miss";
		uFunc.deleteFile(outputDMissPath);
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
			lastPred.CompleteInfo_Triple(brP);
			//********* Get Fir predicate end ************
		} catch (IOException e) {
			e.printStackTrace();
		}
		brD = uFunc.getBufferedReader(DumpsTripleFile);
		try {
			lastLineD = brD.readLine();
			if(lastLineD.startsWith("InfoboxName:"))
			{
				infoboxName = lastLineD.substring(12);
				lastLineD = brD.readLine();
				while(lastLineD.startsWith("null"))
					lastLineD = lastLineD.substring(4);
				TripleNrD ++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		while(true)
		{
			//System.out.println(pageidP + "\t" + pageidD);
			if(lastLineD == null || lastPred == null)
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
				long t1 = System.currentTimeMillis();
				Align();
				long t2 = System.currentTimeMillis();
				AlignTime += (t2 - t1);
				GetNextPagePredInfo();
				GetNextPageDumpTplsInfo();
			}
		}
		while(lastPred != null)
		{
			GetNextPagePredInfo();
		}
		uFunc.addFile(output, outputPath);
		DumpsMissAlignment(null);
	}

	/**
	 * PagePred <=>  PageTpls
	 * one Predicate may correspond with multi dumpsTriples,
	 * mainly based on content of objects.
	 */
	private static void Align() {
		// TODO Auto-generated method stub
		//Scanner sc = new Scanner(System.in);
		//System.out.println("***********************press any key to continue*************************");
		//sc.nextLine();
		while( PagePred.size() > 0 && PageTpls.size() > 0)
		{
			String dTriple = PageTpls.get(PageTpls.size() -1);
			int MaxIndex = 0;
			double MaxSim = 0;
			String dPred = dTriple.split("####")[0];
			String dObjc = dTriple.split("####")[1];
			String tmpInfobox = dTriple.split("####")[2];
			if(tmpInfobox != null && tmpInfobox.equals("null"))
				tmpInfobox = null;
			if(dPred.contains("caption") || dObjc.endsWith(".png") ||
					dPred.contains("title") ||
					dObjc.endsWith(".jpg") || dObjc.endsWith("jpeg"))
			{
				PageTpls.remove(PageTpls.size() - 1);
				continue;
			}
			for(int j = 0 ; j < PagePred.size(); j ++)
			{
				double tSim = GetSim(PagePred.get(j), dPred, dObjc);
				//System.out.println(tSim + "\t" + PagePred.get(j).Content);
				if(tSim > MaxSim)
				{
					MaxSim = tSim;
					MaxIndex = j;
				}
			}
			if(MaxSim < 1)
			{
				PageTpls.remove(PageTpls.size() - 1);
				DumpsMissAlignment(pageidD + "\t" + dTriple);
				continue;
			}
			if(MaxSim < 3)
			{
				info = "align:" + MaxIndex + ";" + MaxSim + "\t" +  dTriple + "\n" + 
						PagePred.get(MaxIndex).toString();
				//uFunc.Alert(true, i, info);
			}
			
			
			if(PagePred.get(MaxIndex).WikitextContent == null || 
					PagePred.get(MaxIndex).WikitextContent.equals(""))
			{
				PagePred.get(MaxIndex).WikitextContent = dPred;
				AlignNrP ++;
			}
			else
				PagePred.get(MaxIndex).WikitextContent += "####" + dPred;
			
			if(tmpInfobox != null)
			{
				String infoboxes = PagePred.get(MaxIndex).InfoboxName; 
				if(infoboxes == null || infoboxes.equals(""))
					PagePred.get(MaxIndex).InfoboxName = tmpInfobox;
				else{
					boolean exist = false;
					for(String infobox : infoboxes.split("####"))
						if(infobox.equals(tmpInfobox))
						{
							exist = true;
							break;
						}
					if(exist == false)
						PagePred.get(MaxIndex).InfoboxName += "####" + tmpInfobox;
				}
			}
			AlignNr ++;
			PageTpls.remove(PageTpls.size() - 1);
		}
	}

	static String outputDMiss = "";
	static String outputDMissPath = "";
	static int outputDMissNr = 0;
	private static void DumpsMissAlignment(String string) {
		// TODO Auto-generated method stub
		if(string == null)
		{
			uFunc.addFile(outputDMiss, outputDMissPath);
			return;
		}
		outputDMiss += string + "\n";
		outputDMissNr ++;
		if(outputDMissNr % 1000 == 0)
		{
			uFunc.addFile(outputDMiss, outputDMissPath);
			outputDMiss = "";
		}
	}

	/**
	 * dPred and dObjc may contain "[[id]]"
	 * @param myPredicate
	 * @param dPred
	 * @param dObjc
	 * @return
	 */
	private static double GetSim(myPredicate predi, String dPred,
			String dObjc) {
		// TODO Auto-generated method stub
		double score = 0;
		int linkOD = -1;
		if(dObjc.matches("\\[\\[[0-9]{1,}\\]\\]"))
		{
			linkOD = Integer.parseInt(dObjc.substring(2, dObjc.length() - 2));
			dObjc = Entity.getTitles(linkOD);
			if(dObjc == null)
			{
				uFunc.Alert(true, i, "dObjc is null:" + pageidD + "\t" + dPred);
				return 0;
			}
		}
		dObjc = dObjc.toLowerCase();
		String whole = "";
		String each = "";
		for(String objP : predi.Objs)
		{
			String contOP = objP;
			int linkOP = 0;
			if(objP.contains("->") == true)
			{
				if(objP.contains("##"))
					objP = objP.substring(0, objP.indexOf("##"));
				if(objP.indexOf("->") < 0)
					uFunc.Alert(true, "WikitextPredicate", objP);
				contOP = objP.substring(0, objP.indexOf("->"));
				String linkString = objP.substring(
						objP.indexOf("->") + 3, objP.length() - 1);
				linkOP = Integer.parseInt(linkString);
				if(linkOP == linkOD)
					score += 5;
			}
			if(contOP.toLowerCase().equals(dObjc))
			{
				score += 3;
			}
			whole += contOP.toLowerCase();
			each += "####" + contOP;
		}
		whole += each;
		String tits = "";
		if(Entity.getTitles(linkOD) != null)
		{
			tits = Entity.getTitles(linkOD) + "####" + dObjc;
		}
		else tits = dObjc;
		double maxPerc = 0;
		tits = uFunc.RemovePunctuations(
				uFunc.Simplify(tits).replaceAll("#", "\0").toLowerCase().replaceAll("\\s|_", ""));
		whole = uFunc.RemovePunctuations(
				uFunc.Simplify(whole.replaceAll("#", "\0").toLowerCase().replaceAll("\\s|_", "")));
		
		tits = tits.replaceAll("\0", "#");
		whole = whole.replaceAll("\0", "#");
		for(String wholeEach : whole.split("####"))
		{
			if(wholeEach.startsWith("等") == false && wholeEach.contains("等"))
				wholeEach = wholeEach.substring(0, wholeEach.indexOf("等"));
			for(String title : tits.split("####"))
			{
				if(title.startsWith("等") == false && title.contains("等"))
					title = title.substring(0, title.indexOf("等"));
				int charSim = 0;
				for(int j = 0; j < title.length(); j ++)
					if(wholeEach.contains(title.charAt(j) + ""))
					{
						if(uFunc.hasChineseCharactor(title.charAt(j) + ""))
							charSim += 2;
						charSim ++;
					}
				if(charSim == 0)
					continue;
				int charDiff = uFunc.GetEditDist(title, wholeEach);
				double perc = 0;
				if(charDiff == 0)
					perc = charSim * title.length();
				else perc = (1.0 * charSim * title.length()/ (charDiff * wholeEach.length()));
				// if object in dumps is normal English word, should contain them all
				if(title.matches("[a-zA-Z]{1,}"))
				{
					if(wholeEach.contains(title) == false)
						perc = Math.min(0.1, perc);
				}
				// if one is website, the corresponding objc should also have website
				if(title.contains("http") || title.contains("www") ||
						wholeEach.contains("http") || wholeEach.contains("www"))
				{
					if(((title.contains("http") || title.contains("www")) &&
							(wholeEach.contains("http") || wholeEach.contains("www"))) == false)
						perc = 0;
					else perc *= 10;
				}
				if(uFunc.isNumeric(title) && wholeEach.contains(title))
				{
					int Index = wholeEach.indexOf(title) + title.length();
					if((Index >= wholeEach.length() || 
							uFunc.containNumber(wholeEach.charAt(Index) + "") == false)
						&& (Index - title.length() - 1 < 0 || 
								uFunc.containNumber(wholeEach.charAt(Index -title.length() - 1) + "") == false))
						perc *= 10;
					else perc /= 2;
				}
				else if(uFunc.isNumeric(title) && wholeEach.contains(title) == false)
					perc /= 5;
				if(linkOD > 0 && wholeEach.contains(title) == false)
					perc /= 3;
				// multiply the longest substring length
				perc *= uFunc.GetLongestCommonSubsequence(wholeEach, title);
				// contain !
				if(wholeEach.contains(title))
					perc *= 3;
				
				
				
				if(perc > maxPerc)
					maxPerc = perc;
				//System.out.println(perc + "\t" + charDiff + "\t" + title + "\t" + wholeEach);
				
			}
		}
		score += maxPerc;
		if(predi.WikitextContent != null &&
				predi.WikitextContent.equals("") == false)
			score -= 0.01;
		return score;
	}

	private static void GetNextPageDumpTplsInfo() {
		if(lastLineD == null)
		{
			info = "lastLineD is null:" + pageidD;
			uFunc.Alert(true, i, info);
			return;
		}
		PageTpls.clear();
		pageNrD ++;
		if(lastLineD.startsWith("InfoboxName:"))
		{
			infoboxName = lastLineD.substring(12);
			try {
				lastLineD = brD.readLine();
				while(lastLineD.startsWith("null"))
					lastLineD = lastLineD.substring(4);
				TripleNrD ++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String [] ss ;
		ss = lastLineD.split("\t");
		pageidD = Integer.parseInt(ss[0].substring(2, ss[0].length() - 2));
		PageTpls.add(ss[1] + "####" + ss[2] + "####" + infoboxName);
		String lastPred = ss[1];
		String lastObj = ss[2];
		try {
			while((lastLineD = brD.readLine()) != null)
			{
				while(lastLineD.startsWith("null"))
					lastLineD = lastLineD.substring(4);
				TripleNrD ++;
				if(lastLineD.startsWith("InfoboxName:"))
				{
					infoboxName = lastLineD.substring(12);
					continue;
				}
				ss = lastLineD.split("\t");
				if(ss.length < 3 || ss[1].equals("name") || ss[1].contains("image") ||
						ss[1].contains("caption") || ss[2].endsWith(".png") ||
						ss[2].endsWith(".jpg") || ss[2].endsWith(".jpeg") || 
						ss[2].matches("[0-9]{1,}px"))
				{
					continue;
				}
				
				int pageid = Integer.parseInt(ss[0].substring(2, ss[0].length() - 2));
				if(pageid != pageidD)
				{
					//System.out.println("&&&&" + lastPred + "\t" + lastObj);
					PageTpls.add(lastPred + "####" + lastObj + "####" + infoboxName);
					break;
				}
				if(lastPred.equals(ss[1]))
					lastObj += ", " + ss[2];
				else {
					//System.out.println("&&&&" + lastPred + "\t" + lastObj);
					PageTpls.add(lastPred + "####" + lastObj + "####" + infoboxName);
					lastPred = ss[1];
					lastObj = ss[2];
				}
			}
			if(lastLineD == null)
				uFunc.Alert(true, i, "dumpFile reached ending");
		} catch (Exception e) {
			System.out.println("ERROR:" + lastLineD);
			e.printStackTrace();
		}
		
	}

	/**
	 * remember to save past PagePred info!
	 * @param prediAddFile 
	 */
	private static void GetNextPagePredInfo() {
		if(lastPred == null)
		{
			info = "lastPredP is null:" + pageidP;
			//uFunc.Alert(true, i, info);
			return;
		}
		SaveLastPagePred();
		PagePred.clear();
		pageNrP ++;
		if(pageNrP % 5000 == 0)
		{
			DecimalFormat df = new DecimalFormat(".00");
			info = pageNrP + " pageNrP;\t" + pageNrD + " pageNrD\tAlignNr:" + AlignNr +
					"\t cost:" + (System.currentTimeMillis() - time)/1000 + "sec\n" +
					"\ttriple in P aligned:" + df.format((1.0*AlignNrP/TripleNrP)) +
					" (" + AlignNrP + "/" +TripleNrP + ")" + "\n" +
					"\ttriple in D aligned:" + df.format((1.0*AlignNr/TripleNrD)) +
					" (" + AlignNr + "/" +TripleNrD + ")" + "\n" + 
					"AlignTime:" + AlignTime;
			time = System.currentTimeMillis();
			uFunc.Alert(true, i, info);
		}
		PagePred.add(lastPred);
		pageidP = lastPred.Pageid;
		String oneLine = ""; 
		myPredicate next = null;
		try {
			while((oneLine = brP.readLine()) != null)
			{
				TripleNrP ++;
				long lastPredId = Long.parseLong(oneLine);
				int lastPageId = Integer.parseInt(
						oneLine.substring(0, oneLine.length() - 3));
				next = new myPredicate(lastPredId, lastPageId);
				next.CompleteInfo_Triple(brP);
				if(next.Pageid != lastPred.Pageid)
				{
					lastPred = next;
					break;
				}
				PagePred.add(next);
				//System.out.println(next);
				lastPred = next;
			}
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
		lastPred = next;
	}


	private static String outputPath;
	private static String output = "";
	private static int outNr = 0;
	private static void SaveLastPagePred() {
		for(int j = 0; j < PagePred.size(); j ++)
		{
			myPredicate pred = PagePred.get(j);
			if(pred.InfoboxName == null && infoboxName != null)
				pred.InfoboxName = infoboxName;
			output += pred.toString();
			outNr  ++;
			if(outNr % 1000 == 0)
			{
				uFunc.addFile(output, outputPath);
				output = "";
			}
		}
		PagePred.clear();
	}
}
