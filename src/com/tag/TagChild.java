package com.tag;

import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Node;
import org.htmlparser.Tag;
import org.htmlparser.Text;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.NodeVisitor;

import tools.uFunc;

public class TagChild {

	/**
	 * contains html-tag and plain-text,
	 * remove "div", "small", "img" tags
	 */
	public static Vector<myTag> children = 
			new Vector<myTag>();
	private static NodeVisitor ChildVisitor;
	private static boolean CdvisInited = false;
	private static HashMap<Tag, Integer> TagLevel = 
			new HashMap<Tag, Integer>();

	private static boolean isDescendantTag = false;
	private static Tag DescendantTag = null;
	public static String info;
	public static String c = "TagChild";
	

	public static String GetChildren(Tag tr, String string) {
		// TODO Auto-generated method stub
		String regex = "<" + string + ">((.|\\n)+?)</" + string + ">";
		regex = regex.toLowerCase();
	 	Pattern p = Pattern.compile(regex);
	 	Matcher match = p.matcher(tr.toHtml().toLowerCase());
	 	//uFunc.Alert(true, c, regex + "\n" + tr.toHtml().toLowerCase());
	 	if(match.find())
	 		return match.group(1);
		return null;
	}

	public static boolean isChild(Tag father, Tag Son)
	{
		if(father == null)
			return false;
		if(father.getChildren() == null)
			return false;
		return 
				father.getChildren().contains(Son);
	}
	public static boolean isChild(Node father, Tag Son)
	{
		if(father == null || father.getChildren() == null)
			return false;
		return 
				father.getChildren().contains(Son);
	}
	public static Tag getDescendantTag(Tag father, String DescendName)
	{
		isDescendantTag = false;
		DescendantTag = null;
		try {
			if(father.getChildren() == null)
				return null;
			father.getChildren().visitAllNodesWith(new NodeVisitor(){
				public void visitTag(Tag tag){
					if(isDescendantTag)
						return;
					if(tag.getTagName().toLowerCase().equals(DescendName.toLowerCase()))
					{
						isDescendantTag = true;
						DescendantTag = tag;
						//System.out.println("tag:" + tag.toHtml());
						return;
					}
					
				}
			});
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isDescendantTag = false;
		}
		return DescendantTag;
	}
	public static boolean containDescendantTag(Tag father, String DescendName)
	{
		isDescendantTag = false;
		DescendantTag = null;
		try {
			if(father.getChildren() == null)
				return false;
			father.getChildren().visitAllNodesWith(new NodeVisitor(){
				public void visitTag(Tag tag){
					if(isDescendantTag)
						return;
					if(tag.getTagName().toLowerCase().equals(DescendName.toLowerCase()))
					{
						isDescendantTag = true;
						DescendantTag = tag;
						//System.out.println("tag:" + tag.toHtml());
						return;
					}
					
				}
			});
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			isDescendantTag = false;
		}
		return isDescendantTag;
	}
	public static Vector<myTag> getChildren(Tag father)
	{
		Vector<myTag> sons = getChildren_old(father);
		Vector<myTag> tsons = getChildren_old(father);
		while(tsons != null && tsons.size() == 1)
		{
			sons = tsons;
			if(sons == null || sons.get(0).tag == null)
				break;
			String tname = sons.get(0).tag.getTagName().toLowerCase();
			if(tname.equals("span") || tname.equals("div"))
			{
				String className = sons.get(0).tag.getAttribute("CLASS");
				if(className != null)
					className = className.toLowerCase();
				if(className != null && 
						(className.contains("navbar") ||
								className.contains("navbox")))
					return null;
				tsons = getChildren_old(sons.get(0).tag);
			}
			else break;
		}
		if(sons!= null && sons.size() < 1)
			return null;
		return sons;
	}
	
	
	public static Vector<myTag> getChildren_old(Tag father)
	{
		if(father == null)
			return null;
		if(father.getChildren() == null || 
				father.getChildren().size() < 1)
			return null;
		Init(father);
		try {
			father.getChildren().visitAllNodesWith(ChildVisitor);
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//uFunc.OutputTagInfo(father, "");
			uFunc.OutputTagInfo(father, "");
			return null;
		}
		return new Vector<myTag>(children);
	}

	public static Tag getOnlyTagSon(Tag father)
	{
		if(father == null)
			return null;
		Vector<myTag> children = TagChild.getChildren(father);
		if(children == null)
			return null;
		Tag mt = null;
		for(int i = 0 ;i < children.size(); i ++)
		{
			Tag son = children.get(i).tag;
			if(son != null && son.getTagName().equals("BR") == false)
			{
				if(mt == null)
					mt = son;
				else
					return null;
			}
		}
		return mt;
	}
	
	private static String lastTagName = "";
	private static void Init(Tag father) {
		// TODO Auto-generated method stub
		TagLevel.clear();
		children.clear();
		TagLevel.put(father, 0);
		
		if(CdvisInited == true)
			return;
		ChildVisitor = new NodeVisitor(){
			public void visitTag(Tag tag){
				String tName = tag.getTagName();
				lastTagName = tName;
				int level = 1;
				if(TagLevel.containsKey(tag.getParent()) == true)
				{
					level += TagLevel.get(tag.getParent());
					TagLevel.put(tag, level);
					if(level == 1)
					{
						boolean isNote = uFunc.HasAttriCompnt
								(tag, "STYLE", "(font\\-size\\:[0-9]{1,}\\%)|(small)");
						if(tName.equals("SMALL")
								|| tName.equals("IMG") || tName.equals("B")
								|| isNote)
						{
							return;
						}
						myTag mtag = new myTag(tag);
						children.add(mtag);
					}
				}
				else
				{
					uFunc.OutputTagInfo(tag, 
							"tag's parent not exist before");
				}
			}
			public void visitStringNode(Text string){
				String text = uFunc.full2HalfChange(
						string.toPlainTextString());
				if(text.equals("\n"))
					return;
				//System.out.println("string:\"" + text + "\"");
				if(TagLevel.containsKey(string.getParent()) == true)
				{
					if(lastTagName.toLowerCase().equals("small"))
						return;
					if(TagLevel.containsKey(string.getParent()) == false)
					{
						System.out.println("TagChild.java: string's father node not exist!");
						return;
					}
					int level = 1 + TagLevel.get(string.getParent());
					if(level == 1)
					{
						myTag mtag = new myTag(string.toPlainTextString());
						children.add(mtag);
					}
				}
			} 
		};
		CdvisInited = true;
	}

}
