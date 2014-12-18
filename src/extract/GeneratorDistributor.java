package extract;

import java.util.HashMap;

import org.htmlparser.Tag;

import tools.uFunc;
import triple.standardize.ObjeStdz;

import com.tag.myElement;
import com.tag.myObj;

import extract.predicatetable.PredIdGenerator;
import extract.triple.TripleGenerator;
import extract.webpageprocess.InfoboxNode;

public class GeneratorDistributor {
	static String i = "GeneratorDistributor";
	static String info;
	

	public static String distribute(int pageid, myObj predi, myObj objc,
			myElement upperTitle, myElement upperTitleMinus, int tRTitleNr,
			Tag objTag, int tRtagId) {
		// TODO Auto-generated method stub
		String result = "";
		if(predi == null || objc == null)
				return null;
		// has multi-predi, e.g.
		// pred = {身高, 体重} 
		// objc = {162 厘米, 44 公斤}
		if(predi.eleNr == objc.eleNr && objc.eleNr > 1)
		{
			String oneLine = "";
			info = "";
			if(objc.eleNr == 2)
			{
				for(int i = 0 ; i < predi.eleNr; i ++)
				{
					info += predi.eles.get(i).context + "$$$";
					myObj tPred = new myObj();
					tPred.addEle(predi.eles.get(i));
					myObj tObjc = new myObj();
					tObjc.addEle(objc.eles.get(i));
					/////////////////////////////////////////////////////
					oneLine = Distribute2Multi(
							pageid, tPred, tObjc, upperTitle, 
							upperTitleMinus, tRTitleNr, objTag, tRtagId);
					if(oneLine != null)
						result += oneLine;
					/////////////////////////////////////////////////////
				}
			}
			else{
				myObj tPred = new myObj();
				myObj tObjc = new myObj();
				tPred.addEle(predi.eles.get(0));
				for(int i = 1 ; i < predi.eleNr; i ++)
					tObjc.addEle(predi.eles.get(i));
				result += Distribute2Multi(
						pageid, tPred, tObjc, upperTitle, 
						upperTitleMinus, tRTitleNr, objTag, tRtagId);
				tPred = new myObj();
				tObjc = new myObj();
				tPred.addEle(objc.eles.get(0));
				for(int i = 1 ; i < objc.eleNr; i ++)
					tObjc.addEle(objc.eles.get(i));
				result += Distribute2Multi(
						pageid, tPred, tObjc, upperTitle, 
						upperTitleMinus, tRTitleNr, objTag, tRtagId);
				
			}
			//uFunc.Alert(true, i, "\n" + result);
		}
		else
		{
			/////////////////////////////////////////////////////
			result = Distribute2Multi(
					pageid, predi, objc, upperTitle, 
					upperTitleMinus, tRTitleNr, objTag, tRtagId);
			/////////////////////////////////////////////////////
			
		}
		if(result == null || result.equals(""))
			return null;
		return result;
	}

	/**
	 * single td can generate a triple:
	 * such as :"经纬度：32E -100N"
	 * @param context
	 * @param pageid
	 * @param tRTitleNr 
	 * @param upperTitle 
	 * @return
	 */
	static HashMap<String, Integer> prediInSingle = 
			new HashMap<String, Integer>();
	public static String distribute(String context, int pageid, 
			myElement upperTitle, myElement upperTitleMinus, int tRTitleNr, 
			Tag objTag, int tRtagId) {
		// TODO Auto-generated method stub
		String result = "";
		if(context.split(":|：").length < 2){
			return null;
		}
		int index = context.indexOf(":");
		if(index < 0 || (index > context.indexOf("：") 
				&& context.indexOf("：") > 0))
			index = context.indexOf("：");
		String pString = uFunc.
				ReplaceBoundSpace(context.substring(0, index));
		String oString = uFunc.
				ReplaceBoundSpace(context.substring(index + 1));
		myObj predi, objc;
		predi = new myObj();
		predi.addEle(new myElement(pString));
		objc = new myObj();
		for(String ss : oString.split(ObjeStdz.splitRegex))
			objc.addEle(new myElement(ss));
		result = Distribute2Multi(pageid, predi, objc, 
				upperTitle, upperTitleMinus, tRTitleNr, objTag, tRtagId);
		if(result == null || result.equals(""))
			return null;
		if(InfoboxNode.LightBlue)
		{
			info = "lightblue:\n" + result;
		}
		return result;
	}
	

	private static String Distribute2Multi(int pageid, myObj predi,
			myObj objc, myElement upperTitle, myElement upperTitleMinus,
			int tRTitleNr, Tag objTag, int tRtagId) {
		// TODO Auto-generated method stub
		String result = TripleGenerator.GetTriples(
				pageid, predi, objc, upperTitle, upperTitleMinus, 
				tRTitleNr, objTag, tRtagId);
		//uFunc.Alert(true, i, result);
		if(result != null && result.equals("") == false)
		{
			PredIdGenerator.generator(pageid, 
					"/home/hanzhe/Public/result_hz/wiki_count2/predicate/predicateId", upperTitle, result);
		}
		// else is considered
		if(result == null || result.equals(""))
			return null;
		return result;
	}

}
