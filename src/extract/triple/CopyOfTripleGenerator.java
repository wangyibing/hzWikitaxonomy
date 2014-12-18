package extract.triple;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Tag;

import com.tag.myElement;
import com.tag.myObj;

import database.Entity;
import extract.GeneratorDistributor;
import tools.URL2UTF8;
import tools.uFunc;
import triple.standardize.HTMLStdz;

public class CopyOfTripleGenerator {
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
		String contP = getStringFromMyelement(predi.eles.get(0), NoLink);
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
			contP = getStringFromMyelement(upperTitle, !NoLink);
			//uFunc.Alert(i, "subTitle is pred:" + contP);
		}
		else{
			contP = getStringFromMyelement(predi.eles.get(0), !NoLink);
		}
		if(contP == null) return null;
		// 郭泓志\t打击：左\t投球：左
		if(contP.contains(":") == true)
		{
			if(objc.eleNr == 1 && 
					objc.eles.get(0).context.contains(":") == true)
			{
				GeneratorDistributor.distribute(contP, PageId,
						UpperTitle, upperTitleMinus, tRTitleNr, null, tRtagId);
				GeneratorDistributor.distribute(objc.eles.get(0).context,
						PageId, UpperTitle, upperTitleMinus, tRTitleNr, null, tRtagId);
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
			String contO = getStringFromMyelement(objc.eles.get(i), !NoLink);
			if(contO == null || contO.equals("") ||
					// page's title can't be predicate
					isPageTitle(pageid, contP))
				continue;
			info = pageid + "\t" + contP + "\t" + contO + remark + "\n";
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

	public static String getStringFromMyelement(myElement predi, boolean hasLink) {
		// TODO Auto-generated method stub
		if(predi == null)
			return null;
		String cont = predi.context.replaceAll("\\{\\{\\{[^\\}]{1,}\\}\\}\\}", "")
				.replaceAll("[0-9]{1,}px", "");
		if(cont.contains("←") || cont.contains("↙") 
			|| cont.contains("◄") || cont.contains("►")
			|| cont.startsWith("<")) 
			return null;
		cont = uFunc.ReplaceBoundSpace(
				HTMLStdz.standardize(cont));
		cont = RePlaceSpaceInside(cont);
		if(cont.contains("•"))
		{
			if(UpperTitle != null)
			{
				//System.out.println(UpperTitle.context + "\t" + cont);
				String upp = UpperTitle.context.replaceAll("(\\(.+\\))|(\\[.+\\])", "");
				if(cont.contains("总") || cont.contains("陆地")
						|| cont.contains("密度")|| cont.contains("水域")
						|| cont.contains("排名")|| cont.contains("首都")
						|| cont.contains("市")|| cont.contains("都会区"))
				{
					cont = upp + cont.replaceAll("(?m)^[•_ ]+", "");
				}
			}
			cont = cont.replaceAll("(?m)^[•_ ]+", "");
			//System.out.println("SecondStandardize.java:" + PageId + cont);
		}
		if(cont.equals("") && predi.context.equals("") == false)
			return null;
		if(predi.link == null)
			return cont;
		String link = predi.link;
		if(hasLink == true && (link.startsWith("/wiki/") || 
				link.startsWith("http://zh.wikipedia.org/wiki/")))
		{
			if(link.endsWith(".jpg") || link.endsWith(".png") ||
					link.endsWith(".svg"))
			{
				return cont;
			}
			else
			{
				String entity = URL2UTF8.unescape(link.substring
						(link.indexOf("/wiki/") + 6));
				String lower = entity.toLowerCase();
				if(lower.startsWith("category:") || lower.startsWith("special:")
						|| lower.startsWith("portal:") || lower.startsWith("wikipedia:"))
					return cont;
				if(entity.contains("#"))
					entity = entity.substring(0,  entity.indexOf("#"));
				int pageid = Entity.getId(entity);
				if(pageid > 0)
				{
					return cont + "->" + "[" + pageid + "]";
				}
				else{
					info = "entity pageid not found!" + cont + " " + PageId;
					uFunc.Alert(true, i, info);
				}
			}
		}
		return cont;
	}

	private static String RePlaceSpaceInside(String cont) {
		// TODO Auto-generated method stub
		if(cont == null || cont.equals(""))
			return "";
		char [] cs = cont.toCharArray();
		StringBuffer sb = new StringBuffer();
		sb.append(cs[0]);
		for(int i = 1 ; i < cs.length - 1; i ++)
		{
			if((cs[i] == ' ') 
					&& uFunc.isChineseChar(cs[i+1])
					&& uFunc.isChineseChar(cs[i-1]))
			{
				continue;
			}
			sb.append(cs[i]);
		}
		sb.append(cs[cs.length-1]);
		return sb.toString();
	}

}
