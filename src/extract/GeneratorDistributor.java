package extract;

import tools.uFunc;
import triple.extract.TripleGenerator;

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
				/**
				 * oneLine
				 */
				oneLine = TripleGenerator.GetTriples(
						pageid, tPred, tObjc, upperTitle, tRTitleNr);
				if(oneLine != null){
					result += oneLine;
					PredIdGenerator.generator(predi, pageid, 
							"data/predicatetable/predicateId");
				}
			}
		}
		else
		{
			/**
			 * oneLine
			 */
			result = TripleGenerator.GetTriples(
					pageid, predi, objc, upperTitle, tRTitleNr);
			PredIdGenerator.generator(predi, pageid, 
					"data/predicatetable/predicateId");
		}
		if(result == null || result.equals(""))
			return null;
		result += "\n";
		
		return result;
	}

	/**
	 * single td can generate a triple:
	 * such as :"经纬度：32E -100N"
	 * @param context
	 * @param pageid
	 * @return
	 */
	public static String distribute(String context, int pageid) {
		// TODO Auto-generated method stub
		String result = TripleGenerator.
				getTripleFromSgl(context, pageid);
		/**
		 * oneLine
		 */
		PredIdGenerator.generator(context, pageid,
				"data/predicatetable/predicateId");
		if(result == null || result.equals(""))
			return null;
		else
		{
			PredIdGenerator.generator(result.split("\t")[1], pageid,
					"data/predicatetable/predicateId");
			result += "\n";
		}
		return result;
	}
}
