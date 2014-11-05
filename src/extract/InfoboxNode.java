package extract;

import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.NodeVisitor;

import tools.uFunc;

import com.tag.TagChild;
import com.tag.myElement;

public class InfoboxNode {
	private NodeList infobox = null;
	private int PageId;
	private NodeVisitor InfoboxVisitor;
	//private String lastTagName;
	//private String lastEndTagName;

	public static myElement UpperTitle;
	public static myElement UpperTitleMinus;
	public static int TRTitleNr;
	public static boolean BattelInfo;
	public static boolean infoboxIMG;
	public static boolean ListTable;
	// album, not triple
	public static boolean LightBlue;
	public static boolean SHUTDOWN;
	private static String con;
	
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
		SHUTDOWN = false;
		con = "";
		if(infobox != null && infobox.asString() != null)
			con = uFunc.Simplify(infobox.asString().replaceAll("\\s", ""));
		if(con.startsWith("站点和里程"))
			SHUTDOWN = true;
		InfoboxVisitor = new NodeVisitor(){
			public void visitTag(Tag tag){
				//uFunc.Alert(true, SHUTDOWN + "", tag.toPlainTextString());
				if(SHUTDOWN || LightBlue)
				{
					return;
				}
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
					if(triples == null || triples.equals(""))
						return;
					outputTriples += triples;
				}
				else if(tagName.equals("HR"))
				{
					UpperTitle = null;
					UpperTitleMinus = null;
				}
				else if(tagName.equals("caption"))
				{
					if(con.contains("发现") && con.contains("小行星"))
						SHUTDOWN = true;
				}
				else if(tagName.equals("HR"))
				{
					UpperTitle = null;
					UpperTitleMinus = null;
				}
				//lastTagName = tagName;
			}
			public void visitEndTag(Tag tag){
				//lastEndTagName = tag.getTagName().toLowerCase();
			}
		};
	}
	
	static int totalLineNr = 0;
	public String GetTriples()
	{
		if(infobox == null)
		{
			return "";
		}
		outputTriples = "";
		try {
			infobox.visitAllNodesWith(InfoboxVisitor);
		} catch (ParserException e) {
			e.printStackTrace();
		}
		totalLineNr += outputTriples.split("\n").length;
		return outputTriples;
	}

}
