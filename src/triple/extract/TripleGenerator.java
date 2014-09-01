package triple.extract;

import com.tag.myElement;
import com.tag.myObj;

import database.Entity;
import tools.URL2UTF8;
import tools.uFunc;
import triple.object.ObjeStdz;
import triple.standardize.HTMLStdz;

public class TripleGenerator {
	public static boolean NoLink = false;
	public static myElement UpperTitle;
	public static int PageId;

	public static String GetTriples(int pageid, myObj predi, myObj objc, 
			myElement upperTitle, int tRTitleNr) {
		// TODO Auto-generated method stub
		UpperTitle = upperTitle;
		PageId = pageid;
		if(predi == null || objc == null)
		{
			//System.out.println("SecondStandardize.java:error in object:" + pageid + ";" + predi);
			return null;
		}
		if(predi.eles.size() < 1)
		{
			//System.out.println("SecondStandardize.java:predi null:" + pageid + ":" + objc.eles.get(0).context);
			return null;
		}
		
		String contP = getStringFromMyelement(predi.eles.get(0), NoLink);
		if(contP == null || contP.equals(""))
		{
			//System.out.println("SecondStandardize.java:" + "conP empty:pageid:" + pageid);
			return null;
		}
		
		String result = "";
		for(int i = 0; i < objc.eles.size(); i ++)
		{
			String contO = getStringFromMyelement(objc.eles.get(i), !NoLink);
			if(contO == null || contO.equals(""))
			{
				//System.out.println("SecondStandardize.java:" +  "contO empty:pageid:" + pageid + " " + contP);
				continue;
			}
			if(predi.eleNr == objc.eleNr && objc.eleNr > 1){
				contP = getStringFromMyelement(predi.eles.get(i), NoLink);
			}
			if(uFunc.isPeriod(contP) || contP.contains("〒"))
			{
				if(tRTitleNr <= 1)
				{
					//System.out.println("not stand sub-title，can't change!" + pageid);
					continue;
				}
				if(contP.contains("〒") == false && upperTitle == null)
				{
					//System.out.println(pageid + ";" + contP);
					continue;
				}
				contO += "####" + contP;
				contP = upperTitle.context;
			}
			if(isNotPredicate(contP))
				continue;
			// page's title can't be predicate
			if(Entity.getEntityId(uFunc.Simplify(contP.replaceAll(" |_", "").toLowerCase())) == pageid)
			{
				//System.out.println("SecondStandardize.java:is Entity:" + pageid + ":" + contP);
				continue;
			}
			//System.out.println(contP + ":" + Entity.getEntityId(uFunc.Simplify(contP.replaceAll(" |_", ""))));
			if(contP.contains(":") == true)
			{
				if(contO.contains(":") == true)
				{
					result += getTripleFromSgl(contP, pageid);
					result += getTripleFromSgl(contO, pageid);
				}
				else
				{
					//System.out.println("contO not :" + PageId + ":" + contP + "###" + contO);
				}
				continue;
			}
			if(contP.matches("\\(.+\\)") == false)
				contP = contP.replaceAll("(?m)(\\(.+\\))$", "");
			result += pageid + "\t" + contP + "\t" + contO + "\n";
		}
		if(result.equals("") == false)
			return result;
		//System.out.println("not in my cases!" + objc.eles.size());
		return null;
	}

	public static String getTripleFromSgl(String cont, int pageid2) {
		// TODO Auto-generated method stub
		PageId = pageid2;
		String result = "";
		if(cont.split(":|：").length < 2){
			System.out.println("SecondStandardize.java: error");
			return "";
		}
		int index = cont.indexOf(":");
		if(index < 0 || (index > cont.indexOf("：") && cont.indexOf("：") > 0))
			index = cont.indexOf("：");
		String predi = uFunc.ReplaceBoundSpace(cont.substring(0, index));
		String objc = uFunc.ReplaceBoundSpace(cont.substring(index + 1));
		myObj p, o;
		p = new myObj();
		p.addEle(new myElement(predi));
		o = new myObj();
		for(String ss : objc.split(ObjeStdz.splitRegex))
			o.addEle(new myElement(ss));
		result += GetTriples(PageId, p, o, null, 0);
		if(result.contains("经纬") == false){
			//System.out.print("SecondStandardize.java:\n\t" + result);
		}
		return result;
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
		String cont = predi.context;
		cont = uFunc.ReplaceBoundSpace(
				HTMLStdz.standardize(cont));
		if(cont.contains("•"))
		{
			if(UpperTitle != null)
			{
				//System.out.println(UpperTitle.context + "\t" + cont);
				String upp = UpperTitle.context.replaceAll("(\\(.+\\))|(\\[.+\\])", "");
				//uFunc.countStrings(upp + ":" + cont);
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
		{
			//System.out.println("cont: is emptey;Context:" + predi.context + " link:" + predi.link);
			return null;
		}
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
				if(entity.contains("#"))
					entity = entity.substring(0,  entity.indexOf("#"));
				int pageid = Entity.getEntityId(entity);
				if(pageid > 0)
				{
					return "[" + Entity.getEntityTitle(pageid) + "]";
				}
				else
				{
					/*
					System.out.println("SecondStandardize.java:cont:entity:" + 
							entity + "; " + 
							"link:" + URL2UTF8.unescape(link));
							*/
					return cont;
				}
			}
		}
		return cont;
	}

}
