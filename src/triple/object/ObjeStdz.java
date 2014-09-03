package triple.object;

import java.util.Vector;

import org.htmlparser.Tag;

import com.tag.TagChild;
import com.tag.TagShape;
import com.tag.myElement;
import com.tag.myObj;
import com.tag.myTag;

import tools.uFunc;
import triple.standardize.HTMLStdz;

public class ObjeStdz {

	public static String splitRegex = "\n|( \\/﻿ )|、|(﻿ \\/ )|﻿(﻿ \\/)|(\\/ )";
	public static myObj standardize(myTag mtag_obj)
	{
		String result = "";
		myObj myobj ;
		result = uFunc.ReplaceBoundSpace(
				HTMLStdz.standardize(mtag_obj.tag.toPlainTextString()));
		if(result.equals(""))
			return null;
		myTag mytag;
		//uFunc.OutputTagInfo(mtag_obj.tag, "ObjeStdz.JAVA:");
		mytag = TagShape.isA(mtag_obj.tag);
		if(mytag != null)
		{
			myElement e = new myElement(mytag.context, 
					mytag.tag.getAttribute("HREF"));
			myobj = new myObj();
			myobj.addEle(e);
			return myobj;
		}
		
		
		// UL or OL
		mytag = TagShape.containULorOL(mtag_obj.tag);
		if(mytag != null)
		{
			//uFunc.OutputTagInfo(mtag_obj.tag, "ObjeStdz.java:object is ul");
			myobj = ULorOL2Myobj(mytag.tag);
			//myobj.OutputEle(mytag.tag);
			return myobj;
		}

		// is Plain text
		String objString = TagShape.isPlainText(mtag_obj.tag);
		if(objString != null)
		{
			myobj = new myObj(Text2Vector(objString));
			//System.out.println("ObjeStdz.java:obj is plain text:" + result);
			return myobj;
		}
		
		// Plaintexts + tags
		myTag m = new myTag(mtag_obj.tag, true);
		if(m.children == null)
		{
			if(mtag_obj.context.equals(""))
			{
				//System.out.println("ObjeStdz.java:mark2*****" + result);
				return null;
			}
			myobj = new myObj();
			for(String ss : mtag_obj.context.split(splitRegex))
			{
				myElement e = new myElement(ss);
				myobj.addEle(e);
			}
			return myobj;
		}

		// single tag, not A
		Vector<myTag> children = TagChild.getChildren(mtag_obj.tag);
		if(children == null)
		{
			myobj = new myObj();
			for(String ss : result.split(splitRegex))
			{
				myElement e = new myElement(ss);
				myobj.addEle(e);
			}
			return myobj;
		}
		else
		{
			return Children2Vector(children);
		}
		
		
	}

	private static myObj Children2Vector(Vector<myTag> children) {
		// TODO Auto-generated method stub
		if(children == null || children.size() < 1)
			return null;
		while(children.size() == 1)
		{
			if(children.get(0).tag == null)
				break;
			String tName = children.get(0).tag.getTagName();
			if(tName.equals("A") == false )
			{
				if(tName.equals("P") == true && 
						TagChild.getChildren(children.get(0).tag)!=null)
					children = TagChild.getChildren(children.get(0).tag);
				else break;
			}
			else
			{
				break;
			}
		}
		int length = children.size();
		myObj result = new myObj();
		String text = "";
		for(int i = 0 ; i < length; i ++)
		{
			String tText = HTMLStdz.standardize(children.get(i).context);
			if(children.get(i).tag != null &&
					children.get(i).tag.getTagName().equals("BR"))
			{
				if(text.equals("") == false)
				{
					myElement e = new myElement(text);
					result.addEle(e);
					text = "";
				}
				continue;
			}
			myElement e;
			if(isLinkElement(children, i, text) == true)
			{
				e = new myElement(tText, children.get(i).link);
				result.addEle(e);
				text = "";
			}
			else if(TagShape.isTag(children.get(i).tag, "P") == true)
			{
				Vector<myTag> tChildren = 
						TagChild.getChildren(children.get(i).tag);
				myObj subobj = Children2Vector(tChildren);
				//System.out.println("is sub object:");
				//subobj.OutputEle(null);
				if(subobj != null)
					result.addEle(subobj.eles);
				text = "";
			}
			else
			{
				if((i == length-1 || tText.contains("\n") || 
						children.get(i+1).context.equals("\n") ||
						(children.get(i+1).tag != null &&
								children.get(i+1).tag.getTagName().equals("BR")))
					&& uFunc.isPunctuations(text+tText) == false)
				{
					String s = text + tText;
					if(s.equals("") == false)
					{
						for(String ss : s.split(splitRegex))
						{
							e = new myElement(ss);
							result.addEle(e);
						}
								
						text = "";
					}
					//System.out.println("ObjeStdz.java:empty text!" + children.get(0).context);
					
				}
				else{
					if(uFunc.isPunctuations(tText) && text.equals(""))
						text = "";
					else text += tText;
					//System.out.println("ObjeStdz.java:" + text);
				}
			}
			
			if(tText.contains("\n"))
				text = "";
		}
		return result;
	}

	private static boolean isLinkElement(Vector<myTag> children, 
			int i, String text) {
		// TODO Auto-generated method stub
		String tText = children.get(i).context;
		int length = children.size();
		if(children.get(i).link != null && uFunc.isPunctuations(text))
		{
			if(tText.contains("\n") || i >= length-1 || 
				uFunc.isPunctuations(children.get(i+1).context)||
				children.get(i+1).context.startsWith("和"))
				return true;
			//System.out.println("ObjeStdz.java:current:" + tText + "; next:" + children.get(i+1).context);
		}
		return false;
	}


	private static Vector<myElement> Text2Vector(String objString) {
		if(objString == null || objString.length() < 1)
			return null;
		String [] s = objString.split("(<br(\\s?)/>)|( / )|、|\n|﻿(﻿ / ﻿)");
		Vector<myElement> eles = new Vector<myElement>();
		for(int i = 0 ; i < s.length; i ++)
		{
			myElement e = new myElement(s[i]);
			eles.add(e);
		}
		if(eles.size() < 1)
			return null;
		return eles;
	}

	/**
	 * Nest form not included!!!!
	 * @param tag
	 * @return
	 */
	private static myObj ULorOL2Myobj(Tag tag) {
		// TODO Auto-generated method stub
		String tName = tag.getTagName().toLowerCase();
		while(tName != null && tName.equals("ul") == false 
				&& tag.getTagName().equals("ol") == false)
		{
			Vector<myTag> child = TagChild.getChildren(tag);
			if(child.size() == 1){
				tag = child.get(0).tag;
				if(tag == null)
					return null;
				tName = tag.getTagName().toLowerCase();
			}
			else return null;
		}
		Vector<myTag> children = TagChild.getChildren(tag);
		myObj myobj = new myObj();
		Tag ctag;
		myTag mtag;
		for(int i = 0 ; i < children.size(); i ++)
		{
			ctag = children.get(i).tag;
			if(ctag != null && ctag.getTagName().equals("LI"))
			{
				myElement e;
				mtag = TagShape.isA(ctag);
				if(mtag != null)
				{
					e = new myElement(mtag.context,
							mtag.tag.getAttribute("HREF"));
					myobj.addEle(e);
				}
				else
				{
					e = new myElement(children.get(i).context);
					myobj.addEle(e);
					/*
					//System.out.println("ObjeStdz.java:" + ctag.toPlainTextString());
					Vector<myTag> child = TagChild.getChildren(ctag);
					if(child == null)
					{
						if(children.get(i).context != null)
						{
							
						}
					}
					else
					{
						System.out.println("ObjeStdz.java:" + ctag.toPlainTextString());
						for(int j = 0 ; j < child.size(); j ++)
						{
							String ss = child.get(j).context; 
							System.out.println("\t" + ss);
							if(ss.equals("") == false && uFunc.isPunctuations(ss) == false)
							{
								e = new myElement(ss);
								myobj.addEle(e);
							}
							
						}
					}
					*/
				}
				//continue;
			}
			
			if(ctag == null)
			{
				System.out.println("ObjeStdz.java:not li element:" +
						children.get(i).context);
			}
		}
		if(myobj.eleNr < 1)
			return null;
		return myobj;
	}
}
