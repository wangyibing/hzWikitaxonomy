package com.tag;

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

}
