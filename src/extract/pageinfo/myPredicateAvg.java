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
	
}
