package extract.predicatetable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;

import database.Entity;
import database.Page;
import database.Zhwiki;
import extract.pageinfo.myPredicate;
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
		uFunc.deleteFile(PrediAddFile);
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
			System.out.println(pageidP + "\t" + pageidD);
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

	/**
	 * PagePred <=>  PageTpls
	 * one Predicate may correspond with multi dumpsTriples,
	 * mainly based on content of objects.
	 */
	private static void Align() {
		// TODO Auto-generated method stub
		Scanner sc = new Scanner(System.in);
		System.out.println("***********************press any key to continue*************************");
		sc.nextLine();
		while( PagePred.size() > 0 && PageTpls.size() > 0)
		{
			String dTriple = PageTpls.get(PageTpls.size() -1);
			System.out.println("dumpsTriple:" + dTriple);
			int MaxIndex = 0;
			double MaxSim = 0;
			String dPred = dTriple.split("####")[0];
			String dObjc = dTriple.split("####")[1];
			String infoboxName = dTriple.split("####")[2];
			if(infoboxName != null && infoboxName.equals("null"))
				infoboxName = null;
			if(dPred.contains("caption") || dObjc.endsWith(".png") ||
					dObjc.endsWith(".jpg") || dObjc.endsWith("jpeg"))
			{
				PageTpls.remove(PageTpls.size() - 1);
				continue;
			}
			for(int j = 0 ; j < PagePred.size(); j ++)
			{
				double tSim = GetSim(PagePred.get(j), dPred, dObjc);
				System.out.println(tSim + "\t" + PagePred.get(j).Content);
				if(tSim > MaxSim)
				{
					MaxSim = tSim;
					MaxIndex = j;
				}
			}
			if(MaxSim < 0.1)
			{
				PageTpls.remove(PageTpls.size() - 1);
				continue;
			}
			info = "align:" + MaxIndex + ";" + MaxSim + "\t" +  dTriple + "\n" + 
					PagePred.get(MaxIndex).toString();
			uFunc.Alert(true, i, info);
			
			if(PagePred.get(MaxIndex).WikitextContent == null || 
					PagePred.get(MaxIndex).WikitextContent.equals(""))
				PagePred.get(MaxIndex).WikitextContent = dPred;
			else
				PagePred.get(MaxIndex).WikitextContent += "####" + dPred;
			
			if(infoboxName != null)
			{
				if(PagePred.get(MaxIndex).InfoboxName == null ||
						PagePred.get(MaxIndex).InfoboxName.equals(""))
					PagePred.get(MaxIndex).InfoboxName = infoboxName;
				else
					PagePred.get(MaxIndex).InfoboxName += "####" + infoboxName;
			}
			
			PageTpls.remove(PageTpls.size() - 1);
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
		if(dObjc.matches("\\[\\[.+\\]\\]"))
		{
			if(Entity.getId(dObjc.substring(2, dObjc.length() - 2)) > 0)
				linkOD = Entity.getId(dObjc.substring(2, dObjc.length() - 2));
			else{
				linkOD = Zhwiki.getPageId(dObjc.substring(2, dObjc.length() - 2));
				if(linkOD <= 0)
					linkOD = -1;
			}
			
			dObjc = dObjc.substring(2, dObjc.length() - 2);
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
				contOP = objP.substring(0, objP.indexOf("->"));
				linkOP = Zhwiki.getPageId(objP.substring(objP.indexOf("->") + 2));
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
		if(Page.getTitles(linkOD) != null)
		{
			tits = Page.getTitles(linkOD) + "####" + dObjc;
			//System.out.println(tits + "\t" + Page.getTitles(linkOD));
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
				}
				// multiply the longest substring length
				perc *= uFunc.GetLongestCommonSubsequence(wholeEach, title);
				// contain !
				if(wholeEach.contains(title))
					perc *= 3;
				
				
				
				if(perc > maxPerc)
					maxPerc = perc;
				System.out.println(perc + "\t" + charDiff + "\t" + title + "\t" + wholeEach);
				
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
		String [] ss ;
		ss = lastLineD.split("\t");
		pageidD = Integer.parseInt(ss[0]);
		PageTpls.add(ss[1] + "####" + ss[2]);
		String lastPred = ss[1];
		String lastObj = ss[2];
		String infoboxName = null;
		try {
			while((lastLineD = brD.readLine()) != null)
			{
				if(lastLineD.startsWith("InfoboxName:"))
					infoboxName = lastLineD.substring(12);
				ss = lastLineD.split("\t");
				if(ss[1].equals("name") || ss[1].contains("image") ||
						ss[1].contains("caption") || ss[2].endsWith(".png") ||
						ss[2].endsWith(".jpg") || ss[2].endsWith(".jpeg"))
				{
					continue;
				}
				
				int pageid = Integer.parseInt(ss[0]);
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
		PagePred.clear();
		if(lastPred == null)
		{
			info = "lastPred is null:" + pageidP;
			uFunc.Alert(true, i, info);
			return;
		}
		PagePred.add(lastPred);
		pageidP = lastPred.Pageid;
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
				PagePred.add(next);
				//System.out.println(next);
				lastPred = next;
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
