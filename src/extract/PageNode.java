package extract;

import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;




import org.htmlparser.Remark;
import org.htmlparser.Tag;
import org.htmlparser.Text;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.NodeVisitor;

import com.tag.dedup;

import database.Entity;
import database.Infobox;
import tools.uFunc;


public class PageNode {
	private static NodeVisitor PageNodeVistor;
	public static HashMap<String, Integer> classAttrName = 
			new HashMap<String, Integer>();
	public static Vector<Tag> InfoboxTables;
	
	
	private NodeList Page = null;
	private int PageId;
	private boolean TextApprd = false;
	private boolean inMvContentText = false;
	private Tag lastEndTag;
	private String lastEndTagName;
	private String lastTagName;
	private String triples = "";
	public boolean hasMedalInfo;
	public String info;
	private String i = "PageNode";
	private Stack<Boolean> FakeDiv;
	
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
		InfoboxTables = new Vector<Tag>();
		FakeDiv = new Stack<Boolean>();
		PageNodeVistor = new NodeVisitor(){
			public void visitTag(Tag tag) {
				String tagName = tag.getTagName().toLowerCase();
				if(tagName.equals("div"))
					FakeDiv.push(uFunc.HasAttriCompnt(tag, "class", "nounderlines"));
				if(TextApprd == true)
					return;

				if(tagName.equals("div"))
				{
					if(uFunc.HasAttriCompnt(tag, "ID", "mw\\-content\\-text"))
						inMvContentText = true;
					// summary exist, namely, infobox search end
					if(uFunc.HasAttriCompnt(tag, "ID", "toc") ||
							uFunc.HasAttriCompnt(tag, "CLASS", "toc"))
						TextApprd = true;
				}
				// content begin
				if(lastEndTag != null && tagName.equals("p"))
				{
					//System.out.println("PageNode.java:" + lastEndTagName + "\n" + tag.toHtml());
					if(lastEndTagName.equals("table") ||
							lastEndTagName.equals("div"))
					{
						String title = Entity.getTitles(pageid);
						if(title != null && uFunc.hasChineseCharactor(tag.toPlainTextString())){
							//System.out.println("#######firs para:" + tag.toPlainTextString());
							TextApprd = true;
						}
						
						String para = uFunc.Simplify(tag.toPlainTextString()); 
						if(para == null || para.contains("页面不存在") 
								|| para.startsWith("本文")
								|| para.startsWith("坐标") || para.equals("")){
							TextApprd = false;
							//System.out.println("content not exist!");
						}
					}
				}
				
				// find infobox
				if(tagName.equals("table"))
				{
					if(FakeDiv.size() > 0 && FakeDiv.peek())
					{
						InfoboxTables.add(tag);
						return;
					}
					String triple = null;
					if(uFunc.HasAttriCompnt(tag, "class", "navbox")
							|| uFunc.HasAttriCompnt(tag, "summary", "sidebar"))
					{
						InfoboxTables.add(tag);
						return;
					}
					// sub-tables in td
					if(lastTagName != null && lastEndTagName != null && 
							lastTagName.equals("td") && lastEndTagName.equals("td") == false)
					{
						uFunc.Alert(i, "PageNode.java: tables in td!" + pageid);
						InfoboxTables.add(tag);
						return;
					}
					for(int i = 0 ; i < InfoboxTables.size(); i ++)
					{
						if(dedup.isFather(InfoboxTables.get(i), tag) == true)
						{
							InfoboxTables.add(tag);
							return;
						}
					}
					String tS = uFunc.Simplify(tag.toPlainTextString());
					if(tS.contains("金牌") || tS.contains("银牌") || tS.contains("铜牌"))
						hasMedalInfo = true;
					if(tag.getAttribute("CLASS") != null)
					{
						String possiName = uFunc.ReplaceBoundSpace(
								tag.getAttribute("CLASS").toLowerCase());
						
						if(Infobox.isInfoboxName(possiName))
						{
							// is infobox
							switch(Mode)
							{
							case 1:
								InfoboxTables.add(tag);
								InfoboxNode infobox = new InfoboxNode(PageId, tag.getChildren());
								triple = infobox.GetTriples();
								triples += triple;
								if(triple.equals("") == false)
								{
									int freq = 1;
									if(classAttrName.containsKey(possiName))
										freq += classAttrName.remove(possiName);
									classAttrName.put(possiName, freq);
								}
								if(triple.equals("") == false && 
										(possiName.contains("metadata")))
								{
									info = pageid + "\t" + possiName + "\n" + infobox.GetTriples();
									uFunc.Alert(true, i, info);
								}
								break;
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
							InfoboxTables.add(tag);
							InfoboxNode infobox = new InfoboxNode(PageId, tag.getChildren());
							triple= infobox.GetTriples();
							triples += triple;
							break;
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
				if(lastEndTagName.equals("div"))
					if(uFunc.HasAttriCompnt(tag, "ID", "mw\\-content\\-text"))
						inMvContentText = false;
				if(lastEndTagName.equals("div"))
					FakeDiv.pop();
			}
			public void finishedParsing(){}
		};
	}

}
