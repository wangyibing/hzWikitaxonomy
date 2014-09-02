package normalization.predicate;

import java.util.Vector;

import org.htmlparser.Tag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.visitors.NodeVisitor;

import com.tag.TagChild;
import com.tag.myElement;
import com.tag.myTag;

import tools.uFunc;
import triple.extract.TripleGenerator;
import triple.predicate.PredStdz;

public class linkExtract {
	private static NodeVisitor Extractor; 
	private static Vector<myTag> sons;
	private static myElement predicate;
	private static String result = "";
	private static int PageId;
	
	public static String Extract(int pageid, NodeList infobox)
	{
		init(pageid);
		try {
			infobox.visitAllNodesWith(Extractor);
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	private static void init(int pageid2) {
		// TODO Auto-generated method stub
		PageId = pageid2;
		result = "";
		if(Extractor == null)
		{
			Extractor = new NodeVisitor(){

				public void visitTag(Tag tag){
					if(tag.getTagName().toLowerCase().equals("tr"))
					{
						sons = TagChild.getChildren(tag);
						if(sons.size() == 2 &&
								sons.get(0).tag.getTagName().equals("TH"))
						{
							predicate = PredStdz.standardize(sons.get(0).tag, pageid2);
							if(predicate != null && predicate.link != null)
							{
								result += TripleGenerator.getStringFromMyelement(predicate, true)
										+ "\t" + uFunc.ReplaceBoundSpace(predicate.context)
										+ "\t" + PageId + "\n";
							}
						}
					}
				}
			};
		}
			
	}
}
