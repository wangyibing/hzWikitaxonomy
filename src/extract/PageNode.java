package extract;

import java.util.Vector;

import normalization.PredExtraction;

import org.htmlparser.Remark;
import org.htmlparser.Tag;
import org.htmlparser.Text;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.NodeVisitor;

import com.tag.dedup;

import database.Entity;
import database.InfoboxNameList;
import tools.uFunc;


public class PageNode {
	private NodeList Page = null;
	private int PageId;
	private static NodeVisitor PageNodeVistor;
	private boolean TextApprd = false;
	private boolean inMvContentText = false;
	private Tag lastEndTag;
	private String lastEndTagName;
	private String lastTagName;
	private String triples = "";
	private Vector<String> possiTableNames;
	private Vector<Tag> InfoboxTag;
	private boolean hasMedalInfo;
	private String info;
	private String i = "PageNode";
	
	/**
	 * 1:extract triples
	 * 2:extract upper titles
	 */
	public static int Mode = 1;
	
	
	public String GetTriples(boolean alert)
	{
		if(Page == null)
		{
			System.out.println("Page is null, can't get infobox!");
			return "";
		}
		try {
			Page.visitAllNodesWith(PageNodeVistor);
		} catch (ParserException e) {
			e.printStackTrace();
		}
		if(triples.equals(""))
		{
			if(hasMedalInfo == true)
				return "";
			info = "FailedExtractPages:" + PageId;
			if(alert)
				uFunc.Alert(i , info);
			return "";
		}
		else
		{
			return triples;
		}
	}
	

	public PageNode(int pageid, NodeList nodelist)
	{
		Page = nodelist;
		PageId = pageid;
		triples = "";
		hasMedalInfo = false;
		possiTableNames = new Vector<String>();
		InfoboxTag = new Vector<Tag>();
		PageNodeVistor = new NodeVisitor(){
			public void visitTag(Tag tag) {
				if(TextApprd == true)
					return;
				if(tag.getTagName().toLowerCase().equals("div") &&
						tag.getAttribute("ID") != null)
				{
					if(tag.getAttribute("ID").toLowerCase()
							.endsWith("mw-content-text"))
					{
						inMvContentText = true;
					}
				}
				// summary exist, namely, infobox search end
				if(tag.getTagName().toLowerCase().equals("div"))
				{
					if(tag.getAttribute("ID") != null &&
							tag.getAttribute("ID").toLowerCase().equals("toc") &&
							tag.getAttribute("CLASS") != null &&
							tag.getAttribute("CLASS").toLowerCase().equals("toc"))
					{
						TextApprd = true;
						//System.out.println("summary exist!");
					}
				}
				// content begin
				if(lastEndTag != null &&
						tag.getTagName().toLowerCase().equals("p"))
				{
					//System.out.println("PageNode.java:" + lastEndTagName + "\n" + tag.toHtml());
					if(lastEndTagName.equals("table") ||
							lastEndTagName.equals("div"))
					{
						String title = Entity.getTitle(pageid);
						if(title != null && uFunc.hasChineseCharactor(tag.toPlainTextString())){
							//System.out.println("#######firs para:" + tag.toPlainTextString());
							TextApprd = true;
						}
						else{
							
						}
						
						String para = uFunc.Simplify(tag.toPlainTextString()); 
						if(para == null || para.contains("页面不存在") 
								|| para.startsWith("本文")
								|| para.startsWith("坐标") || para.equals("")){
							TextApprd = false;
							//System.out.println("content not exist!");
						}
					}
					else
					{
						//System.out.println("PageNode.java:" + "lastEndTag:" + lastEndTag);
					}
				}
				
				// find infobox
				if(tag.getTagName().toLowerCase().equals("table"))
				{
					String tableClass = tag.getAttribute("CLASS");
					if(tableClass != null && tableClass.toLowerCase().contains("navbox"))
					{
						//System.out.println("PageNode.java: nav box!" + pageid + "\n" + tag.toPlainTextString());
						return;
					}
					// sub-tables in td
					if(lastTagName != null && lastEndTagName != null && 
							lastTagName.equals("td") && lastEndTagName.equals("td") == false)
					{
						uFunc.Alert(i, "PageNode.java: tables in td!" + pageid);
						return;
					}
					for(int i = 0 ; i < InfoboxTag.size(); i ++)
					{
						if(dedup.isFather(InfoboxTag.get(i), tag) == true)
							return;
					}
					//uFunc.OutputTagInfo(tag, "PageNode.java:table:");
					String tS = uFunc.Simplify(tag.toPlainTextString());
					if(tS.contains("金牌") || tS.contains("银牌") || tS.contains("铜牌"))
						hasMedalInfo = true;
					if(tag.getAttribute("CLASS") != null)
					{
						String possiName = uFunc.ReplaceBoundSpace(
								tag.getAttribute("CLASS").toLowerCase());
						possiTableNames.add(possiName);
						if(InfoboxNameList.isInfoboxName(possiName))
						{
							// is infobox
							switch(Mode)
							{
							case 1:
								InfoboxNode infobox = new InfoboxNode(PageId, tag.getChildren());
								triples += infobox.GetTriples();
								InfoboxTag.add(tag);
								break;
							case 2:
								PredExtraction.Extract(PageId, tag.getChildren());
							}
							
						}
					}
					else
					{
						if(inMvContentText == false)
							return;
						switch(Mode)
						{
						case 1:
							InfoboxNode infobox = new InfoboxNode(PageId, tag.getChildren());
							triples += infobox.GetTriples();
							InfoboxTag.add(tag);
							break;
						case 2:
							PredExtraction.Extract(PageId, tag.getChildren());
						}
					}
					
				}
				
				
				
			}
			public void visitStringNode (Text string){}
			public void visitRemarkNode (Remark remark){}
			public void beginParsing(){
				TextApprd = false;
				inMvContentText = false;
			}
			public void visitEndTag (Tag tag){
				lastEndTag = tag;
				lastEndTagName = tag.getTagName().toLowerCase();
				//System.out.println("PageNode.java:" + "lastEndTag:" + lastEndTag.getTagName());
				if(lastEndTagName.equals("div") && tag.getAttribute("ID") != null)
				{
					if(tag.getAttribute("ID").endsWith("mw-content-text") == true)
					{
						inMvContentText = false;
					}
				}
			}
			public void finishedParsing(){}
		};
	}

}
