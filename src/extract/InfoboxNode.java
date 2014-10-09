package extract;

import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.NodeVisitor;

import com.tag.TagChild;
import com.tag.myElement;

public class InfoboxNode {
	private NodeList infobox = null;
	private int PageId;
	private NodeVisitor InfoboxVisitor;
	private String lastTagName;
	private String lastEndTagName;

	public static myElement UpperTitle;
	public static myElement UpperTitleMinus;
	public static int TRTitleNr;
	public static boolean BattelInfo;
	public static boolean infoboxIMG;
	public static boolean ListTable;
	// album, not triple
	public static boolean LightBlue;
	
	private String outputTriples = "";
	
	public InfoboxNode(int pageid, NodeList nodelist)
	{
		infobox = nodelist;
		PageId = pageid;
		UpperTitle = null;
		UpperTitleMinus = null;
		TRTitleNr = 0;
		BattelInfo = false;
		infoboxIMG = false;
		ListTable =  false;
		LightBlue = false;
		InfoboxVisitor = new NodeVisitor(){
			public void visitTag(Tag tag){
				String tagName = tag.getTagName().toLowerCase();
				if(tagName.equals("tr"))
				{
					boolean isDirectSon = false;
					for(int i = 0 ; i < PageNode.InfoboxTables.size(); i ++)
						if(TagChild.isChild(PageNode.InfoboxTables.get(i), tag))
						{
							isDirectSon = true;
							break;
						}
					if(isDirectSon == false)
						return;
					if(BattelInfo == true){
						return;
					}
					String triples = 
							RecordGenerator.GenerFromTR(PageId, tag);
					if(triples == null)
						return;
					outputTriples += triples;
				}
				else if(tagName.equals("HR"))
				{
					UpperTitle = null;
					UpperTitleMinus = null;
				}
				lastTagName = tagName;
			}
			public void visitEndTag(Tag tag){
				lastEndTagName = tag.getTagName().toLowerCase();
			}
		};
	}
	
	static int totalLineNr = 0;
	public String GetTriples()
	{
		if(infobox == null)
		{
			//System.out.println("infobox is null, can't get triples!");
			return "";
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
