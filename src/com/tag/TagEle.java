package com.tag;

import org.htmlparser.Tag;

public class TagEle {
	public String text;
	public boolean isTag;
	public TagEle()
	{
		text = "";
		isTag = false;
	}
	public TagEle(String mtext)
	{
		text = mtext;
		isTag = false;
	}
	
	public TagEle(Tag tag)
	{
		text = tag.toPlainTextString();
		isTag = true;
	}

}
