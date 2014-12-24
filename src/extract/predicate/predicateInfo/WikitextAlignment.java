package extract.predicate.predicateInfo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Vector;

import com.tag.myPredicate;

import database.Entity;
import tools.Mysql;
import tools.uFunc;

public class WikitextAlignment {
	private static String i = "";
	private static String info = "";

	private static Vector<myPredicate> PagePred =  
			new Vector<myPredicate>();
	private static int pageidD;
	// "predicate####object####infoboxName"
	private static Vector<String> PageTpls = 
			new Vector<String>();

	/**
	 * 1. find common pageIds
	 * 2. for each pageId-triples, aligning
	 * @param m
	 * @param hzTriple
	 * @param dumpsTriple
	 * @param predTable
	 */
	public static void DO(Mysql m, String hzTriple, String dumpsTriple,
			String predTable) {
		// TODO Auto-generated method stub
		// 1. collect subIds in hzTriple and dumpsTriple
		HashMap<Integer, Integer> hzIds = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> dumpsIds = new HashMap<Integer, Integer>();
		CollectIds(m, hzIds, "select SubId from " + hzTriple);
		CollectIds(m, dumpsIds, "select SubId from " + dumpsTriple);
		
		// 2. find common pageIds
		HashMap<Integer, Integer> commonIds = new HashMap<Integer, Integer>();
		GetCommonIds(commonIds, hzIds, dumpsIds);
		
		// 3.Align
		Mysql hzTable = new Mysql("hzWikiCount2", null);
		hzTable.setQuery("select id, SubId, PredId, ObjId, Subject, Predicate, Object, "
				+ "UpperTitle, OriginalObj, Content_wikitext, Note from hzTriple where SubId = ?");
		
		Mysql dumpsTable = new Mysql("hzWikiCount2", null);
		dumpsTable.setQuery("select SubId, ObjId, Subject, Predicate, Object, InfoboxName from dumpsTriple where SubId = ?");
		
		Mysql updatehzTriple = new Mysql("hzWikiCount2", null);
		updatehzTriple.setQuery("update hzTriple set Content_wikitext= ?, InfoboxNames=? where id=?");
		
		Iterator<Entry<Integer, Integer>> it = commonIds.entrySet().iterator();
		while(it.hasNext())
		{
			Entry<Integer, Integer> next = it.next();
			int pageid = next.getKey();
			// 3.1 init PagePred, dumpsPred
			InitPagePred(PagePred, pageid, hzTable);
			InitDumpsPred(PageTpls, pageid, dumpsTable);
			Align();
			UpdatePagePred(PagePred, pageid, updatehzTriple);
			break;
		}
	}
	
	private static void UpdatePagePred(Vector<myPredicate> pagePred2,
			int pageid, Mysql updatehzTriple) {
		// TODO Auto-generated method stub
		for(myPredicate m : pagePred2)
		{
			String wikitextP = m.WikitextContent;
			String InfoboxName = m.InfoboxName;
			try {
				updatehzTriple.Query.setString(1, wikitextP);
				updatehzTriple.Query.setString(2, InfoboxName);
				updatehzTriple.Query.setLong(3, m.id);
				updatehzTriple.Query.addBatch();
				updatehzTriple.Query.executeBatch();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static void InitDumpsPred(Vector<String> pageTpls2, int pageid,
			Mysql dumpsTable) {
		pageTpls2.clear();
		try {
			dumpsTable.Query.setInt(1, pageid);
			dumpsTable.Query.addBatch();
			ResultSet rs = dumpsTable.Query.executeQuery();
			while(rs.next())
			{
				String pred = rs.getString("Predicate");
				String obj = rs.getString("Object");
				if(pred.contains("image")  || obj.matches("[0-9]{1,}px")||
						pred.contains("caption") || obj.endsWith(".png") ||
						obj.endsWith(".jpg") || obj.endsWith(".jpeg"))
					continue;
				String m = pred + "####" + obj + "####" + rs.getString("InfoboxName");
				pageTpls2.add(m);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	private static void InitPagePred(Vector<myPredicate> pagePred2, int pageid, Mysql hzTable) {
		// TODO Auto-generated method stub
		pagePred2.clear();
		try {
			hzTable.Query.setInt(1, pageid);
			hzTable.Query.addBatch();
			ResultSet rs = hzTable.Query.executeQuery();
			int nr = 1;
			String lastCont = "";
			long t = pageid ;
			t *= 1000;
			System.out.println(t);
			while(rs.next())
			{
				if(rs.getString("Predicate").equals(lastCont))
				{
					pagePred2.get(nr-2).Objs.add(rs.getString("Object"));
					continue;
				}
				myPredicate mp = new myPredicate(t + nr, pageid);
				mp.CompleteInfo(rs);
				lastCont = mp.Content;
				pagePred2.add(mp);
				nr ++;
			}
			for(int i = 0; i < pagePred2.size(); i ++)
			{
				System.out.println(i + ":******" + pagePred2.get(i));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void GetCommonIds(HashMap<Integer, Integer> commonIds,
			HashMap<Integer, Integer> hzIds, HashMap<Integer, Integer> dumpsIds) {
		// TODO Auto-generated method stub
		Iterator<Entry<Integer, Integer>> it = hzIds.entrySet().iterator();
		while(it.hasNext())
		{
			int id = it.next().getKey();
			if(dumpsIds.containsKey(id) && commonIds.containsKey(id) == false)
				commonIds.put(id, 0);
		}
		System.out.println("commonId Nr:" + commonIds.size());
	}
	
	private static void CollectIds(Mysql m, HashMap<Integer, Integer> hzIds,
			String Path) {
		// TODO Auto-generated method stub
		try {
			m.Query = m.conn.prepareStatement(Path);
			ResultSet rs = m.Query.executeQuery();
			while(rs.next())
			{
				int pageid = rs.getInt(1);
				hzIds.put(pageid, 0);
			}
			info = "infobox pageids:" + hzIds.size() + "\t" + Path;
			System.out.println(info);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(Path);
		}
		
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
				uFunc.Alert(true, i, info);
			}
			
			
			if(PagePred.get(MaxIndex).WikitextContent == null || 
					PagePred.get(MaxIndex).WikitextContent.equals(""))
			{
				PagePred.get(MaxIndex).WikitextContent = dPred;
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
			PageTpls.remove(PageTpls.size() - 1);
		}
	}

	static String outputDMiss = "";
	static String outputDMissPath = "data/predicate/AlignmentError";
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


}
