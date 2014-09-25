package normalization;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

import database.Zhwiki;
import extract.pageinfo.myPredicate;
import extract.pageinfo.myPredicateAvg;
import tools.predicate;
import tools.uFunc;

public class PredAvgExtraction {

	public static void main(String [] args)
	{
		
	}
	static HashMap<String, Integer> Cont2PredseqMap = 
			new HashMap<String, Integer>();
	static myPredicateAvg[] predicates = new myPredicateAvg[30000]; 
	static int predNr = 0;
	public static void Extract(String predicateId2_Normal)
	{
		Zhwiki.init();
		BufferedReader br = uFunc.getBufferedReader(predicateId2_Normal);
		String oneLine = "";
		int predicateNr = 0;
		int predSeq = 1;
		try {
			while((oneLine = br.readLine()) != null)
			{
				long predId = Long.parseLong(oneLine);
				int pageId = Integer.parseInt(
						oneLine.substring(0, oneLine.length() - 3));
				myPredicate pred = new myPredicate(predId, pageId);
				pred.CompleteInfo(br);
				if(Cont2PredseqMap.containsKey(pred.Content))
				{
					int id = Cont2PredseqMap.get(pred.Content);
				}
				else{
					predSeq ++;
					Cont2PredseqMap.put(pred.Content, predSeq);
					predicates[predSeq] = new myPredicateAvg(
							pred.Content, predSeq);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
