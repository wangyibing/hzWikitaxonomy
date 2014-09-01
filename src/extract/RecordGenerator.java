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
import triple.extract.TripleGenerator;
import triple.object.ObjeStdz;
import triple.predicate.PredStdz;

public class RecordGenerator {
	private static Vector<myTag> tags = new Vector<myTag>();
	private static int PageId ;
	public static String GenerFromTR(int pageid, Tag tr)
	{
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
				else TRformat = 0;
				
			}
			
			switch(TRformat)
			{
			case 1:
				myObj predi = ObjeStdz.standardize(tagPre);
				myObj objc = ObjeStdz.standardize(tagObj);
				if(InfoboxNode.infoboxIMG == false &&
						predi != null && objc != null)
				{
					InfoboxNode.infoboxIMG = true;
					InfoboxNode.TRTitleNr ++;
					//System.out.println("TripleGenerator.java:" + InfoboxNode.TRTitleNr);
				}
				//System.out.println("TripleGenerator.java:" + tagPre.outputInfo() + "\t" + SecondStandardize.GetTriples(pageid, predi, objc, null));
				return TripleGenerator.GetTriples(pageid, predi, objc, InfoboxNode.UpperTitle, InfoboxNode.TRTitleNr);
			case 2:
				
			}
		}
		else if(tags.size() == 1)
		{
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
				//System.out.println("TripleGenerator.java:single tr containing \":\", " + pageid + ":" + context);
				TripleGenerator.PageId = pageid;
				return TripleGenerator.getTripleFromSgl(context, pageid);
			}
			// subTitle
			if(tName.equals("th"))
			{
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
				String pageTitle = Entity.getEntityTitle(pageid);
				if(pageTitle != null && tUpperTitle.context.contains(pageTitle))
					return null;
				if(Entity.getEntityId(tUpperTitle.context) == PageId)
					return null;
				InfoboxNode.UpperTitle = tUpperTitle;
			}
			// content
			else if(tName.equals("td"))
			{
				RemoveUpperTitleElement(tags.get(0).tag);
				if(TagChild.containDescendantTag(tags.get(0).tag, "tr"))
					return null;
				if(InfoboxNode.TRTitleNr > 1)
				{
					myObj predi = new myObj();
					predi.addEle(InfoboxNode.UpperTitle);
					myObj objc = ObjeStdz.standardize(new myTag(tags.get(0).tag, true));
					//System.out.println("TripleGenerator.java:\n\t" + SecondStandardize.GetTriples(pageid, predi, objc));
					return TripleGenerator.GetTriples(pageid, predi, objc, InfoboxNode.UpperTitle, InfoboxNode.TRTitleNr);
				}
				else if(InfoboxNode.TRTitleNr == 1)
				{
					//if(InfoboxNode.UpperTitle != null)
					//	System.out.println("TripleGenerator.java:not a stand sub-title:" + pageid + "\t" + InfoboxNode.UpperTitle.context + "&&&&" + tags.get(0).context);
				}
			}
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
