package extract;

import java.util.HashMap;

import tools.predicate;
import tools.uFunc;
import triple.extract.TripleGenerator;
import triple.object.ObjeStdz;

import com.tag.myElement;
import com.tag.myObj;

import extract.predicatetable.PredIdGenerator;

public class GeneratorDistributor {
	static String i = "GeneratorDistributor";
	static String info;

	public static String distribute(int pageid, myObj predi, myObj objc,
			myElement upperTitle, int tRTitleNr) {
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
			for(int i = 0 ; i < predi.eleNr; i ++)
			{
				myObj tPred = new myObj();
				tPred.addEle(predi.eles.get(i));
				myObj tObjc = new myObj();
				tObjc.addEle(objc.eles.get(i));
				/////////////////////////////////////////////////////
				oneLine = Distribute2Multi(
						pageid, tPred, tObjc, upperTitle, tRTitleNr);
				if(oneLine != null)
					result += oneLine;
				/////////////////////////////////////////////////////
			}
		}
		else
		{
			/////////////////////////////////////////////////////
			result = Distribute2Multi(
					pageid, predi, objc, upperTitle, tRTitleNr);
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
	public static String distribute(String context, int pageid, myElement upperTitle, int tRTitleNr) {
		// TODO Auto-generated method stub
		String result = "";
		if(context.split(":|：").length < 2){
			uFunc.Alert(i, "single triple distribute failed:" + context);
			return null;
		}
		int index = context.indexOf(":");
		if(index < 0 || (index > context.indexOf("：") 
				&& context.indexOf("：") > 0))
			index = context.indexOf("：");
		String pString = uFunc.UnifiedSentenceZh2En(context.substring(0, index));
		if(pString.contains("("))
			pString = pString.substring(0, pString.indexOf("("));
		pString = uFunc.ReplaceBoundSpace(pString);
		int freq = predicate.PredFreq(pString);
		if(prediInSingle.containsKey(pString) == false)
		{
			//info = pString + "\t" + pageid + "\t" + freq + "\t" + context;
			//uFunc.Alert(true, i, info);
			prediInSingle.put(pString, freq);
		}
		
		String oString = uFunc.
				ReplaceBoundSpace(context.substring(index + 1));
		myObj predi, objc;
		predi = new myObj();
		predi.addEle(new myElement(pString));
		objc = new myObj();
		for(String ss : oString.split(ObjeStdz.splitRegex))
			objc.addEle(new myElement(ss));
		result = Distribute2Multi(pageid, predi, objc, upperTitle, tRTitleNr);
		if(result == null || result.equals(""))
			return null;
		return result;
	}
	

	private static String Distribute2Multi(int pageid, myObj predi,
			myObj objc, myElement upperTitle, int tRTitleNr) {
		// TODO Auto-generated method stub
		String result = TripleGenerator.GetTriples(
				pageid, predi, objc, upperTitle, tRTitleNr);
		//System.out.println(result);
		if(result != null && result.equals("") == false)
		{
			PredIdGenerator.generator(pageid, 
					"data/predicatetable/predicateId", upperTitle, result);
		}
		// else is considered
		if(result == null || result.equals(""))
			return null;
		return result;
	}

}
