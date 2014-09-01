package triple.predicate;

import org.htmlparser.Tag;

import com.tag.myElement;
import com.tag.myTag;

import database.Entity;
import tools.uFunc;
import triple.standardize.HTMLStdz;

public class PredStdz {


	public static myElement standardize(Tag tag, int pageid)
	{
		myTag m = new myTag(tag);
		return standardize(m, pageid);
	}

	public static myElement standardize(myTag mtag_pred, int pageid)
	{
		String result = "";
		result = standardize(mtag_pred.tag.toPlainTextString());
		// predicate is current entity's title, 
		// mustn't be a correct triple
		if(Entity.getEntityId(result) == pageid)
		{
			//System.out.println("PredStdz.java:" + result + ";" + pageid);
			return null;
		}
		//System.out.println("PredStdz.java:result:" + result);
		if(uFunc.isPunctuations(result))
		{
			//System.out.println("PredStdz.java:empty predi:" + mtag_pred.tag.toPlainTextString());
			return null;
		}
		myElement e = new myElement(result);
		return e;
	}

	private static String standardize(String plainTextString) {
		// TODO Auto-generated method stub
		String result = HTMLStdz.standardize(plainTextString);
		result = result
				// replace the ":" in the end
				.replaceAll("(:$)|(：$)", "");
		return result;
	}
}
