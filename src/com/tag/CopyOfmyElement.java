package com.tag;

public class CopyOfmyElement {
	public String context;
	public String link;
	
	public CopyOfmyElement(String iCont, String iLink)
	{
		context = iCont;
		if(iLink == null)
			System.out.println("element without link!");
		link = iLink;
	}
	public CopyOfmyElement(String iCont)
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
}
