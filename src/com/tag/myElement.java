package com.tag;

import tools.URL2UTF8;
import tools.uFunc;
import triple.standardize.HTMLStdz;
import database.Entity;

public class myElement {
	public String context;
	public String link;
	
	public myElement(String iCont, String iLink)
	{
		context = iCont;
		if(iLink == null)
			System.out.println("element without link!");
		link = iLink;
	}
	public myElement(String iCont)
	{
		context = iCont;
		link = null;
	}

	public String toString()
	{
		String result = "";
		result += "context:" + context + "; " + "link:" + link;
		return result;
	}
	
	public String getStringFromMyelement(myElement UpperTitle, boolean hasLink) {
		// TODO Auto-generated method stub
		String cont = context.replaceAll("\\{\\{\\{[^\\}]{1,}\\}\\}\\}", "")
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
		if(cont.equals("") && context.equals("") == false)
			return null;
		if(link == null)
			return cont;
		String Link = link;
		if(hasLink == true && (Link.startsWith("/wiki/") || 
				Link.startsWith("http://zh.wikipedia.org/wiki/")))
		{
			if(Link.endsWith(".jpg") || Link.endsWith(".png") ||
					Link.endsWith(".svg"))
			{
				return cont;
			}
			else
			{
				String entity = URL2UTF8.unescape(Link.substring
						(Link.indexOf("/wiki/") + 6));
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
					String info = "entity pageid not found!" + cont;
					uFunc.Alert(true, "myElement", info);
				}
			}
		}
		return cont;
	}

	private String RePlaceSpaceInside(String cont) {
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
