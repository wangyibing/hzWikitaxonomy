package com.tag;

import java.util.Vector;

import org.htmlparser.Tag;

public class TagInfo {

	public String tagName;
	public String tagText;
	public Vector<TagEle> children;
	public TagInfo(Tag tag)
	{
		tagName = tag.getTagName().toLowerCase();
		tagText = tag.toPlainTextString();
	}
}
