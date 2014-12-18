package extract.triple;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Tag;

import com.tag.myElement;
import com.tag.myObj;

import database.Entity;
import extract.GeneratorDistributor;
import tools.uFunc;

public class TripleGenerator {
	public static String i = "TripleGenerator";
	public static String info;
	
	public static boolean NoLink = false;
	public static myElement UpperTitle;
	public static int PageId;

	public static String GetTriples(int pageid, myObj predi, myObj objc, 
			myElement upperTitle, myElement upperTitleMinus, int tRTitleNr, 
			Tag objTag, int tRtagId) {
		// TODO Auto-generated method stub
		String remark = "";
		UpperTitle = upperTitle;
		PageId = pageid;
		if(predi == null || objc == null || predi.eles.size() < 1)
		{
			//System.out.println("SecondStandardize.java:error in object:" + pageid + ";" + predi);
			return null;
		}
		String contP = predi.eles.get(0).getStringFromMyelement(UpperTitle, NoLink);
		if(contP == null || contP.equals("") || isNotPredicate(contP) ||
				// page's title can't be predicate
				isPageTitle(pageid, contP))
		{
			//conP empty
			return null;
		}
		if(uFunc.isPeriod(contP) ||uFunc.Contain(contP, "(?m)^[0-9]{4}年?") 
				|| contP.contains("〒") || 
				contP.startsWith("- ") || contP.startsWith("-") ||
				contP.startsWith("–"))
		{
			// not stand sub-title，can't change!
			if(upperTitleMinus != null && uFunc.Contain
					(upperTitleMinus.context, "(?m)^[0-9]{4}年?") == false)
			{
				upperTitle = upperTitleMinus;
			}
			if(tRTitleNr <= 1)
				return null;
			if(contP.contains("〒") == false && upperTitle == null)
				return null;
			remark += "##" + contP;
			contP = upperTitle.getStringFromMyelement(null, !NoLink);
			//uFunc.Alert(i, "subTitle is pred:" + contP);
		}
		else{
			contP = predi.eles.get(0).getStringFromMyelement(UpperTitle, !NoLink);
		}
		if(contP == null) return null;
		// 郭泓志\t打击：左\t投球：左
		if(contP.contains(":") == true)
		{
			if(objc.eleNr == 1 && 
					objc.eles.get(0).context.contains(":") == true)
			{
				GeneratorDistributor.distribute(contP, PageId,
						UpperTitle, upperTitleMinus, tRTitleNr, objTag, tRtagId);
				GeneratorDistributor.distribute(objc.eles.get(0).context,
						PageId, UpperTitle, upperTitleMinus, tRTitleNr, objTag, tRtagId);
			}
			// else is considered
			return null;
		}
		if(contP.matches("\\(.+\\)") == false)
			contP = contP.replaceAll("(?m)( |_)?(\\(.+\\))$", "");
		
		String result = "";
		contP = uFunc.ReplaceBoundSpace(
				contP.replaceAll("(?m)^[[•\\s\\-]]+", ""));
		Pattern p = Pattern.compile("[0-9]{4}年");
		Matcher m = p.matcher(contP);
		if(m.find())
		{
			int ind = m.start();
			remark += "##" + contP.substring(ind);
			contP = uFunc.ReplaceBoundSpace(contP.substring(0, ind));
		}
		for(int i = 0; i < objc.eles.size(); i ++)
		{
			if(objc.eles.get(i) == null)
				continue;
			String contO = objc.eles.get(i).getStringFromMyelement(UpperTitle, !NoLink);
			if(contO == null || contO.equals("") ||
					// page's title can't be predicate
					isPageTitle(pageid, contP))
				continue;
			info = pageid + "\t" + contP + "\t" + contO + remark + "\n";
			Triple2Mysql.insert(pageid, contP, contO, objTag,
					remark, UpperTitle, tRtagId);
			result += info;
		}
		if(result.equals("") == false)
			return result;
		return null;
	}
	
	private static boolean isPageTitle(int pageid2, String contP) {
		// TODO Auto-generated method stub
		String titles = Entity.getTitles(pageid2);
		if(titles != null)
		{
			for(String title : titles.split("####"))
			{
				if(uFunc.Simplify(contP.toLowerCase().replaceAll(" |_", ""))
						.equals(uFunc.Simplify(title.toLowerCase().replaceAll(" |_", ""))))
				{
					//uFunc.Alert(true, i, title + ":" + contP);
					return true;
				}
			}
		}
		return false;
	}
	
	private static boolean isNotPredicate(String contP) {
		// TODO Auto-generated method stub
		contP = uFunc.Simplify(contP);
		if(contP.contains("伤亡") && uFunc.containNumber(contP))
			return true;
		if(contP.contains("国旗") || contP.contains("←") 
				|| contP.contains("国旗") || contP.contains("«")  )
			return true;
		if(contP.contains("查") && contP.contains("论") && contP.contains("编"))
			return true;
		return false;
	}


}
