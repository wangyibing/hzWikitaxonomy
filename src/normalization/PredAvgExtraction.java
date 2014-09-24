package normalization;

import java.io.BufferedReader;
import java.util.HashMap;

import extract.pageinfo.myPredicateAvg;
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
		BufferedReader br = uFunc.getBufferedReader(predicateId2_Normal);
		
	}
}
