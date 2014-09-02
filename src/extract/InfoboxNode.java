package extract;

import java.util.Stack;

import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.NodeVisitor;

import com.tag.myElement;

public class InfoboxNode {
	private NodeList infobox = null;
	private int PageId;
	private NodeVisitor InfoboxVisitor;
	private String lastTagName;
	private String lastEndTagName;
	private Stack<String> tags;
	
	public static myElement UpperTitle;
	public static int TRTitleNr;
	public static boolean BattelInfo;
	public static boolean infoboxIMG;
	
	private String outputTriples = "";
	
	public InfoboxNode(int pageid, NodeList nodelist)
	{
		infobox = nodelist;
		PageId = pageid;
		tags = new Stack<String>();
		UpperTitle = null;
		TRTitleNr = 0;
		BattelInfo = false;
		infoboxIMG = false;
		InfoboxVisitor = new NodeVisitor(){
			public void visitTag(Tag tag){
				String tagName = tag.getTagName().toLowerCase();
				if(tagName.equals("tr"))
				{
					if(BattelInfo == true){
						return;
					}
					String triples = 
							RecordGenerator.GenerFromTR(PageId, tag);
					if(triples == null)
						return;
					outputTriples += triples;
				}
				lastTagName = tagName;
				tags.push(lastTagName);
			}
			public void visitEndTag(Tag tag){
				lastEndTagName = tag.getTagName().toLowerCase();
				while(tags.empty() == false && 
						tags.peek().equals(lastEndTagName) == false)
					tags.pop();
			}
		};
	}
	
	static int totalLineNr = 0;
	public String GetTriples()
	{
		if(infobox == null)
		{
			//System.out.println("infobox is null, can't get triples!");
			return null;
		}
		//System.out.println("InfoboxNode.java:GetTriples():" + infobox.toHtml() + "InfoboxNode.java:GetTriples end");
		outputTriples = "";
		try {
			infobox.visitAllNodesWith(InfoboxVisitor);
		} catch (ParserException e) {
			e.printStackTrace();
		}
		if(outputTriples.equals(""))
		{
			//System.out.println("InfoboxNode.java:" + PageId + 
			//		" extract triples failed!");
		}
		else
		{
			//System.out.println("\"" + outputTriples + "\"");
		}
		totalLineNr += outputTriples.split("\n").length;
		return outputTriples;
	}

}
