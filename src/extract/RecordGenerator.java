package extract;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Tag;

import com.tag.TagChild;
import com.tag.myElement;
import com.tag.myObj;
import com.tag.myTag;

import database.Entity;
import tools.uFunc;
import triple.object.ObjeStdz;
import triple.predicate.PredStdz;

public class RecordGenerator {
	private final static String c = "RecordGenerator";
	private static String info;
	private static Vector<myTag> tags = new Vector<myTag>();
	private static int PageId ;
	public static String GenerFromTR(int pageid, Tag tr)
	{
		if(TagChild.containDescendantTag(tr, "img"))
			InfoboxNode.infoboxIMG = true;
		if(Init(tr) == false)
		{
			return null;
		}
		tags = TagChild.getChildren(tr);
		if(tags == null)
		{
			uFunc.OutputTagInfo(tr, "no children");
			return null;
		}
		PageId = pageid;
		
		int TRformat = -1;
		if(tags.size() == 2)
		{
			myTag tagPre = tags.get(0);
			myTag tagObj = tags.get(1);
			if(TagChild.containDescendantTag(tags.get(0).tag, "b") ||
					tagPre.tag.getTagName().equals("TH"))
			{
					UpdateUpperTitle(tr, pageid, true);
			}
			String preName = tagPre.tag.getTagName().toLowerCase();
			String objName = tagObj.tag.getTagName().toLowerCase();
			
			if((preName != null && objName != null ))
			{
				if((preName.equals("th") && objName.equals("td")))
					TRformat = 1;
				else if(preName.equals("td") && objName.equals("td") 
						&& (tagPre.context.endsWith(":") || tagPre.context.endsWith("：")))
					TRformat = 1;
				else if((preName.equals("td") && objName.equals("td")))
				{
					if(isNotPredicate(tagPre))
					{
						TRformat = 0;
					}
					else
					{
						TRformat = 1;
					}
				}
				else{
					TRformat = 0;
				}
				// return to false only when new infobox or uppertitle exist
				if((preName.equals("th") && objName.equals("th")) &&
						InfoboxNode.infoboxIMG == false)
				{
					String h1 = uFunc.ReplaceBoundSpace(
							tags.get(0).tag.toPlainTextString());
					String h2 = uFunc.ReplaceBoundSpace(
							tags.get(1).tag.toPlainTextString());
					if(h1.equals("") == false && h2.equals("") == false)
						InfoboxNode.ListTable = true;
				}
					

			}
			
			switch(TRformat)
			{
			case 1:
				myObj predi = ObjeStdz.standardize(tagPre);
				myObj objc = ObjeStdz.standardize(tagObj);
				// normal triple exist, namely subtitle exist
				if(InfoboxNode.infoboxIMG == false &&
						predi != null && objc != null)
				{
					InfoboxNode.infoboxIMG = true;
					InfoboxNode.TRTitleNr ++;
					//System.out.println("TripleGenerator.java:" + InfoboxNode.TRTitleNr);
				}
				//System.out.println("TripleGenerator.java:" + tagPre.outputInfo() + "\t" +  GeneratorDistributor.distribute(pageid, predi, objc, null, 2));
				String triple = GeneratorDistributor.distribute(
						pageid, predi, objc, InfoboxNode.UpperTitle,
						InfoboxNode.UpperTitleMinus, InfoboxNode.TRTitleNr);
				if(InfoboxNode.ListTable == true && triple != null &&
						triple.equals("") == false)
				{
					/*
					info = pageid + "InfoboxNode.ListTable = false" + "\n" +
							triple;
					uFunc.Alert(true, c, info);
					*/
					return "";
				}
				return triple;
			case 2:
				
			}
		}
		else if(tags.size() == 1)
		{
			InfoboxNode.ListTable = false;
			String tName = tags.get(0).tag.getTagName().toLowerCase();

			// top img
			if(InfoboxNode.infoboxIMG == false && 
					TagChild.containDescendantTag(tags.get(0).tag, "img"))
			{
				InfoboxNode.TRTitleNr = 0;
				InfoboxNode.infoboxIMG = true;
				InfoboxNode.TRTitleNr = 0;
				//System.out.println("TripleGenerator.java:IMG" + tags.get(0).tag.toPlainTextString());
			}
			
			String context =uFunc.Simplify(uFunc.ReplaceBoundSpace(
					tags.get(0).tag.toPlainTextString())); 
			if(context.matches(".+([\\u4e00-\\u9fa5]| |_)+(\\:|：).+") &&
					TagChild.containDescendantTag(tags.get(0).tag, "tr") == false)
			{
				// single tr containing ":"
				return null;
				//return GeneratorDistributor.distribute(context, pageid, InfoboxNode.UpperTitle, InfoboxNode.TRTitleNr);
			}
			// subTitle
			if(tName.equals("th"))
			{
				return UpdateUpperTitle(tr, pageid, false);
			}
			else if(TagChild.containDescendantTag(tr, "b"))
			{
				UpdateUpperTitle(tr, pageid, true);
			}
			// content
			if(tName.equals("td"))
			{
				RemoveUpperTitleElement(tags.get(0).tag);
				if(TagChild.containDescendantTag(tags.get(0).tag, "tr"))
					return null;
				if(InfoboxNode.TRTitleNr > 1 && 
						InfoboxNode.UpperTitle != null)
				{
					myObj predi = new myObj();
					predi.addEle(InfoboxNode.UpperTitle);
					myObj objc = ObjeStdz.standardize(new myTag(tags.get(0).tag, true));
					//System.out.println("TripleGenerator.java:\n\t" + SecondStandardize.GetTriples(pageid, predi, objc));
					return GeneratorDistributor.distribute(
							pageid, predi, objc, InfoboxNode.UpperTitle,
							InfoboxNode.UpperTitleMinus, InfoboxNode.TRTitleNr);
				}
			}
		}
		else{
			boolean isListTable = true;
			for(int i = 0 ; i < tags.size(); i ++)
				if(!(tags.get(i).tag.getTagName().equals("TH") ||
						TagChild.containDescendantTag(tags.get(i).tag, "b")))
				{
					isListTable = false;
					break;
				}
			if(isListTable == true || tags.size() >= 3)
				InfoboxNode.ListTable = true;
		}
		return null;
	}
	
	

	private static String UpdateUpperTitle(Tag tr, int pageid,
			boolean forMinusSymbolUpperTitle) {
		// TODO Auto-generated method stub
		InfoboxNode.TRTitleNr ++;
		String cont = uFunc.Simplify(tags.get(0).tag.toPlainTextString());
		if(cont.contains("参战方") || cont.contains("交战方"))
		{
			//System.out.println("battle:" + pageid);
			InfoboxNode.BattelInfo = true;
		}
		// it's the title in infobox, not a subtitle
		myElement tUpperTitle = PredStdz.standardize(tags.get(0).tag, pageid);
		
		// there are some image or format defin on the top
		if(tUpperTitle == null || tUpperTitle.context == null)
		{
			return null;
		}
		// subtitle is not the entity's name
		String pageTitle = Entity.getTitle(pageid);
		if(pageTitle != null && tUpperTitle.context.contains(pageTitle))
			return null;
		if(Entity.getId(tUpperTitle.context) == PageId)
			return null;
		if(forMinusSymbolUpperTitle == false)
			InfoboxNode.UpperTitle = tUpperTitle;
		else{
			InfoboxNode.UpperTitleMinus = tUpperTitle;
			/*
			if(tr.getTagName().equals("TH") == false)
			{
				info = "UpperTitle:" + tUpperTitle.context + "\t" + pageid;
				uFunc.Alert(true, c, info);
			}*/
		}
		
		return null;
	}



	private static void RemoveUpperTitleElement(Tag tag) {
		// TODO Auto-generated method stub
		if(tag.getTagName().equals("DIV"))
		{
			InfoboxNode.UpperTitle = null;
			return;
		}
		Vector<myTag> child = TagChild.getChildren(tag);
		Tag tTag;
		while(child != null && child.size() == 1)
		{
			tTag = child.get(0).tag;
			if(tTag == null)
				return;
			if(tTag != null && tTag.getTagName().equals("DIV"))
			{
				InfoboxNode.UpperTitle = null;
				return;
			}
			child = TagChild.getChildren(tTag);
		}
	}



	private static boolean isNotPredicate(myTag tagPre) {
		// TODO Auto-generated method stub
		int eleNr = 0;
		if(tagPre == null)
			return true;
		Vector<myTag> child = TagChild.getChildren(tagPre.tag);
		if(child == null)
			return true;
		Pattern pat = Pattern.compile("\n");
		for(int i = 0 ; i < child.size(); i ++)
		{
			if(child.get(i).tag == null)
				continue;
			Matcher mat = pat.matcher(child.get(i).tag.toPlainTextString());
			while(mat.find())
			{
				eleNr ++;
			}
			eleNr ++;
		}
		if(eleNr > 1)
		{
			return true;
		}
		return false;
	}



	private static boolean Init(Tag tr) {
		// TODO Auto-generated method stub
		tags.clear();

		if(tr.getTagName().toLowerCase().equals("tr") == false)
		{
			System.out.println("TripleGenerator.java:not a \"tr\" tag");
			return false;
		}
		return true;
	}

}
