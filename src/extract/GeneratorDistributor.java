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
		String result = TripleGenerator.GetTriples(
				pageid, predi, objc, upperTitle, tRTitleNr);
		if(result == null || result.equals(""))
			return null;
		if(predi.eleNr == objc.eleNr && objc.eleNr > 1)
		{
			info = pageid + " has multi-predi:";
			for(int i = 0 ; i < predi.eleNr; i ++)
				info += predi.eles.get(i).context + ";" + objc.eles.get(i).context + " ";
			
			uFunc.Alert(i, info);
		}
		PredIdGenerator.generator(predi.eles.get(0).context, pageid, "data/predicatetable/predicateId");
		
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
		
		if(result == null || result.equals(""))
			return null;
		return result;
	}
}
