package com.tag;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Tag;

import tools.uFunc;

public class TagShape {

	public static Vector<myTag> children = 
			new Vector<myTag>();

	public static boolean isUselessTag(Tag tag)
	{
		if(tag == null)
			return false;
		String tagName = tag.getTagName();
		if(tagName.equals("BR") ||
				tagName.equals("DIV") ||
				tagName.equals("SMALL"))
		{
			return true;
		}
		return false;
	}
	/**
	 * only one element, without any <A> element 
	 * @param father
	 * @return
	 */
	public static String isPlainText(Tag father)
	{
		if(father == null){
			return null;
		}
		children = TagChild.getChildren(father);
		if(children == null)
			return null;
		while(children.size() == 1)
		{
			myTag top = children.get(0);
			if(top.type == tagType.PlainText)
			{
				return top.context;
			}
			if(top.tag != null && top.tag.getTagName().equals("A"))
				return null;
			if(top.tag != null)
			{
				children = TagChild.getChildren(top.tag);
				if(children == null)
					return null;
			}
			else break;
			
		}
		return null;
	}
	

	public static boolean isTag(Tag father, String TagName) {
		// TODO Auto-generated method stub
		if(father == null)
			return false;
		if(father.getTagName().equals(TagName.toUpperCase()))
			return true;
		return false;
	}
	
	/**
	 * <a href="" title = "" >...</a>
	 * TODO get"..." : getText(current) or toPlainText(?)
	 * @param father
	 * @return
	 */
	public static myTag isA(Tag father)
	{
		if(father == null)
			return null;
		if(father.getTagName().equals("A"))
			return new myTag(father);
		children = TagChild.getChildren(father);
		if(children == null)
			return null;
		//System.out.println();
		while(getOnlySon(children, false) >= 0)
		{
			myTag top = children.get(getOnlySon(children, false));
			//if(top.tag != null)
			//	System.out.println("top tag:" + top.tag.getTagName());
			if(top.tag != null && top.tag.getTagName().equals("A"))
			{
				return top;
			}
			if(top.tag != null)
			{
				children = TagChild.getChildren(top.tag);
				if(children == null)
					return null;
			}
			else break;
		}
		return null;
	}

	private static int getOnlySon(Vector<myTag> children2, boolean output) {
		// TODO Auto-generated method stub
		if(children2 == null || children2.size() < 1)
			return -1;
		int son = -1;
		for(int i = 0 ; i < children2.size(); i ++)
		{
			if(uFunc.isPunctuations(children2.get(i).context) == false)
			{
				if(output)
				{
					System.out.println("TagShape.java:" + i + ":\"" +  
							children2.get(i).context + "\"");
				}
				if(son >= 0)
					return -1;
				else
				{
					son = i;
				}
			}
		}
		return son;
	}
	/**
	 * .../...
	 * only "/" exist in text, not other punctuation
	 * @param father
	 * @return
	 */
	public static myTag isSplit(Tag father)
	{
		String text = father.toPlainTextString();
		Pattern pattern=Pattern.compile(
				"[`~!@#$^&*()=|{}':;',\\[\\].<>?~！@#￥……"
				+ "&*（）()——|{}【】‘；：”“'。，、？]");
		Matcher matcher = pattern.matcher(
				uFunc.full2HalfChange(text));
		if(matcher.find()){
			return null;
		}
		if(text.contains("/") || text.contains("\n"))
		{
			return new myTag(father.toPlainTextString(), children,
					tagType.Split, father);
		}
		return null;
	}
	
	/**
	 * "...(...)"
	 * @param father
	 * @return
	 */
	public static myTag containNote(Tag father)
	{
		String text = father.toPlainTextString();
		Pattern pattern=Pattern.compile(
				"[`~!@#$^&*=|{}':;',\\[\\].<>/?~！@#￥……"
				+ "&*——|{}【】‘；：”“'。，、？]");
		Matcher matcher = pattern.matcher(uFunc.full2HalfChange(text));
		if(matcher.find()){
			return null;
		}
		if((text.contains("(") ||text.contains("（")) && 
				(text.contains(")") || text.contains("）")))
		{
			children = TagChild.getChildren(father);
			return new myTag(father.toPlainTextString(), children,
					tagType.Note, father);
		}
		return null;
	}
	
	/**
	 * <ul>
	 * <tab><li>...</li>
	 * <tab><li>...</li>
	 * </ul>
	 * @param father
	 * @return
	 */
	public static myTag containULorOL(Tag father) {

		if(father == null)
			return null;
		children = TagChild.getChildren(father);
		if(children == null)
			return null;
		//System.out.println();
		while(children.size() >= 1)
		{
			myTag top = children.get(0);
			//if(top.tag != null)
			//	System.out.println("top tag:" + top.tag.getTagName());
			if(top.tag != null && (top.tag.getTagName().equals("UL")||
					top.tag.getTagName().equals("OL")))
			{
				return top;
			}
			if(top.tag != null)
			{
				//System.out.println(top.tag.getTagName());
				children = TagChild.getChildren(top.tag);
				if(children == null)
					break;
			}
			else break;
		}
		return null;
	}
}
