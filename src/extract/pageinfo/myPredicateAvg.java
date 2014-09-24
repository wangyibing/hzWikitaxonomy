package extract.pageinfo;

import java.util.HashMap;


public class myPredicateAvg {

	String Content;
	String Pinyin;
	float [] vectorW2V;
	HashMap<Integer, Integer> Links;
	// <entityid, freq>
	HashMap<Integer, Double> SubjectCateDistr;
	HashMap<Integer, Double> PredCateDistr;
	HashMap<Integer, Double> ObjCateDistr;
	// 
	HashMap<String, Double> UpperTitles;
	HashMap<String, Double> InfoboxNames;
	HashMap<String, Double> WikitextContents;
	HashMap<String, Double> ObjTypes;
	
	myPredicateAvg(String cont)
	{
		Content = cont;
		Pinyin = null;
		vectorW2V = null;
		Links = new HashMap<Integer, Integer>();
		SubjectCateDistr = new HashMap<Integer, Double>();
		PredCateDistr = new HashMap<Integer, Double>();
		ObjCateDistr = new HashMap<Integer, Double>();
		UpperTitles = new HashMap<String, Double>();
		InfoboxNames = new HashMap<String, Double>();
		WikitextContents = new HashMap<String, Double>();
		ObjTypes = new HashMap<String, Double>();
	}
}
