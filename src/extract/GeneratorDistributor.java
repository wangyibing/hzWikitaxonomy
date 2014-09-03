package extract;

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
		if(result == null || result.equals(""))
			return null;
		if(predi.eleNr == objc.eleNr && objc.eleNr > 1)
		{
			info = pageid + " has multi-predi:\n";
			for(int i = 0 ; i < predi.eleNr; i ++)
				info += "\t" + predi.eles.get(i).context + ";" + objc.eles.get(i).context + "\n";
			uFunc.Alert(i, info);
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
			result = TripleGenerator.GetTriples(
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
		result = Distribute2Multi(pageid, predi, objc, upperTitle, tRTitleNr);
		if(result.equals(""))
			return null;
		return result;
	}
	

	private static String Distribute2Multi(int pageid, myObj predi,
			myObj objc, myElement upperTitle, int tRTitleNr) {
		// TODO Auto-generated method stub
		String result = TripleGenerator.GetTriples(
				pageid, predi, objc, upperTitle, tRTitleNr);
		if(result != null)
		{
			PredIdGenerator.generator(predi, pageid, 
					"data/predicatetable/predicateId", result);
			
		}
		return result;
	}

}