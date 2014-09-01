package com.tag;

import java.util.Vector;

import org.htmlparser.Tag;

import tools.uFunc;

public class myTag {
	public String context;
	public String link;
	public Vector<myTag> children;
	public tagType type;
	public Tag tag;
	
	public myTag(String cont)
	{
		context = uFunc.full2HalfChange(cont);
		link = null;
		children = null;
		type = tagType.PlainText;
		tag = null;
	}

	public myTag(String cont, String lk)
	{
		context = uFunc.full2HalfChange(cont);
		link = lk;
		children = null;
		type = tagType.A;
		tag = null;
	}
	
	public myTag(Tag itag)
	{
		context = uFunc.full2HalfChange(itag.toPlainTextString());
		if(itag.getTagName().equals("A"))
		{
			link = itag.getAttribute("HREF");
			if(link == null)
				System.out.println("myTag.java:link is null");
			//System.out.println("link:" + link);
			//System.out.println("context:" + itag.getText() + "\n");
			children = null;
			type = tagType.A;
		}
		else{
			link = null;
			children = null;
			type = tagType.Tag;
		}
		tag = itag;
	}
	
	public myTag(Tag itag, boolean forInfoOutput)
	{
		context = uFunc.full2HalfChange(itag.toPlainTextString());
		if(itag.getTagName().equals("A"))
		{
			link = itag.getAttribute("HREF");
			if(link == null)
				System.out.println("myTag.java:link is null");
		}
		else link = null;
		
		if(forInfoOutput == true)
		{
			children = TagChild.getChildren(itag);	
		}
		else
		{
			children = null;
		}
		type = tagType.Tag;
		tag = itag;
	}
	public myTag(String cont, Vector<myTag> children2, tagType t,
			Tag itag) {
		// TODO Auto-generated constructor stub
		context = uFunc.full2HalfChange(cont);
		link = null;
		if(children2 != null)
		{
			children = new Vector<myTag>(children2);
		}
		else
		{
			children = null;
		}
		type = t;
		tag = itag;
	}
	
	public String outputInfo()
	{
		String result = "";
		result += "*******myTag Info begin********\n";
		result += "type:" + type + "\n";
		if(tag != null)
		{
			result += "tag:" + tag.getTagName() + "\n";
		}
		if(type == tagType.A)
		{
			result += "link:" + link + "\n";
		}
		result += "context:" + context + "\n";
		if(children != null)
		{
			for(int i = 0 ; i < children.size(); i ++)
			{
				if(children.get(i).type == tagType.PlainText)
				{
					result += "\tchild" + i + ":\"" + children.get(i).context + "\"";
				}
				else
				{
					result += "\tchild" + i + ": tag:\"" + children.get(i).tag.getTagName() 
							+ ":" + children.get(i).tag + "\"";
				}
				if(children.get(i).type == tagType.A)
					result += "[" + children.get(i).link;
				result += "\n";
			}
		}
		result += "******* myTag Info end ********\n";
		return result;
	}
	
}

