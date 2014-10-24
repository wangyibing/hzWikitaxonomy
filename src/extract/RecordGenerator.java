package extract;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;
import org.htmlparser.visitors.NodeVisitor;

import com.tag.TagChild;
import com.tag.myElement;
import com.tag.myObj;
import com.tag.myTag;

import database.Page;
import tools.uFunc;
import triple.standardize.ObjeStdz;
import triple.standardize.PredStdz;

public class RecordGenerator {
	private final static String c = "RecordGenerator";
	private static String info;
	private static Vector<myTag> tags = new Vector<myTag>();
	public static String GenerFromTR(int pageid, Tag tr)
	{
		
		if(TagChild.containDescendantTag(tr, "img"))
		{
			tr.accept(new NodeVisitor(){
				public void visitTag(Tag tag)
				{
					if(tag.getTagName().equals("IMG"))
					{
						String width = tag.getAttribute("WIDTH");
						if(width != null && Integer.parseInt(width) >= 50)
						{
							InfoboxNode.UpperTitle = null;
							InfoboxNode.UpperTitleMinus = null;
							InfoboxNode.TRTitleNr = 0;
							InfoboxNode.infoboxIMG = true;
						}
					}
					return;
				}
			});
		}
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
		String TrClass = tr.getAttribute("CLASS");
		if(TagChild.containDescendantTag(tr, "HR") ||
				(TrClass != null && TrClass.toLowerCase().contains("mergedtoprow")))
		{
			InfoboxNode.UpperTitle = null;
			InfoboxNode.UpperTitleMinus = null;
		}
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
				if((TagChild.containDescendantTag(tagPre.tag, "b") || preName.equals("th"))
						&& (TagChild.containDescendantTag(tagObj.tag, "b") || objName.equals("th")))
				{
					String h1 = uFunc.ReplaceBoundSpace(
							tags.get(0).tag.toPlainTextString());
					String h2 = uFunc.ReplaceBoundSpace(
							tags.get(1).tag.toPlainTextString());
					if(h1.equals("") == false && h2.equals("") == false)
					{
						InfoboxNode.ListTable = true;
						uFunc.Alert(true, c, "listttable");
					}
				}
			}
			switch(TRformat)
			{
			case 1:
				myElement pred = PredStdz.standardize(tagPre, pageid);
				if(pred == null){
					if(InfoboxNode.TRTitleNr > 1 && 
							InfoboxNode.UpperTitle != null)
					{
						pred = InfoboxNode.UpperTitle;
						//uFunc.Alert(true, c, pageid + "\t" + pred.context + ";" + tagObj.context);
					}
					if(pred == null)
						return "";
				}
				myObj predi = new myObj();
				predi.addEle(pred);
				myObj objc = ObjeStdz.standardize(tagObj);
				/*uFunc.Alert(true, c, "");
				predi.OutputEle("");
				objc.OutputEle("");*/
				// normal triple exist, namely subtitle exist
				if(InfoboxNode.infoboxIMG == false &&
						predi != null && objc != null)
				{
					InfoboxNode.infoboxIMG = true;
					InfoboxNode.TRTitleNr ++;
					//System.out.println("TripleGenerator.java:" + InfoboxNode.TRTitleNr);
				}
				//System.out.println("TripleGenerator.java:" + tagPre.outputInfo() + "\t" +  GeneratorDistributor.distribute(pageid, predi, objc, null, 2));
				if(tagPre.tag.getAttribute("BGCOLOR") != null)
				{
					String bc = tagPre.tag.getAttribute("BGCOLOR");
					if(bc.equals("#EEEEEE"))
					{
						return "";
					}
				}
				myElement uN = InfoboxNode.UpperTitle;
				if(uN != null)
				{
					myObj newObj = new myObj();
					String cont = uFunc.Simplify(uN.context.replaceAll("\\[[^\\]]{1,}\\]", ""));
					if(cont.contains("聚集地")|| cont.contains("聚居地")
							|| cont.endsWith("友好城市") || cont.contains("族群")
							|| cont.contains("分布地区") || cont.equals("人种构成")
							|| cont.equals("语言") || cont.equals("主要品种")
							|| cont.equals("仪器") || cont.equals("望远镜") )
					{
						newObj.addEle(predi.eles);
						return TripleFromUT(pageid, newObj);
					}
				}
				if(InfoboxNode.ListTable == true)
				{
					return "";
				}
				String triple = GeneratorDistributor.distribute(
						pageid, predi, objc, InfoboxNode.UpperTitle,
						InfoboxNode.UpperTitleMinus, InfoboxNode.TRTitleNr);
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
				//System.out.println("TripleGenerator.java:IMG" + tags.get(0).tag.toPlainTextString());
			}

			String context =uFunc.Simplify(uFunc.ReplaceBoundSpace(
					tags.get(0).tag.toPlainTextString())); 
			if(context.matches(".+([\\u4e00-\\u9fa5]| |_)+(\\:|：).+") &&
					TagChild.containDescendantTag(tags.get(0).tag, "tr") == false)
			{
				// single tr containing ":"
				//uFunc.Alert(true, c, "single tr:" + context);
				return "";
				//return GeneratorDistributor.distribute(context, pageid, InfoboxNode.UpperTitle, InfoboxNode.TRTitleNr);
			}
			// subTitle
			if(tName.equals("th"))
			{
				return UpdateUpperTitle(tr, pageid, false);
			}
			else if(TagChild.containDescendantTag(tr, "b"))
			{
				UpdateUpperTitle(tr, pageid, false);
			}
			// content
			else if(tName.equals("td"))
			{
				RemoveUpperTitleElement(tags.get(0).tag);
				if(TagChild.containDescendantTag(tags.get(0).tag, "tr"))
					return "";
				myObj objc = ObjeStdz.standardize(new myTag(tags.get(0).tag, true));
				return TripleFromUT(pageid, objc);
				
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
			if(isListTable == true || tags.size() >= 4)
			{
				InfoboxNode.ListTable = true;
			}
		}
		return "";
	}
	
	

	private static String TripleFromUT(int pageid, myObj objc) {
		// TODO Auto-generated method stub
		if(InfoboxNode.TRTitleNr > 1 && 
				InfoboxNode.UpperTitle != null)
		{
			myObj predi = new myObj();
			predi.addEle(InfoboxNode.UpperTitle);
			/*info = InfoboxNode.UpperTitle.context + "\n" + 
					GeneratorDistributor.distribute(pageid, predi, objc, InfoboxNode.UpperTitle, InfoboxNode.UpperTitleMinus, InfoboxNode.TRTitleNr);
			uFunc.Alert(true, c, info);*/
			return GeneratorDistributor.distribute(
					pageid, predi, objc, InfoboxNode.UpperTitle,
					InfoboxNode.UpperTitleMinus, InfoboxNode.TRTitleNr);
		}
		return "";
	}



	private static String UpdateUpperTitle(Tag tr, int pageid,
			boolean forMinusSymbolUpperTitle) {
		InfoboxNode.TRTitleNr ++;
		String style = tr.getAttribute("STYLE");
		if(style != null)
			style = style.toLowerCase();
		if(style == null || !style.contains("lightsteelblue"))
		{
			InfoboxNode.LightBlue = false;
		}
		else{
			InfoboxNode.LightBlue = true;
		}
		String cont = uFunc.Simplify(tags.get(0).tag.toPlainTextString());
		if(cont.contains("参战方") || cont.contains("交战方"))
		{
			//System.out.println("battle:" + pageid);
			InfoboxNode.BattelInfo = true;
		}
		// it's the title in infobox, not a subtitle
		myElement tUpperTitle;
		if(tags.get(0).tag.getTagName().equals("TH"))
			tUpperTitle = PredStdz.standardize(tags.get(0).tag, pageid);
		else{
			String bold = TagChild.GetChildren(tr, "b");
			//System.out.println(bold + "\t" + tr.toHtml());
			if(bold == null)
				return null;
			tUpperTitle = new myElement(bold);
		}
		
		// there are some image or format defin on the top
		if(tUpperTitle == null || tUpperTitle.context == null)
		{
			return null;
		}
		/*info = "th:" + tr.toPlainTextString();
		uFunc.Alert(true, c, info);
		info = "UpperTItle:" + tUpperTitle.context;
		uFunc.Alert(true, c, info);*/
		// subtitle is not the entity's name
		String titles = Page.getTitles(pageid);
		String ut = tUpperTitle.context;
		boolean isEntityName = false;
		if(titles != null)
		{
			for(String title : titles.split("####"))
			{
				if(ut != null && title != null && 
						(ut.contains(title) || title.contains(ut)))
				{
					isEntityName = true;
					break;
				}
			}
		}
		if(isEntityName == true)
			return null;
		if(forMinusSymbolUpperTitle == false && 
				tUpperTitle.context.equals("") == false){
			//uFunc.Alert(true, c, "UppderTitle:" + ut);
			InfoboxNode.UpperTitle = tUpperTitle;
		}
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
		
		tags.clear();

		if(tr.getTagName().toLowerCase().equals("tr") == false)
		{
			System.out.println("TripleGenerator.java:not a \"tr\" tag");
			return false;
		}
		return true;
	}

}
